package com.hashmato.retailtouch.domain.model.promotions

import kotlinx.serialization.Serializable

@Serializable
data class PromotionDao(
    val promotionId:Long = 0,
    val inventoryCode:String = "",
    val promotion: Promotion = Promotion()
)