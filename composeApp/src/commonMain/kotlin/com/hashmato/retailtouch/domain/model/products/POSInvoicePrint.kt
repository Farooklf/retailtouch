package com.hashmato.retailtouch.domain.model.products

import kotlinx.serialization.Serializable


@Serializable
data class POSInvoicePrint(
    val invoiceNo: String = "",
    val invoiceDate: String = "",
    val customerName: String = "",
    val address1 :String = "",
    val address2 :String = "",
    val qty :Int = 0,
    val invoiceSubTotal: Double = 0.0,
    val invoiceItemDiscount: Double = 0.0,
    val invoiceNetDiscount: Double = 0.0,
    val invoiceNetDiscountPer: String = "",
    val invoiceTax: Double = 0.0,
    val invoiceNetTotal: Double = 0.0,
    val posInvoiceDetails: List<PosInvoicePrintDetails> = emptyList(),
    val posPayments: List<PosPayment> = emptyList(),
)

@Serializable
data class PosInvoicePrintDetails(
    val posInvoiceId: Int = 0,
    val inventoryName: String = "",
    val inventoryCode: String = "",
    val productId: Long = 0,
    val price: Double = 0.0,
    val qty: Int=0,
    val itemDiscount: String = "",
    val itemDiscountPerc: Double = 0.0,
    val netDiscount: Double = 0.0,
    val netTotal: Double,
    val subTotal: Double,
    val netCost: Double,
)
