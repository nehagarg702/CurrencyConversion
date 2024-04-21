package com.example.currencyconversion.model

data class CurrencyConversion(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Map<String, Double>,
    val timestamp: Int
)

fun CurrencyConversion.toCurrencyRates() : List<CurrencyRate> {
    return rates.map {
        CurrencyRate(it.key, it.value)
    }
}