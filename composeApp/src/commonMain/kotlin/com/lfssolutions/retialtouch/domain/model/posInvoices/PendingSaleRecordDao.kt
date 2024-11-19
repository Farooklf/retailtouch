package com.lfssolutions.retialtouch.domain.model.posInvoices

import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import kotlinx.serialization.Serializable

@Serializable
data class PendingSaleRecordDao(
    val isSynced : Boolean = false,
    val isDbUpdate : Boolean = false,
    val posInvoice: PosInvoice = PosInvoice()
)
