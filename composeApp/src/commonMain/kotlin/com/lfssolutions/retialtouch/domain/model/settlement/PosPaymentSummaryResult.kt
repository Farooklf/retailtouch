package com.lfssolutions.retialtouch.domain.model.settlement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PosPaymentSummaryResult(
    @SerialName("result")
    val result: List<PosPaymentSummary>? = emptyList(),
    @SerialName("success")
    val success: Boolean = false,
)
