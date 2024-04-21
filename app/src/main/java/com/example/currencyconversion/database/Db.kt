package com.example.currencyconversion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.model.CurrencyRate

@Database(version = 1, entities = [Currency::class, CurrencyRate::class])
abstract class Db : RoomDatabase() {

    abstract fun getCurrencyDao() : CurrencyDao
    abstract fun getCurrencyRateDao() : CurrencyRateDao
}