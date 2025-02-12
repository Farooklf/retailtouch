package com.lfssolutions.retialtouch.domain.model.products



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
    val invoiceTax: Double = 0.0,
    val invoiceNetTotal: Double = 0.0,
    val posInvoiceDetails: List<PosInvoiceDetail> = emptyList(),
    val posPayments: List<PosPayment> = emptyList(),
)
