package com.lfssolutions.retialtouch.domain.model.settlement

import kotlinx.serialization.Serializable

@Serializable
data class PosSettlement(
    val id: Int = 0,
    val date: String,
    val diff: Double,
    val floatMoney: Double,
    val amount: Double,
    val cashIn: Double,
    val cashOut: Double,
    val itemTotal: Double,
    val netTotal: Double,
    val subTotal: Double,
    val localTotal: Double,
    val posSettlementDetails: List<PosSettlementItem> = emptyList()
)
@Serializable
data class PosSettlementItem(
    val paymentTypeId: Int,
    val paymentType: String,
    val serverAmount: Double,
    val localAmount: Double,
    val diff: Double,
)
