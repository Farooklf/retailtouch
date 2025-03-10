package com.hashmato.retailtouch.domain.model.promotions

import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetPromotionResult(
    @SerialName("result") val result: List<PromotionItem>? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("error") val error:  ErrorResponse? = null
)
