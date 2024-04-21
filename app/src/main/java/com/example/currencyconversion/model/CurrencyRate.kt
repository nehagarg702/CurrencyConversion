package com.example.currencyconversion.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyRate (
    @PrimaryKey val symbol: String,
    val amount: Double = 0.0,
    var timeStamp: Long = 0
)