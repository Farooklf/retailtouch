package com.hashmato.retailtouch.presentation.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.ui.common.AppBaseCard
import com.hashmato.retailtouch.presentation.ui.common.AppCheckBox
import com.hashmato.retailtouch.presentation.ui.common.AppCircleProgressIndicator
import com.hashmato.retailtouch.presentation.ui.common.dialogs.AppDialogChoiceFromList
import com.hashmato.retailtouch.presentation.ui.common.AppPrimaryButton
import com.hashmato.retailtouch.presentation.ui.common.AppRadioButton
import com.hashmato.retailtouch.presentation.ui.common.AppSwitch
import com.hashmato.retailtouch.presentation.ui.common.BackgroundScreen
import com.hashmato.retailtouch.presentation.ui.common.dialogs.ChoosePrinterTemplateDialog
import com.hashmato.retailtouch.presentation.ui.common.dialogs.NetworkAddressDialog
import com.hashmato.retailtouch.presentation.ui.common.PrinterTextField
import com.hashmato.retailtouch.presentation.ui.common.dialogs.TerminalCodeDialog
import com.hashmato.retailtouch.presentation.ui.common.TopAppBarContent
import com.hashmato.retailtouch.presentation.ui.common.fillScreenHeight
import com.hashmato.retailtouch.presentation.viewModels.PrinterViewModel
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.LocalAppState
import com.hashmato.retailtouch.utils.PaperSize
import com.hashmato.retailtouch.utils.PrinterType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.add_printer
import retailtouch.composeapp.generated.resources.create_printer
import retailtouch.composeapp.generated.resources.edit_printer
import retailtouch.composeapp.generated.resources.enable_this_printer
import retailtouch.composeapp.generated.resources.enter_terminal_code
import retailtouch.composeapp.generated.resources.ic_printer
import retailtouch.composeapp.generated.resources.no_printer_selected
import retailtouch.composeapp.generated.resources.no_usb_devices
import retailtouch.composeapp.generated.resources.number_of_copies
import retailtouch.composeapp.generated.resources.orders
import retailtouch.composeapp.generated.resources.paper_size
import retailtouch.composeapp.generated.resources.printer_station_name
import retailtouch.composeapp.generated.resources.printer_type
import retailtouch.composeapp.generated.resources.receipts
import retailtouch.composeapp.generated.resources.select_bluetooth_device
import retailtouch.composeapp.generated.resources.select_usb_device
import retailtouch.composeapp.generated.resources.success_message
import retailtouch.composeapp.generated.resources.terminal_code_saved_successfully
import retailtouch.composeapp.generated.resources.update_printer

data object PrinterScreen: Screen {

    @Composable
    override fun Content() {
        PrinterContent()
    }
}

