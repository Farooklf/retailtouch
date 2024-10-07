package com.lfssolutions.retialtouch.domain.model.promotions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PromotionRequest(
    @SerialName("TenantId")
    val tenantId: Int,
)
