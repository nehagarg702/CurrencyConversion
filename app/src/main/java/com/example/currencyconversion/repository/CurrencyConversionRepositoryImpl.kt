package com.example.currencyconversion.repository

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.currencyconversion.database.CurrencyDao
import com.example.currencyconversion.database.CurrencyRateDao
import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.model.CurrencyRate
import com.example.currencyconversion.model.toCurrencyRates
import com.example.currencyconversion.network.ApiInterface
import com.example.currencyconversion.network.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class CurrencyConversionRepositoryImpl(
    private val apiInterface: ApiInterface,
    private val currencyRateDao: CurrencyRateDao,
    private val currencyDao: CurrencyDao
) : CurrencyConversionRepository {

    private val _currencyList = mutableStateOf<NetworkResult<List<Currency>>>(NetworkResult.Loading())
    override val currencyList: State<NetworkResult<List<Currency>>>
        get() = _currencyList

    private val _currencyRateList = mutableStateOf<NetworkResult<List<CurrencyRate>>>(NetworkResult.Loading())
    override val currencyRateList: State<NetworkResult<List<CurrencyRate>>>
        get() = _currencyRateList

    override suspend fun getCurrencyRateFromServer(): NetworkResult<List<CurrencyRate>> {
        return safeApiCall(
            call = { apiInterface.getOpenExchangeRates() },
            transform = { it.toCurrencyRates() },
            saveCallResult = { insertCurrencyRateInDb(it) }
        )
    }

    override suspend fun getCurrencyRateFromDb(): NetworkResult<List<CurrencyRate>> {
        return safeDbCall { currencyRateDao.getCurrencyRateList() }
    }

    override suspend fun getCurrencyListFromServer(): NetworkResult<List<Currency>> {
        return safeApiCall(
            call = { apiInterface.getCurrencies() },
            transform = { it.map { Currency(it.key, it.value) } },
            saveCallResult = { insertCurrencyListInDb(it) }
        )
    }

    override suspend fun getCurrencyListFromDb(): NetworkResult<List<Currency>> {
        return safeDbCall { currencyDao.getCurrencyList() }
    }

    override suspend fun insertCurrencyRateInDb(currencyRates: List<CurrencyRate>) {
        val currentTime = System.currentTimeMillis()
        withContext(Dispatchers.IO) {
            currencyRateDao.deleteCurrencyRateList()
            currencyRates.forEach { it.timeStamp = currentTime }
            currencyRateDao.insertCurrencyRateList(currencyRates)
        }
    }

    override suspend fun insertCurrencyListInDb(currencies: List<Currency>) {
        val currentTime = System.currentTimeMillis()
        withContext(Dispatchers.IO) {
            currencyDao.deleteCurrencyList()
            currencies.forEach { it.timeStamp = currentTime }
            currencyDao.insertCurrencyList(currencies)
        }
    }

    override suspend fun getCurrencyRateList() {
        _currencyRateList.value = NetworkResult.Loading()
        _currencyRateList.value = if (getCurrencyRateFromDb().data?.isEmpty() == true) {
            getCurrencyRateFromServer()
        } else {
            getCurrencyRateFromDb()
        }
    }

    override suspend fun getCurrencyList() {
        _currencyList.value = NetworkResult.Loading()
        _currencyList.value = if (getCurrencyListFromDb().data?.isEmpty() == true) {
            getCurrencyListFromServer()
        } else {
            getCurrencyListFromDb()
        }
    }

    private suspend fun <T, R> safeApiCall(
        call: suspend () -> retrofit2.Response<T>,
        transform: (T) -> R,
        saveCallResult: suspend (R) -> Unit
    ): NetworkResult<R> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                val data = transform(response.body()!!)
                saveCallResult(data)
                NetworkResult.Success(data)
            } else {
                val errorObj = JSONObject(response.errorBody()?.charStream()?.readText() ?: "{}")
                NetworkResult.Error(errorObj.getString("status_message"))
            }
        } catch (e: HttpException) {
            NetworkResult.Error("Something went wrong! HTTP error: ${e.code()}")
        } catch (e: IOException) {
            NetworkResult.Error("Couldn't reach server, check your internet connection.")
        }
    }

    private suspend fun <T> safeDbCall(call: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.Success(withContext(Dispatchers.IO) { call() })
        } catch (e: Exception) {
            NetworkResult.Error("Error fetching data from database: ${e.message}")
        }
    }
}
