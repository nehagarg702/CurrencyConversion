package com.example.currencyconversion.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.currencyconversion.network.NetworkResult
import com.example.currencyconversion.viewmodel.CurrencyConversionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(viewModel: CurrencyConversionViewModel) {

    var expanded by remember { mutableStateOf(false) }

    Column {
        if (viewModel.currencyRateList.value.data?.isNotEmpty() == true) {
            OutlinedTextField(
                value = TextFieldValue(
                    text = viewModel.amount.value,
                    selection = TextRange(viewModel.amount.value.length)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 0.dp, end = 8.dp, start = 8.dp)
                    .border(BorderStroke(width = 1.dp, color = Color.Transparent)),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { viewModel.getConvertedCurrencyRateList(it.text) },
                placeholder = { Text("0.0", textAlign = TextAlign.End) },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .wrapContentSize(Alignment.TopEnd)
                    .padding(top = 4.dp, bottom = 10.dp, end = 8.dp)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
                            .clickable(onClick = { expanded = !expanded } ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(viewModel.baseCurrency.value.symbol)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    DropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (viewModel.currencyRateList.value is NetworkResult.Loading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .width(500.dp)
                                .height(500.dp),
                            content = {
                                viewModel.currencyList.value.data?.let {
                                    items(it.size) { index ->
                                        val currency = it[index]
                                        DropdownMenuItem(
                                            text = { Text("${currency.symbol} - ${currency.name}") },
                                            onClick = {
                                                viewModel.setBaseCurrency(it[index])
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            })
                    }
            }
        }
        CurrencyConversionListScreen(viewModel = viewModel)
    }
}