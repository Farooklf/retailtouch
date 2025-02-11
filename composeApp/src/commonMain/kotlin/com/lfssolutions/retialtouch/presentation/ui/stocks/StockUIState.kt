package com.lfssolutions.retialtouch.presentation.ui.stocks

import com.lfssolutions.retialtouch.domain.model.products.Product

data class StockUIState(
    val isError:Boolean=false,
    val errorMsg:String="",
    val searchQuery:String="",
    val products: List<Product>  = emptyList()
)