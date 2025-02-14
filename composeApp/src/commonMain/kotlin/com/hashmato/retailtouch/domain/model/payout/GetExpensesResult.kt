package com.hashmato.retailtouch.domain.model.payout

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetExpensesResult(
    @SerialName("success")
    val success: Boolean = false,
)
