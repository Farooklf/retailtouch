package com.lfssolutions.retialtouch.domain.model.settlement

import com.lfssolutions.retialtouch.domain.model.location.Location

data class SettlementUIState(
    var isLoading : Boolean = false,
    var isError : Boolean = false,
    var errorDesc : String = "",
    val location : Location = Location(),

    val localSettlement : List<PosPaymentTypeSummary> = mutableListOf(),
    val remoteSettlement:PosDaySummary? = PosDaySummary(),
    val localSettlement1 :List<PosPaymentTypeSummary> = mutableListOf(),

)
