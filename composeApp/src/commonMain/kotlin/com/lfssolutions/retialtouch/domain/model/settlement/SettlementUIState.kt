package com.lfssolutions.retialtouch.domain.model.settlement

import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale

data class SettlementUIState(
    var isLoading : Boolean = false,
    var isSync : Boolean = false,
    var isError : Boolean = false,
    var errorDesc : String = "",
    var enteredAmount : Double = 0.0,
    val location : Location = Location(),
    val tenantId : Int = 0,
    var callPosSettlement : Boolean = false,
    val localSettlement : List<PosPaymentTypeSummary> = mutableListOf(),
    val remoteSettlement:PosDaySummary? = PosDaySummary(),

    //Pending
    var showPendingSales : Boolean = false,
    var showPendingSalesMessage : Boolean = false,
    val pendingSaleCount : Long = 0,
    val pendingSales : List<PendingSale> = mutableListOf(),


)
