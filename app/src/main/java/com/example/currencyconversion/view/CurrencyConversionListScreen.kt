package com.example.currencyconversion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.currencyconversion.network.NetworkResult
import com.example.currencyconversion.viewmodel.CurrencyConversionViewModel
import java.text.NumberFormat

@Composable
fun CurrencyConversionListScreen(viewModel: CurrencyConversionViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (viewModel.currencyRateList.value) {
            is NetworkResult.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is NetworkResult.Error -> {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)) {
                    Text(
                        text = viewModel.currencyRateList.value.message!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,

                    )
                    Button(onClick = { viewModel.getData() },
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant )

                    ){
                        Text(
                            text = "Retry",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    content = {
                        viewModel.convertedCurrencyRateList.value.let {
                            items(it.size) { index ->
                                val conversion = it[index]
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .padding(5.dp)
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxSize(),
                                        shape = RoundedCornerShape(4.dp),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 4.dp
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 4.dp, end = 4.dp, top = 4.dp),
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = conversion.symbol,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = NumberFormat.getNumberInstance().format(conversion.amount),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    }
}