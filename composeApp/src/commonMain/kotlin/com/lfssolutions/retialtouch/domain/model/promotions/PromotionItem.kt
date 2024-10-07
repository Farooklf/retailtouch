package com.lfssolutions.retialtouch.domain.model.promotions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionItem(
    @SerialName("aliasName")
    val aliasName: String? = "",
)
