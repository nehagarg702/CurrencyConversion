package com.example.currencyconversion.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencyconversion.repository.CurrencyConversionRepository

class ViewModelFactory (private var currencyConversionRepository: CurrencyConversionRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("ViewModelFactory", "Creating ViewModel: $modelClass")
        return CurrencyConversionViewModel(currencyConversionRepository) as T
    }
}

//@Singleton
//class ViewModelFactory @Inject constructor(
//    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
//) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val creator = creators[modelClass]
//            ?: creators.entries.firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
//            ?: throw IllegalArgumentException("unknown model class $modelClass")
//
//        return try {
//            creator.get() as T
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//    }
//}
