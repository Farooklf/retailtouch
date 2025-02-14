package com.hashmato.retailtouch.domain.model.promotions

import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPromotionsByQtyResult(
    @SerialName("result") val result: List<PromotionQtyDetails>? = emptyList(),
    @SerialName("success") val success: Boolean,
    @SerialName("error") val error:  ErrorResponse? = null
)
