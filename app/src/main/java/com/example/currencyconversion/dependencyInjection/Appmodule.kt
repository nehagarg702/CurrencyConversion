package com.example.currencyconversion.dependencyInjection

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.currencyconversion.utils.Constants
import com.example.currencyconversion.utils.UtilityMethods
import com.example.currencyconversion.viewmodel.ViewModelFactory
import com.example.currencyconversion.database.CurrencyDao
import com.example.currencyconversion.database.CurrencyRateDao
import com.example.currencyconversion.database.Db
import com.example.currencyconversion.network.ApiInterface
import com.example.currencyconversion.repository.CurrencyConversionRepository
import com.example.currencyconversion.repository.CurrencyConversionRepositoryImpl
import com.example.currencyconversion.viewmodel.CurrencyConversionViewModel
import com.example.currencyconversion.workManager.CurrencyConversionWorkManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


val appModule = module {
    single { provideWorkerManager(get(named("context")), get()) }
    single { provideCurrencyDao(get()) }
    single { provideCurrencyRateDao(get()) }
    single { provideCurrencyConversionDb(get(named("context"))) }
    single { provideRandomUserRepository(get(), get(), get()) }
    single { ViewModelFactory(get()) }
    viewModel { CurrencyConversionViewModel(get()) }
    single { getApiService(get()) }
    single { getRetrofit(get(), get()) }
    single { getGsonConvertorFactory(get()) }
    single { getGson() }
    single { getCache(get()) }
    single(named("application")) { androidApplication() }
    single(named("context")) { get<Application>(named("application")).applicationContext }
    single { getFile(get(named("context"))) }
    single(named("loggingInterceptor")) { getLoggingInterceptor() }
    single(named("offlineInterceptor")) { offlineCacheInterceptor() }
    single(named("responseInterceptor")) { responseCacheInterceptor() }
    single { provideOkHttpClient(get(named("loggingInterceptor")), get(named("offlineInterceptor")), get(named("responseInterceptor")), get()) }
}

fun provideWorkerManager(context: Context, currencyConversionRepository: CurrencyConversionRepository) : Operation {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val periodicWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest
        .Builder(CurrencyConversionWorkManager::class.java, 30, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .setInputData(workDataOf("repository" to currencyConversionRepository ))
        .build()
    return WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork("CurrencyConversionTask",
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest
    )
}

fun provideRandomUserRepository(apiInterface: ApiInterface, currencyRateDao: CurrencyRateDao, currencyDao: CurrencyDao ): CurrencyConversionRepository {
    return CurrencyConversionRepositoryImpl(apiInterface, currencyRateDao, currencyDao)
}

fun provideCurrencyRateDao(database : Db): CurrencyRateDao {
    return database.getCurrencyRateDao()
}

fun provideCurrencyDao(database : Db): CurrencyDao {
    return database.getCurrencyDao()
}

fun provideCurrencyConversionDb(context: Context): Db {
    return Room.databaseBuilder(context, Db::class.java, "CurrencyConversionDb").build()
}

fun getApiService(retrofit: Retrofit): ApiInterface {
    return retrofit.create(ApiInterface::class.java)
}

fun getRetrofit(httpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory) : Retrofit {
    return Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(gsonConverterFactory)
        .baseUrl(Constants.BASE_URL)
        .build()
}

fun getGsonConvertorFactory(gson : Gson) : GsonConverterFactory {
    return GsonConverterFactory.create(gson)
}

fun getGson() : Gson {
    return GsonBuilder().create()
}

fun provideOkHttpClient(
  loggingInterceptor: Interceptor,
    offlineInterceptor: Interceptor,
 responseInterceptor: Interceptor,
    cache: Cache
): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(offlineInterceptor)
        .addNetworkInterceptor(responseInterceptor)
        .cache(cache)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}


fun getFile(context: Context): File {
    return File(context.cacheDir, "HttpCache")
}

fun getCache(cacheFile: File): Cache {
    return Cache(cacheFile, 10 * 1024 * 1024)
}

fun offlineCacheInterceptor(): Interceptor {
    return Interceptor {
        if (!UtilityMethods.isNetworkAvailable()) {
            it.request()
                .newBuilder()
                .removeHeader("Pragma")
                .header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=" + 7 * 24 * 60 * 60
                )
                .build()
        }
        it.proceed(it.request())
    }
}

fun responseCacheInterceptor(): Interceptor {
    return Interceptor {
        it.proceed(it.request())
            .newBuilder()
            .removeHeader("Pragma")
            .header("Cache-Control", "public, max-age=" + 60)
            .build()
    }
}

fun getLoggingInterceptor(): Interceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
}

