package com.lfssolutions.retialtouch.utils.printer

import com.lfssolutions.retialtouch.AndroidApp
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.utils.PrinterType
import comlfssolutionsretialtouch.Printers

actual class PrinterServiceProvider actual constructor(){
    private val printer = Printer()
    private val app= AndroidApp
    actual fun getAllUSBBDevices(): List<String> {

        val allUsbConnection = printer.getUSBConnections(app.getApplicationContext()) ?: emptyArray()
        println("callusConnection $allUsbConnection")
        return allUsbConnection.map { it.device.deviceName }
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
    ){
        printer.connectPrinter(printers, printerType, textToPrint)

    }

    actual fun getPrintTextForReceiptTemplate(
        posInvoice: PosInvoice,
        template: String
    ): String {
        return printer.applyDynamicReceiptTemplate(posInvoice,template)
    }
}