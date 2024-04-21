package com.example.currencyconversion.repository

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.currencyconversion.database.CurrencyDao
import com.example.currencyconversion.database.CurrencyRateDao
import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.model.CurrencyRate
import com.example.currencyconversion.model.toCurrencyRates
import com.example.currencyconversion.network.ApiInterface
import com.example.currencyconversion.network.NetworkResult
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class CurrencyConversionRepositoryImpl (
    private val apiInterface: ApiInterface,
    private val currencyRateDao: CurrencyRateDao,
    private val currencyDao: CurrencyDao
) : CurrencyConversionRepository {

    private val _currencyList = mutableStateOf<NetworkResult<List<Currency>>>(NetworkResult.Loading())
    override val currencyList: State<NetworkResult<List<Currency>>>
        get() = _currencyList

    private val _currencyRateList = mutableStateOf<NetworkResult<List<CurrencyRate>>>(NetworkResult.Loading())
    override val currencyRateList: State<NetworkResult<List<CurrencyRate>>>
        get() = _currencyRateList

    override suspend fun getCurrencyRateFromServer(): NetworkResult<List<CurrencyRate>> {
        var result: NetworkResult<List<CurrencyRate>>
        try {
            val response = apiInterface.getOpenExchangeRates()

            result = if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.toCurrencyRates()
                insertCurrencyRateInDb(data)
                NetworkResult.Success(data)
            } else if (response.errorBody() != null) {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                NetworkResult.Error(errorObj.getString("status_message"))
            } else {
                NetworkResult.Error("Something went wrong")
            }
        } catch (e: HttpException) {
            result = NetworkResult.Error(
                message = "Something went wrong! HTTP error: ${e.code()}"
            )
        } catch (e: IOException) {
            result = NetworkResult.Error(
                message = "Couldn't reach server, check your internet connection."
            )
        }
        _currencyRateList.value = result
        return result
    }

    override suspend fun getCurrencyRateFromDb(): NetworkResult<List<CurrencyRate>> {

        val result: NetworkResult<List<CurrencyRate>> = try {
            NetworkResult.Success(currencyRateDao.getCurrencyRateList())
        } catch (e: IllegalStateException) {
            NetworkResult.Error(
                message = e.message ?: "Error fetching data from database"
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Error fetching data from database"
            )
        }
        return result
    }

    override suspend fun getCurrencyListFromServer(): NetworkResult<List<Currency>> {

        var result: NetworkResult<List<Currency>>

        try {
            val response = apiInterface.getCurrencies()

            if (response.isSuccessful && response.body() != null) {
                val currencies = mutableListOf<Currency>()
                response.body()!!.map {
                    currencies += Currency(it.key, it.value)
                }
                insertCurrencyListInDb(currencies)
                result = NetworkResult.Success(currencies)
            } else if (response.errorBody() != null) {
                val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
                result = NetworkResult.Error(errorObj.getString("status_message"))
            } else {
                result = NetworkResult.Error("Something went wrong")
            }
        } catch (e: HttpException) {
            result =
                NetworkResult.Error(
                    message = "Something went wrong! HTTP error: ${e.code()}"
                )

        } catch (e: IOException) {
            result =
                NetworkResult.Error(
                    message = "Couldn't reach server, check your internet connection."
                )
        }
        _currencyList.value = result
        return result
    }

    override suspend fun getCurrencyListFromDb(): NetworkResult<List<Currency>> {

        val result: NetworkResult<List<Currency>> = try {
            NetworkResult.Success(currencyDao.getCurrencyList())
        } catch (e: IllegalStateException) {
            NetworkResult.Error(
                message = e.message ?: "Error fetching data from database"
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = "Error fetching data from database"
            )
        }
        return result
    }

    override suspend fun insertCurrencyRateInDb(currencyRates: List<CurrencyRate>) {
        try {
            currencyRateDao.deleteCurrencyRateList()
            val currentTime = System.currentTimeMillis()
            currencyRates.map { it.timeStamp = currentTime }
            currencyRateDao.insertCurrencyRateList(currencyRates)
        } catch (e: Exception) {
            throw Exception(e.message ?: "Error handling data from database")
        }
    }

    override suspend fun insertCurrencyListInDb(currencies: List<Currency>) {
        try {
            currencyDao.deleteCurrencyList()
            val currentTime = System.currentTimeMillis()
            currencies.map { it.timeStamp = currentTime }
            currencyDao.insertCurrencyList(currencies)

        } catch (e: Exception) {
            throw Exception(e.message ?: "Error handling data from database")
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