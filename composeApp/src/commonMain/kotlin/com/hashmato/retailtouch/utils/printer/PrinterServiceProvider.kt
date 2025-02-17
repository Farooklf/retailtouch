package com.hashmato.retailtouch.utils.printer

import com.hashmato.retailtouch.domain.model.products.POSInvoicePrint
import com.hashmato.retailtouch.domain.model.settlement.PosSettlement
import com.hashmato.retailtouch.utils.PrinterType
import comhashmatoretailtouchsqldelight.Printers

expect class PrinterServiceProvider(){
    fun getAllBluetoothDevices(): List<Pair<String,String>>
    fun getAllUSBBDevices(): List<String>
    fun connectPrinterAndPrint(
        printers: Printers,
        printerType: PrinterType,
        textToPrint: String,
    )

    //suspend fun getPrintTextForReceiptTemplate(ticket: Any?,currencyCode:String, template:String, printers: Printers):String
    //suspend fun getFormattedTemplateForSettlement(posSettlement: PosSettlement, template:String, printers: Printers):String


    suspend fun getPrintTextForReceiptTemplate(
        ticket: Any?,
        currencyCode: String,
        template: String,
        printers: Printers
    ): String

}