package com.example.currencyconversion.view

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.currencyconversion.network.NetworkResult
import com.example.currencyconversion.viewmodel.CurrencyConversionViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Header(viewModel: CurrencyConversionViewModel) {
    val currencyRateListState by viewModel.currencyRateList

    Column {
        ContentState(
            visible = currencyRateListState is NetworkResult.Success,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun ContentState(visible: Boolean, viewModel: CurrencyConversionViewModel) {
    if (visible) {
        var expanded by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = TextFieldValue(
                text = viewModel.amount.collectAsState().value,
                selection = TextRange(viewModel.amount.collectAsState().value.length)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 0.dp, end = 8.dp, start = 8.dp)
                .border(BorderStroke(width = 1.dp, color = Color.Transparent)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                textColor = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { viewModel.getConvertedCurrencyRateList(it.text) },
            placeholder = { Text("0.0", textAlign = TextAlign.End) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
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
                    .clickable(onClick = { expanded = !expanded }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.baseCurrency.collectAsState().value.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
        CurrencyConversionListScreen(viewModel = viewModel)
    }
}