@Composable
fun PrinterContent(
    viewModel: PrinterViewModel = koinInject()
){

    val coroutineScope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

    LaunchedEffect(Unit){
        viewModel.initialisePrinter()
        viewModel.setPrinter(appState.isTablet)
    }

    DisposableEffect(key1 = viewModel) {
        onDispose {
            viewModel.resetScreenState()
        }
    }

    LaunchedEffect(state.backToScreen){
        if(state.backToScreen){
            NavigatorActions.navigateBack(navigator)
        }
    }

    LaunchedEffect(state.showMessage) {
        if (state.showMessage) {
            val message=getString(Res.string.success_message,if(!state.isEditMode) "Printer Added" else "Printer Updated")
            snackbarHostState.value.showSnackbar(message)
            viewModel.dismissMessage()
        }
    }


    BackgroundScreen(
        modifier = Modifier.systemBarsPadding(),
        appToolbarContent = {
            TopAppBarContent(
                title = if(!state.isEditMode) stringResource(Res.string.create_printer) else stringResource(Res.string.update_printer),
                showBackButton = true,
                isTablet = appState.isTablet,
                onBackClick = {navigator.pop()}
            )
        },
        contentMaxWidth = Int.MAX_VALUE.dp,
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillScreenHeight()
                .align(Alignment.TopCenter)
        ){
            Text(
                text = stringResource(Res.string.printer_type),
                style = AppTheme.typography.bodyNormal(),
                color = AppTheme.colors.textPrimary
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                PrinterTypeSelection(
                    modifier = Modifier.padding(top = 10.dp),
                    selectedType = state.printerType,
                    onTypeSelected = { viewModel.updatePrinterType(it) },
                    isTablet = appState.isTablet
                )

                PrinterTextField(
                    value = state.printerStationName,
                    onValueChange = { viewModel.updatePrinterStationName(it) },
                    label = stringResource(Res.string.printer_station_name),
                    fontSize = if (appState.isTablet) 18.sp else 14.sp
                )

                PrinterTextField(
                    value = state.numbersOfCopies.toString(),
                    onValueChange = { viewModel.updateNumbersOfCopies(it) },
                    label = stringResource(Res.string.number_of_copies),
                    fontSize = if (state.isTablet) 18.sp else 14.sp,
                    maxLength = 10,
                    isDigitInputOnly = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                /*PrinterTextField(
                    value = state.printerTemplates.name?:"",
                    onValueChange = { viewModel.updatePrinterStationName(it) },
                    onClick = { viewModel.updatePrinterTemplateDialogVisibility(true) },
                    label = stringResource(Res.string.select_print_template),
                    fontSize = if (state.isTablet) 18.sp else 14.sp,
                    enabled = false,
                )*/

                PaperSizeSelection(
                    selectedSize = state.paperSize,
                    onSelect = { viewModel.updatePaperSize(it) },
                    isTablet = state.isTablet
                )

                PrinterItem(
                    label = state.printerName ?: stringResource(Res.string.no_printer_selected),
                    onClick = {
                        when(state.printerType) {
                            PrinterType.Ethernet -> viewModel.updateNetworkDialogVisibility(true)
                            PrinterType.USB -> {
                                if (state.usbDevices.isEmpty()) {
                                    coroutineScope.launch {
                                        snackbarHostState.value.showSnackbar(getString(Res.string.no_usb_devices))
                                    }
                                } else {
                                    viewModel.updateUsbDeviceSelectionDialogVisibility(true)
                                }
                            }
                            PrinterType.Bluetooth ->{  viewModel.updateBluetoothDeviceSelectionDialogVisibility(true) }
                        }
                    },
                    isTablet = state.isTablet
                )

                ReceiptsOrdersItem(
                    isReceipts = state.isReceipts,
                    isOrders = state.isOrders,
                    onReceiptsClick = { viewModel.updateReceiptsValue() },
                    onOrdersClick = { viewModel.updateOrdersValue() },
                    isTablet = state.isTablet
                )


                EnablePrinterItem(
                    isEnabled = state.isPrinterEnable,
                    onClick = { viewModel.updatePrinterEnable() },
                    isTablet = state.isTablet
                )

                AppPrimaryButton(
                    label = stringResource(if (state.isEditMode) Res.string.edit_printer else Res.string.add_printer),
                    onClick = {
                        viewModel.createPrinter()
                        //NavigatorActions.navigateBack(navigator)
                        },
                    modifier = Modifier.height(if (state.isTablet) 60.dp else 50.dp).fillMaxWidth()
                )
            }

        }

        AppCircleProgressIndicator(
            isVisible=state.isLoading
        )

        SnackbarHost(
            hostState = snackbarHostState.value,
            modifier = Modifier
                .align(Alignment.TopCenter))
    }

    ChoosePrinterTemplateDialog(
        isVisible = state.showChoosePrinterTemplateDialog,
        selectedType = state.printerTemplates,
        onCloseDialog = { viewModel.updatePrinterTemplateDialogVisibility(false) },
        onDialogResult = { viewModel.updatePrintTemplate(it) },
        printerTemplateList = state.printerTemplatesList
    )

    TerminalCodeDialog(
        isVisible = state.showTerminalCodeDialog,
        onCloseDialog = { viewModel.updateTerminalCodeDialogVisibility(false) },
        onDialogResult = {
            if (it.isEmpty()) {
                coroutineScope.launch {
                    snackbarHostState.value.showSnackbar(getString(Res.string.enter_terminal_code))
                }
            } else {
                coroutineScope.launch {
                    viewModel.updateTerminalCode(it)
                    snackbarHostState.value.showSnackbar(getString(Res.string.terminal_code_saved_successfully))
                }
            }
        }
    )

    NetworkAddressDialog(
        isVisible = state.showNetworkDialog,
        onCloseDialog = { viewModel.updateNetworkDialogVisibility(false) },
        onDialogResult = { viewModel.updateNetworkPrinter(it) }
    )

    //USB
    AppDialogChoiceFromList(
        isVisible = state.showUsbDeviceSelectionDialog,
        list = state.usbDevices,
        title = stringResource(Res.string.select_usb_device),
        onDismissRequest = { viewModel.updateUsbDeviceSelectionDialogVisibility(false) },
        onDialogResult = { viewModel.updatePrinterName(state.usbDevices[it]) },
        contentMaxWidth = 600.dp
    )
    //Bluetooth
    AppDialogChoiceFromList(
        isVisible = state.showBluetoothSelectionDialog,
        list = state.bluetoothDevices.map { it.first },
        title = stringResource(Res.string.select_bluetooth_device),
        onDismissRequest = { viewModel.updateBluetoothDeviceSelectionDialogVisibility(false) },
        onDialogResult = { viewModel.updateBluetoothPrinter(state.bluetoothDevices[it]) },
        contentMaxWidth = 600.dp
    )
}

