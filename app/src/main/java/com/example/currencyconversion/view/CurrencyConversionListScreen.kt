package com.example.currencyconversion.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.currencyconversion.network.NetworkResult
import com.example.currencyconversion.viewmodel.CurrencyConversionViewModel

@Composable
fun CurrencyConversionListScreen(viewModel: CurrencyConversionViewModel) {
    val currencyRateListState by viewModel.currencyRateList
    Box(modifier = Modifier.fillMaxSize()) {
        LoadingState(visible = currencyRateListState is NetworkResult.Loading)
        ErrorState(
            visible = currencyRateListState is NetworkResult.Error,
            errorMessage = (currencyRateListState as? NetworkResult.Error)?.message,
            onRetry = { viewModel.getData() }
        )
        ContentState(
            visible = currencyRateListState is NetworkResult.Success,
            viewModel = viewModel
        )
    }
}


@Composable
fun LoadingState(visible: Boolean) {
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ErrorState(visible: Boolean, errorMessage: String?, onRetry: () -> Unit) {
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            ) {
                Text(
                    text = errorMessage ?: "Error occurred",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "Retry",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun ContentState(visible: Boolean, viewModel: CurrencyConversionViewModel) {
    if (visible) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            content = {
                viewModel.convertedCurrencyRateList.value.let {
                    items(it.size) { index ->
                        val conversion = it[index]
                        CurrencyGridItem(
                            symbol = conversion.symbol,
                            amount = conversion.amount
                        )
                    }
                }
            },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        )
    }
}
