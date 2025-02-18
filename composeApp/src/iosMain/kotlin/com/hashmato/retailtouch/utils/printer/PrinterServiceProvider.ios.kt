package com.hashmato.retailtouch.utils.printer

import com.hashmato.retailtouch.domain.model.products.PosInvoice
import com.hashmato.retailtouch.domain.model.settlement.PosSettlement
import com.hashmato.retailtouch.utils.PrinterType
import comhashmatoretailtouchsqldelight.Printers

actual class PrinterServiceProvider actual constructor() {
    actual fun getAllBluetoothDevices(): List<Pair<String, String>> {
        return emptyList()
    }

    actual fun getAllUSBBDevices(): List<String> {
        return emptyList()
    }

    actual fun connectPrinterAndPrint(
        printers: Printers,
        printerType: PrinterType,
        textToPrint: String
    ) {
    }



    /*actual fun getFormattedTemplateForSettlement(
        posSettlement: PosSettlement,
        template: String,
        printers: Printers
    ): String {
        return ""
    }*/


    actual suspend fun getPrintTextForReceiptTemplate(
        ticket: Any?,
        currencyCode: String,
        template: String,
        printers: Printers
    ): String {
      return ""
    }

    actual fun openCashDrawer() {
    }

}

