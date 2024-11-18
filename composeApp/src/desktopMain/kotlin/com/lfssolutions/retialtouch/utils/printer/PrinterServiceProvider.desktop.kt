package com.lfssolutions.retialtouch.utils.printer

import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.utils.PrinterType
import comlfssolutionsretialtouch.Printers

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

    actual fun getPrintTextForReceiptTemplate(
        posInvoice: PosInvoice,
        template: String
    ): String {
        return ""
    }

}