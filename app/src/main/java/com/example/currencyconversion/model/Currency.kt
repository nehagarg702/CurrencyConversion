package com.example.currencyconversion.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Currency (
    @PrimaryKey val symbol: String = "",
    val name: String = "",
    var timeStamp: Long = 0
)