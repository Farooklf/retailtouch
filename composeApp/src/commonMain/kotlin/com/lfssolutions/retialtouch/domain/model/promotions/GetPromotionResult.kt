package com.lfssolutions.retialtouch.domain.model.promotions

import com.lfssolutions.retialtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetPromotionResult(
    @SerialName("result") val result: List<PromotionItem?>? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("error") val error:  ErrorResponse? = null
)
