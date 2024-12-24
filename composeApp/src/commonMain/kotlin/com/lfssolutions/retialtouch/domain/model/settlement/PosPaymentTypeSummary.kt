package com.lfssolutions.retialtouch.domain.model.settlement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosPaymentTypeSummary(
    @SerialName("paymentTypeId")
    val paymentTypeId: Int? = 0,
    @SerialName("paymentType")
    val paymentType: String? =  "",
    @SerialName("amount")
    var amount: Double? =  0.0,
    var enteredAmount: String =  "",
    var imageUrl: String? = "",
)
