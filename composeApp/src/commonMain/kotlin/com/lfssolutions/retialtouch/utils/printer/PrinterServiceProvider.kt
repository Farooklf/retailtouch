package com.lfssolutions.retialtouch.utils.printer

import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.domain.model.settlement.PosSettlement
import com.lfssolutions.retialtouch.utils.PrinterType
import comlfssolutionsretialtouch.Printers

expect class PrinterServiceProvider(){
    fun getAllBluetoothDevices(): List<Pair<String,String>>
    fun getAllUSBBDevices(): List<String>
    fun connectPrinterAndPrint(
        printers: Printers,
        printerType: PrinterType,
        textToPrint: String,
    )

    suspend fun getPrintTextForReceiptTemplate(posInvoice: PosInvoice, template:String,printers: Printers):String
    suspend fun getFormattedTemplateForSettlement(posSettlement: PosSettlement, template:String, printers: Printers):String
}