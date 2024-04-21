package com.example.currencyconversion.network

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null, val code : Int? = null) {
    class Loading<T>() : NetworkResult<T>()
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String?, code: Int? = null, data: T? = null, ) : NetworkResult<T>(data, message, code)
    class Exception<T>(message: String?) : NetworkResult<T>(null, message)
}