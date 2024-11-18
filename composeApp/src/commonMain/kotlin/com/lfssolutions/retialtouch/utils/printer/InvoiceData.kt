package com.lfssolutions.retialtouch.utils.printer

data class InvoiceData(
    val invoiceNo: String,
    val invoiceDate: String,
    val customerName: String,
    val productName: String,
    val qty: Int,
    val price: Double,
    val netTotal: Double
)

data class ItemTest(
    val productName: String,
    val quantity: Int,
    val price: Double
)
