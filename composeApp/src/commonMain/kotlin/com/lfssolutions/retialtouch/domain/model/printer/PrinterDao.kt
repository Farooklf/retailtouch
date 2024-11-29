package com.lfssolutions.retialtouch.domain.model.printer


import kotlinx.serialization.Serializable

@Serializable
data class PrinterDao(
    val printerStationName: String,
    val printerName: String,
    val numbersOfCopies: Long,
    val paperSize: Long,
    val isReceipts: Boolean,
    val isOrders: Boolean,
    val isRefund: Boolean,
    val isPrinterEnable: Boolean,
    val printerType: Long,
    val networkIpAddress: String,
    val selectedBluetoothAddress: String,
    val selectedUsbId: String,
    val templateId:Long,
    val printerId:Long =0,
)
