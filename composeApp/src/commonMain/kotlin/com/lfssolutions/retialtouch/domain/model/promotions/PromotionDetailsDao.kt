package com.lfssolutions.retialtouch.domain.model.promotions

import kotlinx.serialization.Serializable

@Serializable
data class PromotionDetailsDao(
    val id:Int = 0,
    val promotionDetails: PromotionDetails = PromotionDetails()
)