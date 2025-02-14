package com.hashmato.retailtouch.presentation.ui.stocks

import com.hashmato.retailtouch.domain.model.products.POSProduct

data class StockUIState(
    val isError:Boolean=false,
    val errorMsg:String="",
    val searchQuery:String="",
    val products: List<POSProduct>  = emptyList(),
    val selectedProducts: Set<Long> = emptySet() // Store selected product IDs
)