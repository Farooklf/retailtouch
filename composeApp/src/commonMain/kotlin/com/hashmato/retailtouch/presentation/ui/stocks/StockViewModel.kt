package com.hashmato.retailtouch.presentation.ui.stocks


import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.model.products.POSProduct
import com.hashmato.retailtouch.presentation.viewModels.BaseViewModel
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
    private var originalProductList: List<POSProduct> = emptyList()

    init {
        getProducts()
    }

    private fun getProducts() {
        viewModelScope.launch {
            delay(1000)
            sqlRepository.getProducts()
                .collect { product ->
                    _stockUiState.update { currentState ->
                        originalProductList = currentState.products + product
                        currentState.copy(products = currentState.products + product)
                    } }
        }
    }

    fun updateSearchQuery(query:String){
        viewModelScope.launch {
            _stockUiState.update { currentState->
                if (query.isEmpty()) {
                    // Restore full list when query is cleared
                    currentState.copy(products = originalProductList, searchQuery = query)
                } else if (!isCode(query)) {
                    val filteredList = stockUiState.value.products.filter {
                        it.barcode.contains(query) || it.productCode.contains(query)
                    }
                    currentState.copy(products = filteredList,searchQuery = query)
                }else{
                    currentState.copy(searchQuery = query)
                }
            }
        }
    }

    // Detects if a query is likely a code (numeric and shorter than a typical name)
    private fun isCode(query: String): Boolean {
        return query.all { it.isDigit() } /*&& query.length <= 10 // Adjust length if needed*/
    }

    // Filter by barcode or inventory code
    private fun filterListByCode(query: String) {
        val filteredList = stockUiState.value.products.filter {
            it.barcode.contains(query) || it.productCode.contains(query)
        }

        _stockUiState.update { it.copy(products = filteredList) }
       /* if (filteredList.isNotEmpty()) {
            insertPosListItem(filteredList[0]) // Add only the first matched item
        }*/
    }

    fun toggleProductSelection(productId: Long) {
        _stockUiState.update { state ->
            val newSelection = state.selectedProducts.toMutableSet()
            if (newSelection.contains(productId)) {
                newSelection.remove(productId)
            } else {
                newSelection.add(productId)
            }
            state.copy(selectedProducts = newSelection)
        }
    }

    fun clearSelection() {
        _stockUiState.update { it.copy(selectedProducts = emptySet()) }
    }

    fun scanBarcode(){
        viewModelScope.launch {
            val query=_stockUiState.value.searchQuery
            val filteredList = _stockUiState.value.products.filter {
                it.barcode.contains(query) || it.productCode.contains(query)
            }
            _stockUiState.update { it.copy(products = filteredList) }
        }
    }

}