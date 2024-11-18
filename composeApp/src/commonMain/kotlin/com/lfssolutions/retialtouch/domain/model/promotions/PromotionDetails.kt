package com.lfssolutions.retialtouch.domain.model.promotions


import kotlinx.serialization.Serializable

@Serializable
data class PromotionDetails(
    val id:Long=0,
    val promotionId: Int=0,
    val productId: Int=0,
    val price: Double=0.0,
    var promotionPrice: Double=0.0,
    val promotionPerc: Double?=0.0,
    val inventoryCode: String="",
    val qty: Double=0.0,
    val amount: Double=0.0,
    val promotionTypeName: String="",
    val priceBreakPromotionAttribute : List<PriceBreakPromotionAttribute>? = emptyList()
)
