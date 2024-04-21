package com.example.currencyconversion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconversion.model.CurrencyRate

@Dao
interface CurrencyRateDao {
    @Query("SELECT * FROM CurrencyRate")
    suspend fun getCurrencyRateList(): List<CurrencyRate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyRateList(entities: List<CurrencyRate>)

    @Query("DELETE FROM CurrencyRate")
    suspend fun deleteCurrencyRateList()
}