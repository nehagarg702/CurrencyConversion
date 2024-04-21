package com.example.currencyconversion.workManager

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.currencyconversion.repository.CurrencyConversionRepository

class CurrencyConversionWorkManager(context: Context, workerParams: WorkerParameters
                                    ,private var currencyConversionRepository: CurrencyConversionRepository) :
    CoroutineWorker(context, workerParams) {

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        return try{
            currencyConversionRepository.getCurrencyListFromServer()
            currencyConversionRepository.getCurrencyRateFromServer()
            Result.Success()
        }
        catch (exception : Exception)
        {
            Result.Failure()
        }

    }
}