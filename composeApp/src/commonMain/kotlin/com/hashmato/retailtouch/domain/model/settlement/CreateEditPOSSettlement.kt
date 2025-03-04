package com.hashmato.retailtouch.domain.model.settlement

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateEditPOSSettlement(
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("settlementDate")
    val settlementDate: LocalDateTime? = null,
    @SerialName("locationId")
    val locationId: Long? = null,
    @SerialName("locationName")
    val locationName: String? = null,
    @SerialName("terminalName")
    val terminalName: String? = null,
    @SerialName("shift")
    val shift: Int? = null,
    @SerialName("updateDefaultShift")
    val updateDefaultShift:Boolean=false,
    @SerialName("salesTotal")
    val salesTotal: Double? = null,
    @SerialName("computerTotal")
    val computerTotal: Double? = null,
    @SerialName("manualTotal")
    val manualTotal: Double? = null,
    @SerialName("floatAmount")
    val floatAmount: Double? = null,
    @SerialName("cashOut")
    val cashOut: Double? = null,
    @SerialName("cashIn")
    val cashIn: Double? = null,
    @SerialName("netManualTotal")
    val netManualTotal: Double? = null,
    @SerialName("shortageOrExcess")
    val shortageOrExcess: Double? = null,
    @SerialName("createdDateTime")
    val createdDateTime: LocalDateTime? = null,
    @SerialName("posSettlementDetails")
    val posSettlementDetails: List<PosSettlementDetail>? = null
)
