package com.lfssolutions.retialtouch.domain.model.promotions


data class CRPromotionByQuantity(
    val amount :Double,
    val qty :Double,
    val promoQty :Double,
    val percentage :Double?,
    var remainingPromotionQty :Double,
    val items :List<CRPromotionByQuantityItem>,
)
data class CRPromotionByQuantityItem(
    val code :String,
    val qty :Double,
    val price :Double,
    val checked :Boolean
)
data class CRPromotionByPriceBreak(
    val price :Double,
    val qty :Double,
    val promoQty :Double,
)
