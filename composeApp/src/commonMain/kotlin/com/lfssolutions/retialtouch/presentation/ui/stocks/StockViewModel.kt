package com.lfssolutions.retialtouch.presentation.ui.stocks


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class StockViewModel :BaseViewModel() , KoinComponent {

    private val _stockUiState = MutableStateFlow(StockUIState())
    val stockUiState: StateFlow<StockUIState> = _stockUiState.asStateFlow()

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            delay(5000)
            sqlRepository.getProducts()
                .collect { product ->
                    _stockUiState.update { currentState ->
                        currentState.copy(products = currentState.products + product)
                    } }
        }
    }

    fun updateSearchQuery(query:String){
        viewModelScope.launch {
            _stockUiState.update {
                if (isCode(query)) {
                    filterListByCode(query) // Filter by barcode or inventory code
                }
                it.copy(searchQuery = query)
            }
        }
    }

    // Detects if a query is likely a code (numeric and shorter than a typical name)
    private fun isCode(query: String): Boolean {
        return query.all { it.isDigit() } /*&& query.length <= 10 // Adjust length if needed*/
    }

    private fun filterListByCode(query: String) {
        val filteredList = stockUiState.value.products.filter {
            it.barcode.contains(query) || it.productCode.contains(query)
        }

        _stockUiState.update { it.copy(products = filteredList) }
       /* if (filteredList.isNotEmpty()) {
            insertPosListItem(filteredList[0]) // Add only the first matched item
        }*/
    }

}