package com.hashmato.retailtouch.utils.printer

import com.hashmato.retailtouch.AndroidApp
import com.hashmato.retailtouch.domain.model.products.POSInvoicePrint
import com.hashmato.retailtouch.domain.model.settlement.PosSettlement
import com.hashmato.retailtouch.utils.PrinterType
import comhashmatoretailtouchsqldelight.Printers

actual class PrinterServiceProvider actual constructor(){
    private val printer = Printer()
    private val app = AndroidApp

    actual fun getAllUSBBDevices(): List<String> {
        val launchActivity = app.INSTANCE.currentActiveActivity
        if (launchActivity != null) {
            println("USBBDevices $launchActivity")
            val allUsbConnection = printer.getUSBConnections(app.getApplicationContext()) ?: emptyArray()
            println("callusConnection $allUsbConnection")
            return allUsbConnection.map { it.device.deviceName }
        }
        return emptyList()
    }

    actual fun getAllBluetoothDevices(): List<Pair<String, String>> {
        val launchActivity = app.INSTANCE.currentActiveActivity
        if (launchActivity != null) {
            println("BlueToothDevices $launchActivity")
            return printer.bluetoothDevices(launchActivity).map { Pair(it.name, it.address) }
        }
        return emptyList()
    }
    actual fun connectPrinterAndPrint(
        printers: Printers,
        printerType: PrinterType,
        textToPrint: String,
        openDrawer: Boolean
    ){
        printer.connectPrinter(printers = printers, printerType = printerType, textToPrint = textToPrint,openDrawer=openDrawer)
    }

    actual fun openCashDrawer(
        printers: Printers,
        printerType: PrinterType,
        textToPrint: String
    ) {
        printer.connectPrinter(printers, printerType, textToPrint)
    }

    actual suspend fun getPrintTextForReceiptTemplate(
        ticket: Any?,
        currencyCode: String,
        template: String,
        printers: Printers
    ): String {
        return printer.applyDynamicReceiptTemplate(ticket,currencyCode,template,printers)
    }
}