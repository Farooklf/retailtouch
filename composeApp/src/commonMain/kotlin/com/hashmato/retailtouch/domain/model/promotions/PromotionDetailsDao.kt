package com.hashmato.retailtouch.domain.model.promotions

import kotlinx.serialization.Serializable

@Serializable
data class PromotionDetailsDao(
    val id:Long = 0,
    val promotionDetails: PromotionDetails = PromotionDetails()
)