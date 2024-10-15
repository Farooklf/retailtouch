package com.lfssolutions.retialtouch.domain.model.inventory

data class CRShoppingCartItem(
    val price: Double,
    val discount: Double = 0.0,
    val discountIsInPercent: Boolean = false,
    var qty: Int = 1,
    val promotionActive: Boolean = false,
    val promotionPrice: Double? = null
){

}
