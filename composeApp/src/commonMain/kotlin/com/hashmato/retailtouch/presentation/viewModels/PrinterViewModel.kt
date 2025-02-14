package com.hashmato.retailtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.model.printer.PrinterScreenState
import com.hashmato.retailtouch.domain.model.printer.PrinterTemplates
import com.hashmato.retailtouch.utils.PaperSize
import com.hashmato.retailtouch.utils.PrinterType
import com.hashmato.retailtouch.utils.printer.PrinterServiceProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PrinterViewModel :BaseViewModel(), KoinComponent {
    private val _screenState = MutableStateFlow(PrinterScreenState())
    val screenState: StateFlow<PrinterScreenState> = _screenState.asStateFlow()


    fun initialisePrinter(){
        viewModelScope.launch {
            dataBaseRepository.getPrinter().collect{printer->
                if(printer!=null){
                    _screenState.update { state->
                       state.copy(
                           isEditMode=true,
                           printerId = printer.printerId,
                           printerType = when(printer.printerType){
                                1L-> PrinterType.Ethernet
                                2L-> PrinterType.USB
                                3L-> PrinterType.Bluetooth
                               else -> {
                                   PrinterType.USB
                               }
                           },
                           printerStationName = printer.printerStationName,
                           numbersOfCopies = printer.noOfCopies?:1,
                           paperSize = when(printer.paperSize){
                               58L-> PaperSize.Size58mm
                               80L-> PaperSize.Size80mm
                               else -> {
                                   PaperSize.Size58mm
                               }
                           },
                           selectedBluetoothAddress=printer.bluetoothAddress?:"",
                           selectedUsbId =printer.usbId?:"",
                           networkIpAddress=printer.networkAddress?:"",
                           printerName=printer.printerName,
                           isReceipts = printer.isReceipts?:true,
                           isOrders = printer.isOrders?:true,
                           isRefund = printer.isRefund?:true
                       )
                    }
                }
            }
        }
    }

    fun resetScreenState(){
        viewModelScope.launch {
            _screenState.update { PrinterScreenState() }
        }
    }

    fun updateDialog(loader:Boolean) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(isLoading = loader) }
        }
    }

    fun setPrinter(tablet: Boolean) {
        viewModelScope.launch {
            _screenState.update {state->
                state.copy(isTablet = tablet)
            }
        }
    }
    fun updatePrinterType(type: PrinterType) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(printerType = type) }
        }
    }

    fun updatePrinterStationName(value: String) {
        viewModelScope.launch{
            _screenState.update { state -> state.copy(printerStationName = value) }
        }
    }

    fun updateNumbersOfCopies(value: String) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(numbersOfCopies = value.toLong()) }
        }
    }

    fun updatePrinterTemplateDialogVisibility(value: Boolean) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(showChoosePrinterTemplateDialog = value) }
        }
    }

    fun updatePaperSize(value: PaperSize) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(paperSize = value) }
        }
    }

    fun updateNetworkDialogVisibility(value: Boolean) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(showNetworkDialog = value) }
        }
    }

    fun updateUsbDeviceSelectionDialogVisibility(value: Boolean) {
        viewModelScope.launch {
            val usbDevicesList = PrinterServiceProvider().getAllUSBBDevices()
            _screenState.update { state ->
                state.copy(
                    showUsbDeviceSelectionDialog = value,
                    usbDevices = usbDevicesList
                )
            }
        }
    }

    fun updateBluetoothDeviceSelectionDialogVisibility(value: Boolean) {
        viewModelScope.launch{
            val bluetoothDeviceList = PrinterServiceProvider().getAllBluetoothDevices()
            _screenState.update { state ->
                state.copy(
                    showBluetoothSelectionDialog = if (value) bluetoothDeviceList.isNotEmpty() else value,
                    bluetoothDevices = bluetoothDeviceList
                )
            }
        }
    }

    fun updateReceiptsValue() {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(isReceipts = !state.isReceipts) }
        }
    }

    fun updateOrdersValue() {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(isOrders = !state.isOrders) }
        }
    }

    fun updatePrinterEnable() {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(isPrinterEnable = !state.isPrinterEnable) }
        }
    }


    fun updatePrintTemplate(printerTemplates: PrinterTemplates) {
        viewModelScope.launch {
            _screenState.update { state ->
                state.copy(
                    printerTemplates = printerTemplates,
                    showChoosePrinterTemplateDialog = false
                )
            }
        }
    }

    fun updateTerminalCodeDialogVisibility(value: Boolean) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(showTerminalCodeDialog = value) }
        }
    }

    fun updateTerminalCode(value: String) {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(terminalCode = value) }
            preferences.setTerminalCode(value)
        }
    }

    fun updateNetworkPrinter(value: String) {
        viewModelScope.launch {
            _screenState.update { state ->
                state.copy(
                    networkIpAddress = value,
                    showNetworkDialog = false,
                    showUsbDeviceSelectionDialog = false,
                    showBluetoothSelectionDialog = false,
                )
            }
        }
    }

    fun updatePrinterName(value: String) {
        viewModelScope.launch {
            _screenState.update { state ->
                state.copy(
                    printerName = value,
                    showNetworkDialog = false,
                    showUsbDeviceSelectionDialog = false,
                    showBluetoothSelectionDialog = false,
                )
            }
        }
    }

    fun updateBluetoothPrinter(selectedBluetooth: Pair<String, String>) {
        viewModelScope.launch {
            _screenState.update { state ->
                state.copy(
                    printerName = selectedBluetooth.first,
                    selectedBluetoothAddress = selectedBluetooth.second,
                    showNetworkDialog = false,
                    showUsbDeviceSelectionDialog = false,
                    showBluetoothSelectionDialog = false,
                )
            }
        }
    }

    fun dismissMessage() {
        viewModelScope.launch {
            _screenState.update { state -> state.copy(showMessage = false) }
            delay(1000)
            _screenState.update { state -> state.copy(backToScreen = true) }
        }
    }

    fun createPrinter() {
        viewModelScope.launch {
            updateDialog(true)
            delay(2000)
            dataBaseRepository.insertOrUpdatePrinter(screenState.value)
            updateDialog(false)
            _screenState.update { state->state.copy(showMessage=true) }
        }
    }


}