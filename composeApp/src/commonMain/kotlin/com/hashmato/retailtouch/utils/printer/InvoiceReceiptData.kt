package com.hashmato.retailtouch.utils.printer

data class InvoiceReceiptData(
    val invoiceNo: String,
    val invoiceDate: String,
    val terms: String,
    val customerName: String,
    val address1: String,
    val address2: String,
    val items: List<ItemData>,
    val qty: Int,
    val invoiceSubTotal: Double,
    val tax: Double,
    val netTotal: Double,
    val balanceAmount: Double,
    val signature: String,
    val qrUrl: String
)

data class ItemData(
    val index: Int,
    val productName: String,
    val qty: Double,
    val price: String,
    val netTotal: String
)
