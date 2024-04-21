package com.example.currencyconversion.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.model.CurrencyRate
import com.example.currencyconversion.network.NetworkResult
import com.example.currencyconversion.repository.CurrencyConversionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.NumberFormat

class CurrencyConversionViewModel(private var currencyConversionRepository: CurrencyConversionRepository)
    : ViewModel(){

    private val _baseCurrency = mutableStateOf(Currency(""))
    val baseCurrency : State<Currency>
        get() = _baseCurrency

    private val _amount = mutableStateOf("0")
    val amount: State<String>
        get() = _amount

    val currencyList: State<NetworkResult<List<Currency>>>
        get() = currencyConversionRepository.currencyList

    val currencyRateList: State<NetworkResult<List<CurrencyRate>>>
        get() = currencyConversionRepository.currencyRateList

    private val _convertedCurrencyRateList = mutableStateOf<List<CurrencyRate>>(listOf())
    val convertedCurrencyRateList: State<List<CurrencyRate>>
        get() = _convertedCurrencyRateList

    init {
        getData()
    }

    fun getData(){
        viewModelScope.launch {
            val currencyRateData = viewModelScope.async { currencyConversionRepository.getCurrencyRateList() }
            val currencyData = viewModelScope.async { currencyConversionRepository.getCurrencyList() }
            currencyRateData.await()
            currencyData.await()
            getConvertedCurrencyRateList("0")
            _baseCurrency.value = currencyList.value.data?.find { it.symbol == "USD" } ?: Currency()
        }
    }

    fun getConvertedCurrencyRateList(amount : String) {
        val data  = currencyRateList.value.data?.find { it.symbol == baseCurrency.value.symbol }
        val currencyRateListData = mutableListOf<CurrencyRate>()
        if(amount.isEmpty() || amount == "00" || amount == "0") {
            _amount.value = "0"
            currencyRateList.value.data?.map {
                currencyRateListData.add(
                    CurrencyRate(
                        it.symbol,
                        0.0)
                )
            }
        }
        else {
            _amount.value = NumberFormat.getNumberInstance().format(amount.replace(",","").toDouble())
            currencyRateList.value.data?.map {
                currencyRateListData.add(
                    CurrencyRate(
                        it.symbol,
                        it.amount * (amount.replace(",", "").toDoubleOrNull() ?: 0.0) / (data?.amount ?: 1.0)
                    )
                )
            }
        }
        _convertedCurrencyRateList.value = currencyRateListData
    }

    fun setBaseCurrency(currency: Currency) {
        _baseCurrency.value = currency
        getConvertedCurrencyRateList(_amount.value)
    }
}