@Composable
private fun PrinterTypeSelection(
    modifier: Modifier = Modifier,
    selectedType: PrinterType,
    onTypeSelected: (PrinterType) -> Unit = {},
    isTablet: Boolean = false,
) {
    AppBaseCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrinterType.entries.forEachIndexed { index, printerType ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTypeSelected(printerType) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppRadioButton(
                        selected = selectedType == printerType,
                        onClick = { onTypeSelected(printerType) }
                    )

                    Text(
                        text = printerType.toStringValue(),
                        modifier = Modifier.weight(1f),
                        style = AppTheme.typography.titleMedium().copy(fontSize = if(isTablet) 18.sp else 14.sp),
                        color = AppTheme.colors.primaryColor
                    )
                    if (index != PrinterType.entries.size - 1) {
                        VerticalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun PaperSizeSelection(
    modifier: Modifier = Modifier,
    selectedSize: PaperSize,
    onSelect: (PaperSize) -> Unit = {},
    isTablet: Boolean,
) {
    AppBaseCard(modifier = modifier) {

        Column {
            Text(
                text = stringResource(Res.string.paper_size),
                modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                style = AppTheme.typography.bodyNormal(),
                color = AppTheme.colors.secondaryText
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ) {

                PaperSize.entries.forEach { paperSize ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onSelect(paperSize) }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppRadioButton(
                            selected = selectedSize == paperSize,
                            onClick = { onSelect(paperSize) }
                        )

                        Text(
                            text = paperSize.toStringValue(),
                            modifier = Modifier.weight(1f),
                            style = AppTheme.typography.titleMedium()
                                .copy(fontSize = if (isTablet) 18.sp else 14.sp),
                            color = AppTheme.colors.primaryText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrinterItem(
    label: String,
    onClick: () -> Unit = {},
    isTablet: Boolean = false,
) {
    AppBaseCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Image(
                painter = painterResource(Res.drawable.ic_printer),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )

            Text(
                text = label,
                modifier = Modifier.padding(vertical = 20.dp),
                style = AppTheme.typography.bodyNormal().copy(fontSize = if (isTablet) 18.sp else 15.sp),
                color = AppTheme.colors.primaryText
            )
        }
    }
}


@Composable
private fun ReceiptsOrdersItem(
    isReceipts: Boolean,
    isOrders: Boolean,
    onReceiptsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    isTablet: Boolean = false
) {
    AppBaseCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(10.dp)
        ) {

            CheckBoxItem(
                label = stringResource(Res.string.receipts),
                modifier = Modifier.weight(1f),
                checked = isReceipts,
                onClick = onReceiptsClick,
                isTablet = isTablet
            )

            VerticalDivider()

            CheckBoxItem(
                label = stringResource(Res.string.orders),
                modifier = Modifier.weight(1f),
                checked = isOrders,
                onClick = onOrdersClick,
                isTablet = isTablet
            )
        }
    }
}

@Composable
private fun CheckBoxItem(
    label: String,
    modifier: Modifier = Modifier,
    checked: Boolean,
    onClick: () -> Unit = {},
    isTablet: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = AppTheme.typography.bodyMedium().copy(fontSize = if (isTablet) 18.sp else 14.sp),
            color = AppTheme.colors.primaryText
        )

        AppCheckBox(
            checked = checked,
            onCheckedChange = { onClick() }
        )
    }
}

@Composable
private fun EnablePrinterItem(
    isEnabled: Boolean,
    onClick: () -> Unit = {},
    isTablet: Boolean = false
) {
    AppBaseCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.enable_this_printer),
                modifier = Modifier.weight(1f),
                style = AppTheme.typography.bodyMedium().copy(fontSize = if (isTablet) 18.sp else 14.sp),
                color = AppTheme.colors.primaryText
            )

            AppSwitch(
                checked = isEnabled,
                onCheckedChange = { onClick() }
            )
        }
    }
}