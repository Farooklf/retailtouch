package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.printer.PrinterScreenState
import com.lfssolutions.retialtouch.domain.model.printer.PrinterTemplates
import com.lfssolutions.retialtouch.utils.PaperSize
import com.lfssolutions.retialtouch.utils.PrinterType
import com.lfssolutions.retialtouch.utils.printer.PrinterServiceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PrinterViewModel :BaseViewModel(), KoinComponent {
    private val _screenState = MutableStateFlow(PrinterScreenState())
    val screenState: StateFlow<PrinterScreenState> = _screenState.asStateFlow()


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
            _screenState.update { state -> state.copy(numbersOfCopies = value.toInt()) }
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

    fun createPrinter() {
        viewModelScope.launch {
            dataBaseRepository.insertPrinter(screenState.value)
            _screenState.update { state->state.copy(backToScreen=true) }
        }
    }


}