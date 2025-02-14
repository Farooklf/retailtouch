package com.hashmato.retailtouch.domain.model.posInvoices

import com.hashmato.retailtouch.domain.model.products.PosInvoice
import kotlinx.serialization.Serializable

@Serializable
data class PendingSaleDao(
    val isSynced : Boolean = false,
    val isDbUpdate : Boolean = false,
    val posSaleId : Long = 0,
    val posInvoice: PosInvoice = PosInvoice()
)
