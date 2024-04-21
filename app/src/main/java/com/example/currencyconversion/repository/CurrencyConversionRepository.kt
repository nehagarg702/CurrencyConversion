package com.example.currencyconversion.repository

import androidx.compose.runtime.State
import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.model.CurrencyRate
import com.example.currencyconversion.network.NetworkResult

interface CurrencyConversionRepository {

    val currencyList: State<NetworkResult<List<Currency>>>

    val currencyRateList: State<NetworkResult<List<CurrencyRate>>>

    suspend fun getCurrencyRateFromServer(): NetworkResult<List<CurrencyRate>>

    suspend fun getCurrencyRateFromDb(): NetworkResult<List<CurrencyRate>>

    suspend fun getCurrencyListFromServer(): NetworkResult<List<Currency>>

    suspend fun getCurrencyListFromDb(): NetworkResult<List<Currency>>

    suspend fun insertCurrencyRateInDb(currencyRates: List<CurrencyRate>)

    suspend fun insertCurrencyListInDb(currencies: List<Currency>)

    suspend fun getCurrencyRateList()

    suspend fun getCurrencyList()
}
