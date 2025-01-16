package com.lfssolutions.retialtouch.domain.model.payout

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PayOutIn(
    val id: Int? = null,
    val payOutInNo: String? = null,
    val type: String? = null,
    val typeName: String? = null,
    val locationId: Long? = null,
    val locationName: String? = null,
    val terminal: String? = null,
    val terminalName: String? = null,
    val description: String? = null,
    val amount: Double? = null,
    val payTo: String? = null,
    val transactionDate: LocalDateTime? = null,
    val createdDateTime: LocalDateTime? = null
)
