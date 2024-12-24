package com.lfssolutions.retialtouch.domain.model.settlement

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosSettlementDetail(
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("posSettlementId")
    val posSettlementId: Int? = null,
    @SerialName("paymentTypeId")
    val paymentTypeId: Int? = null,
    @SerialName("paymentTypeName")
    val paymentTypeName: String? = null,
    @SerialName("computerTotal")
    val computerTotal: Double? = null,
    @SerialName("manualTotal")
    val manualTotal: Double? = null,
    @SerialName("shortageOrExcess")
    val shortageOrExcess: Double? = null,
)
