package com.example.currencyconversion

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.model.CurrencyRate
import com.example.currencyconversion.network.NetworkResult
import com.example.currencyconversion.repository.CurrencyConversionRepository

class MockCurrencyConversionsRepository : CurrencyConversionRepository {
    var localCurrencyRateData = listOf<CurrencyRate>()
    var localCurrencyData = listOf<Currency>()


    private val _currencyList = mutableStateOf<NetworkResult<List<Currency>>>(NetworkResult.Loading())
    override val currencyList: State<NetworkResult<List<Currency>>>
        get() = _currencyList

    private val _currencyRateList = mutableStateOf<NetworkResult<List<CurrencyRate>>>(NetworkResult.Loading())
    override val currencyRateList: State<NetworkResult<List<CurrencyRate>>>
        get() = _currencyRateList

    override suspend fun getCurrencyRateFromServer(): NetworkResult<List<CurrencyRate>> {
        val serverData = mutableListOf<CurrencyRate>()
        serverData += CurrencyRate("AED", 3.673)
        serverData += CurrencyRate("USD", 1.0)
        insertCurrencyRateInDb(serverData)
        return NetworkResult.Success(serverData)
    }

    override suspend fun getCurrencyRateFromDb(): NetworkResult<List<CurrencyRate>> {
        return NetworkResult.Success(localCurrencyRateData)
    }

    override suspend fun getCurrencyListFromServer(): NetworkResult<List<Currency>> {
        val serverData = mutableListOf<Currency>()
        serverData += Currency("AED","United Arab Emirates Dirham" )
        serverData += Currency("USD", "United States Dollar")
        insertCurrencyListInDb(serverData)
        return NetworkResult.Success(serverData)
    }

    override suspend fun getCurrencyListFromDb(): NetworkResult<List<Currency>> {
        return NetworkResult.Success(localCurrencyData)
    }

    override suspend fun insertCurrencyRateInDb(currencyRates: List<CurrencyRate>) {
        localCurrencyRateData = currencyRates.map {
            CurrencyRate(it.symbol, it.amount )
        }
    }

    override suspend fun insertCurrencyListInDb(currencies: List<Currency>) {
        localCurrencyData = currencies.map {
            Currency(it.symbol, it.name )
        }
    }

    override suspend fun getCurrencyRateList() {
        _currencyRateList.value = NetworkResult.Loading()
        _currencyRateList.value =  if(getCurrencyRateFromDb().data?.isEmpty() == true){
            getCurrencyRateFromServer()
        } else{
            getCurrencyRateFromDb()
        }
    }

    override suspend fun getCurrencyList() {
        _currencyList.value = NetworkResult.Loading()
        _currencyList.value =  if(getCurrencyListFromDb().data?.isEmpty() == true){
            getCurrencyListFromServer()
        } else{
            getCurrencyListFromDb()
        }
    }
}