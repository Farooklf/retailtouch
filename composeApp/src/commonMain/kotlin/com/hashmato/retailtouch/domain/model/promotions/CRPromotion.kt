package com.hashmato.retailtouch.domain.model.promotions


data class CRPromotionByQuantity(
    var amount :Double=0.0,
    var qty :Double=0.0,
    var promoQty :Double=0.0,
    var percentage :Double?=0.0,
    var remainingPromotionQty :Double=0.0,
    var items :MutableList<CRPromotionByQuantityItem> = mutableListOf(),
)
data class CRPromotionByQuantityItem(
    val code :String,
    val qty :Double,
    val price :Double,
    val checked :Boolean
)
data class CRPromotionByPriceBreak(
    var price :Double=0.0,
    var qty :Double=0.0,
    var promoQty :Double=0.0,
)
