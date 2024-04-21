package com.example.currencyconversion

import com.example.currencyconversion.model.Currency
import com.example.currencyconversion.model.CurrencyRate
import com.example.currencyconversion.repository.CurrencyConversionRepository
import com.example.currencyconversion.viewmodel.CurrencyConversionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrencyConversionViewModelTest {

    private lateinit var currencyConversionRepository: CurrencyConversionRepository

    private lateinit var currencyConversionViewModel: CurrencyConversionViewModel

    private val testDispatcher = TestCoroutineDispatcher()


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher for testing
        currencyConversionRepository = MockCurrencyConversionsRepository()
        currencyConversionViewModel = CurrencyConversionViewModel(currencyConversionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher after testing
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `test initialization`() = runBlockingTest {
        // Mock data retrieval
        val currencyList = listOf(Currency("USD","United States Dollar"), Currency("AED", "United Arab Emirates Dirham"))
        val currencyRateList = listOf(CurrencyRate("USD", 1.0), CurrencyRate("AED", 3.673))
        // Call initialization method
        currencyConversionViewModel.getData()

        // Assert that base currency is set and data is loaded
        assertEquals(currencyList.first(), currencyConversionViewModel.baseCurrency.value)
        assertEquals(currencyList.size, currencyConversionViewModel.currencyList.value.data?.size)
        assertEquals(currencyRateList.size, currencyConversionViewModel.currencyRateList.value.data?.size)
    }

    @Test
    fun `test conversion logic with valid amount`() = runBlockingTest {
        // Set base currency
        currencyConversionViewModel.setBaseCurrency(Currency("USD"))

        // Set amount
        val amount = "100"
        currencyConversionViewModel.getConvertedCurrencyRateList(amount)

        // Assert conversion logic
        val convertedCurrencyRates = currencyConversionViewModel.convertedCurrencyRateList.value
        assertEquals(100.0, convertedCurrencyRates.firstOrNull { it.symbol == "USD" }?.amount)
        assertEquals(367.3, convertedCurrencyRates.firstOrNull { it.symbol == "AED" }?.amount)
    }

    @Test
    fun `test base currency change`() = runBlockingTest {
        // Set base currency to USD
        currencyConversionViewModel.setBaseCurrency(Currency("USD"))

        // Set amount
        val amount = "100"
        currencyConversionViewModel.getConvertedCurrencyRateList(amount)

        // Assert conversion logic for USD
        var convertedCurrencyRates = currencyConversionViewModel.convertedCurrencyRateList.value
        assertEquals(100.0, convertedCurrencyRates.firstOrNull { it.symbol == "USD" }?.amount)
        assertEquals(367.3, convertedCurrencyRates.firstOrNull { it.symbol == "AED" }?.amount)

        // Change base currency to EUR
        currencyConversionViewModel.setBaseCurrency(Currency("AED"))

        // Assert conversion logic for EUR
        convertedCurrencyRates = currencyConversionViewModel.convertedCurrencyRateList.value
        assertEquals(27.22570106180234, convertedCurrencyRates.firstOrNull { it.symbol == "USD" }?.amount)
        assertEquals(100.0, convertedCurrencyRates.firstOrNull { it.symbol == "AED" }?.amount)
    }
}

