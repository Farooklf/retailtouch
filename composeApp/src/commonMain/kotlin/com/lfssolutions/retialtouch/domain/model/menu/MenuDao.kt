package com.lfssolutions.retialtouch.domain.model.menu

import com.lfssolutions.retialtouch.domain.model.products.Stock
import kotlinx.serialization.Serializable

@Serializable
data class MenuDao(
    val productId:Long =0,
    val menuProductItem: Stock = Stock()
)


