package com.example.currencyconversion.utils

import android.app.Application
import com.example.currencyconversion.dependencyInjection.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class CurrencyConversionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        applicationInstance = this
        startKoin{
            androidLogger()
            androidContext(this@CurrencyConversionApp)
            modules(appModule)
        }
    }

    companion object {
        @JvmStatic
        lateinit var applicationInstance: CurrencyConversionApp
    }
}