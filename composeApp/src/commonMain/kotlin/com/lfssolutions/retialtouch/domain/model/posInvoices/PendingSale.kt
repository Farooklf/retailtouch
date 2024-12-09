package com.lfssolutions.retialtouch.domain.model.posInvoices

import com.lfssolutions.retialtouch.domain.model.products.PosInvoiceDetail
import com.lfssolutions.retialtouch.domain.model.products.PosPayment
import kotlinx.serialization.Serializable

@Serializable
data class PendingSale(
    val id: Long = 0,
    val locationId: Long = 0,
    val locationCode: String = "",
    val tenantId: Int = 0,
    val employeeId: Int = 0,
    val invoiceNo: String = "",
    val terminalName: String="",
    val isRetailWebRequest:Boolean=false,
    val invoiceDate: String="",
    val grandTotal: Double=0.0,
    val invoiceTotal: Double=0.0,
    val invoiceItemDiscount: Double=0.0,
    val invoiceTotalValue: Double=0.0,
    val invoiceNetDiscountPerc: Double=0.0,
    val invoiceNetDiscount: Double=0.0,
    val invoiceTotalAmount: Double=0.0,
    val invoiceSubTotal: Double=0.0,
    val invoiceNetTotal: Double=0.0,
    val invoiceNetCost: Double=0.0,
    val invoiceRoundingAmount: Double=0.0,
    val paid: Double=0.0,
    val globalTax: Double=0.0,
    val remarks: String="",
    val deliveryDateTime: String="",
    val type: Int=0,
    val status: Int=0,
    val memberId: Int=0,
    val memberName: String="",
    val qty: Int=0,
    val address1: String="",
    val address2: String="",
    val posInvoiceDetailRecord: List<PosInvoiceDetail>? = emptyList(),
    val posPaymentConfigRecord: List<PosPayment>? = emptyList(),
    val globalDiscount: Double=0.0,
    val isSynced: Boolean = false
)

