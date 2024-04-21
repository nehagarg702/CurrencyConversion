package com.example.currencyconversion.network

import com.example.currencyconversion.utils.Constants.APP_ID
import com.example.currencyconversion.model.CurrencyConversion
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("api/latest.json")
    suspend fun getOpenExchangeRates(@Query("app_id") appId: String = APP_ID) : Response<CurrencyConversion>

    @GET("api/currencies.json")
    suspend fun getCurrencies(@Query("app_id") appId: String = APP_ID) : Response<Map<String, String>>

}
