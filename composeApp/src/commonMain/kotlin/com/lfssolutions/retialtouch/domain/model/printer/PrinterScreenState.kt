package com.lfssolutions.retialtouch.domain.model.printer

import com.lfssolutions.retialtouch.utils.PaperSize
import com.lfssolutions.retialtouch.utils.PrinterType

data class PrinterScreenState(
    val isTablet: Boolean = false,
    val isEditMode: Boolean = false,
    val printerType: PrinterType = PrinterType.USB,
    val printerStationName: String = "",
    val numbersOfCopies: Int = 1,
    val paperSize: PaperSize = PaperSize.Size58mm,
    val printerName: String? = null,
    val isReceipts: Boolean = true,
    val isOrders: Boolean = true,
    val isRefund: Boolean = true,
    val backToScreen: Boolean = false,
    val isPrinterEnable: Boolean = true,
    val networkIpAddress: String = "",
    val selectedBluetoothAddress: String= "",
    val selectedUsbId: String= "",
    val terminalCode: String? = null,
    val usbDevices: List<String> = emptyList(),
    val bluetoothDevices: List<Pair<String,String>> = emptyList(),
    val printerTemplatesList: List<PrinterTemplates> = emptyList(),
    val printerTemplates: PrinterTemplates = PrinterTemplates(
        id = 10101,
        name = "Default Template",
        mergeLines = false,
        contents = "",
        files = ""
    ),

    val showChoosePrinterTemplateDialog: Boolean = false,
    val showTerminalCodeDialog: Boolean = false,
    val showNetworkDialog: Boolean = false,
    val showUsbDeviceSelectionDialog: Boolean = false,
    val showBluetoothSelectionDialog: Boolean = false,
)