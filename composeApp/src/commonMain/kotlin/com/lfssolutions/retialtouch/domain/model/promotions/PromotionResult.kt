package com.lfssolutions.retialtouch.domain.model.promotions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionResult(
    @SerialName("items")
    val items: List<PromotionItem?>? = listOf()
)
