package com.example.currencyconversion.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.network.NetworkResult
import com.example.currencyconversion.theme.CurrencyConversionTheme
import com.example.currencyconversion.viewmodel.CurrencyConversionViewModel
import com.example.currencyconversion.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class CurrencyConversionActivity : ComponentActivity() {

    private val viewModelFactory: ViewModelFactory by inject()
    private val viewModel: CurrencyConversionViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConversionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        Header(viewModel)
                    }
                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    CurrencyConversionTheme {
//        Greeting("Android")
//    }
//}