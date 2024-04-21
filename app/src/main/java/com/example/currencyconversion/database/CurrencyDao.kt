package com.example.currencyconversion.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyconversion.model.Currency

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM Currency")
    suspend fun getCurrencyList(): List<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyList(entities: List<Currency>)


    @Query("DELETE FROM Currency")
    suspend fun deleteCurrencyList()
}