package com.lfssolutions.retialtouch.presentation.ui.stocks

import com.lfssolutions.retialtouch.domain.model.products.POSProduct

data class StockUIState(
    val isError:Boolean=false,
    val errorMsg:String="",
    val searchQuery:String="",
    val products: List<POSProduct>  = emptyList(),
    val selectedProducts: Set<Long> = emptySet() // Store selected product IDs
)