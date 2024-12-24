package com.lfssolutions.retialtouch.domain.model.settlement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateEditPOSSettlementResult(
    @SerialName("success")
    val success: Boolean = false,
)
