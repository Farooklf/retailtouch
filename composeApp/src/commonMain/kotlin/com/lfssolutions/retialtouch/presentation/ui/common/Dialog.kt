package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetDefaults.properties
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lfssolutions.retialtouch.domain.model.printer.PrinterTemplates
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.popup.Modal
import com.outsidesource.oskitcompose.popup.ModalStyles
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.alert_cancel
import retailtouch.composeapp.generated.resources.alert_close
import retailtouch.composeapp.generated.resources.alert_ok
import retailtouch.composeapp.generated.resources.alert_save
import retailtouch.composeapp.generated.resources.alert_yes
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.choose_printer_template
import retailtouch.composeapp.generated.resources.close
import retailtouch.composeapp.generated.resources.delete_payment
import retailtouch.composeapp.generated.resources.dialog_message
import retailtouch.composeapp.generated.resources.enter_terminal_code
import retailtouch.composeapp.generated.resources.error
import retailtouch.composeapp.generated.resources.ic_add
import retailtouch.composeapp.generated.resources.ic_printer
import retailtouch.composeapp.generated.resources.ic_success
import retailtouch.composeapp.generated.resources.network_dialog_hint
import retailtouch.composeapp.generated.resources.network_dialog_title
import retailtouch.composeapp.generated.resources.new_order
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.payment_success
import retailtouch.composeapp.generated.resources.print_receipts
import retailtouch.composeapp.generated.resources.search
import retailtouch.composeapp.generated.resources.search_items
import retailtouch.composeapp.generated.resources.terminal_code
import retailtouch.composeapp.generated.resources.yes


@Composable
fun AppDialog(
    isVisible: Boolean,
    bgColor:Color=AppTheme.colors.backgroundDialog,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    isFullScreen: Boolean = false,
    content: @Composable () -> Unit
) {

    val appState = LocalAppState.current
    val  padding=when(appState.isPortrait){
        true-> {
            PaddingValues(horizontal = AppTheme.dimensions.phoneHorPadding, vertical = AppTheme.dimensions.phoneVerPadding)
        }
        false->{
            PaddingValues(horizontal = AppTheme.dimensions.tabHorPadding, vertical = AppTheme.dimensions.tabVerPadding)
        }
    }

    Modal(
        isVisible = isVisible,
        modifier = modifier.padding(padding),
        onDismissRequest = onDismissRequest,
        dismissOnBackPress = properties.dismissOnBackPress,
        dismissOnExternalClick = properties.dismissOnClickOutside,
        styles = ModalStyles.UserDefinedContent,
        isFullScreen = isFullScreen
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = contentMaxWidth)
                .wrapContentHeight()
                .padding(padding),
            shape = AppTheme.appShape.dialog,
            color = bgColor,
            shadowElevation = 10.dp
        ){
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()){
                content()
            }
        }
    }
}


@Composable
fun ErrorDialog(
    errorTitle: String=stringResource(Res.string.error),
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = errorTitle,
                style = AppTheme.typography.errorTitle()
            )
        },
        text = {
            Text(text = errorMessage,
                style = AppTheme.typography.errorBody()
            )
        },
        confirmButton = {
            AppCloseButton(
                onClick = {
                    onDismiss()
                },
                label = stringResource(Res.string.close) ,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(vertical = 10.dp)
            )
        },
        shape = AppTheme.appShape.dialog // Dialog with rounded corners
    )
}

@Composable
fun SearchableTextField(
    isVisible:Boolean,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
){

    AppDialog(
        isVisible = isVisible,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false
        ),
        onDismissRequest = onDismiss,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = false,
    ){
        content.invoke()
    }
}


@Composable
fun AppDialogContent(
    title: String,
    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = AppTheme.typography.titleMedium(),
    titleIcon: DrawableResource? = null,
    body: @Composable ColumnScope.() -> Unit,
    buttons: @Composable RowScope.() -> Unit,
) {
    Column(
        modifier = modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            titleIcon?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = title,
                )
            }

            Text(
                text = title,
                style = titleTextStyle,
                color = AppTheme.colors.textPrimary
            )
        }

        body()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            buttons()
        }
    }
}

@Composable
fun AppDialogButton(
    title: String,
    onClick: () -> Unit = {}
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = AppTheme.colors.primaryColor
        )
    ) {
        Text(
            text = title,
            style = AppTheme.typography.bodyMedium(),
        )
    }
}

@Composable
fun MemberListDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    dialogBody: @Composable () -> Unit
){
    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
    ){
        dialogBody()
    }
}

@Composable
fun HoldSaleDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    dialogBgColor: Color = Color.Transparent,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    dialogBody: @Composable () -> Unit
){
    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
        bgColor = dialogBgColor
    ){
        dialogBody()
    }
}


@Composable
fun ActionDialog(
    isVisible: Boolean,
    dialogTitle:String,
    dialogMessage:String,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(dismissOnBackPress = false,dismissOnClickOutside = false),
    onDismissRequest: () -> Unit,
    onYes: () -> Unit,
    onCancel: () -> Unit
){
    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        modifier = modifier.padding(10.dp),
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
    ){
        Column(modifier= Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spaceBetweenPadded(20.dp)) {

            Text(
                text = dialogTitle,
                style = AppTheme.typography.bodyMedium(),
                color = AppTheme.colors.primaryText,
                minLines= 1,
                maxLines = 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(Res.string.dialog_message,dialogMessage),
                style = AppTheme.typography.bodyNormal(),
                color = AppTheme.colors.primaryText,
                minLines= 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )

            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {

                AppBorderButton(
                    onClick = {
                       onCancel()
                    },
                    modifier=Modifier.wrapContentSize().padding(horizontal = 10.dp),
                    label = stringResource(Res.string.cancel)
                )

                AppPrimaryButton(
                    onClick = {
                        onYes()
                    },
                    modifier=Modifier.wrapContentSize().padding(horizontal = 10.dp),
                    label = stringResource(Res.string.yes),
                    backgroundColor = AppTheme.colors.primaryColor,
                    contentColor = AppTheme.colors.appWhite
                )

            }

        }
    }
}


@Composable
fun CreateMemberDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    dialogBody: @Composable () -> Unit
){
    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
    ){
        dialogBody()
    }
}


@Composable
fun DiscountDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    dialogBody: @Composable () -> Unit
){
    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        modifier = modifier.padding(10.dp),
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
    ){
        dialogBody()
    }
}

@Composable
fun DeletePaymentModeDialog(
    isVisible: Boolean,
    payment: String = "CASH",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {

    AppDialog(
        isVisible = isVisible,
        onDismissRequest = onDismiss,
        contentMaxWidth = 600.dp
    ) {
        AppDialogContent(
            title = stringResource(Res.string.payment),
            body = {
                Text(
                    text = stringResource(Res.string.delete_payment, payment),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = AppTheme.typography.bodyNormal(),
                )
            },
            buttons = {
                AppDialogButton(
                    title = stringResource(Res.string.alert_cancel),
                    onClick = onDismiss
                )
                AppDialogButton(
                    title = stringResource(Res.string.alert_yes),
                    onClick = onConfirm
                )
            }
        )
    }
}

@Composable
fun PaymentSuccessDialog(
    isVisible: Boolean = false,
    onDismiss: () -> Unit = {},
    onPrinting: () -> Unit = {},
    appliedPayments: Double,
    balance: Double,

) {

    /*CustomerDetailsDialog(
        isVisible = state.showEmailReceiptsDialog,
        type = CustomerDetailsDialogType.Email,
        onCancelClick = { viewModel.updateEmailReceiptsDialogVisibility(false) }
    )

    CustomerDetailsDialog(
        isVisible = state.showPhoneReceiptsDialog,
        type = CustomerDetailsDialogType.Phone,
        onCancelClick = { viewModel.updatePhoneReceiptsDialogVisibility(false) }
    )*/

    val dialogText = if (balance>0) {
        "Change :$balance \n\n Out of $appliedPayments"

    }else {
        "Total :$appliedPayments"
    }

    AppDialog(
        isVisible = isVisible,
        modifier = Modifier.systemBarsPadding(),
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        contentMaxWidth = 800.dp,
        isFullScreen = false
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.colors.appWhite)
                .padding(
                    vertical = 20.dp,
                    horizontal = 30.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_success),
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimensions.largeIcon)
            )

            Text(
                text = stringResource(Res.string.payment_success),
                style = AppTheme.typography.bodyBlack(),
                color = AppTheme.colors.primaryText,
                textAlign = TextAlign.Center
            )

            /*Text(
                text = "Balance : ${abs(balance)}",
                style = AppTheme.typography.h1Black().copy(fontSize = 25.sp),
                color = AppTheme.colors.primaryText,
                textAlign = TextAlign.Center
            )*/

            Text(
                text = dialogText,
                style = AppTheme.typography.h1Black(),
                color = AppTheme.colors.primaryText,
                textAlign = TextAlign.Center
            )

            /*Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                DialogButton(
                    onClick = { interactor.updateEmailReceiptsDialogVisibility(true) },
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.email_receipts),
                    icon = Res.drawable.ic_email
                )

                DialogButton(
                    onClick = { interactor.updatePhoneReceiptsDialogVisibility(true) },
                    modifier = Modifier.weight(1f),
                    label = stringResource(Res.string.phone_receipts),
                    icon = Res.drawable.ic_phonerecipt
                )
            }*/

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                DialogButton(
                    onClick = {onPrinting.invoke()},
                    modifier = Modifier.weight(1f).height(IntrinsicSize.Max),
                    label = stringResource(Res.string.print_receipts),
                    icon = Res.drawable.ic_printer
                )

                DialogButton(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(IntrinsicSize.Max),
                    label = stringResource(Res.string.new_order),
                    primaryText = AppTheme.colors.appWhite,
                    textStyle = AppTheme.typography.bodyBold(),
                    backgroundColor = AppTheme.colors.appGreen,
                    icon = Res.drawable.ic_add
                )
            }
        }
    }
}

@Composable
private fun DialogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    textStyle: TextStyle = AppTheme.typography.captionNormal(),
    icon: DrawableResource? = null,
    fillMaxWidth: Boolean = true,
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    backgroundColor: Color = AppTheme.colors.primaryColor,
    primaryText: Color = AppTheme.colors.appWhite,
    iconColor: Color = AppTheme.colors.appWhite,
) {
    BaseButton(
        onClick = onClick,
        modifier = modifier,
        interactionSource = remember { MutableInteractionSource() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
        ),
        elevation = elevation,
        content = {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = iconColor
                )
            }

            Text(
                text = label,
                style = textStyle,
                color = primaryText,
                modifier = Modifier
                    .then(if (fillMaxWidth) {
                        Modifier.weight(1f)
                    } else {
                        Modifier.padding(horizontal = 10.dp)
                    }),
                textAlign = TextAlign.Center,
            )
        }
    )
}

@Composable
fun ChoosePrinterTemplateDialog(
    isVisible: Boolean = false,
    selectedType: PrinterTemplates,
    onCloseDialog: () -> Unit = {},
    onDialogResult: (PrinterTemplates) -> Unit = {},
    printerTemplateList:List<PrinterTemplates>
) {
    AppDialog(
        isVisible = isVisible,
        onDismissRequest = onCloseDialog,
        contentMaxWidth = 600.dp
    ) {
        AppDialogContent(
            title = stringResource(Res.string.choose_printer_template),
            modifier = Modifier.padding(bottom = 10.dp),
            body = {
                printerTemplateList.forEach { template ->
                    AppRadioButtonWithText(
                        title = template.name?:"",
                        selected = template == selectedType,
                        isClickable = true,
                        onClick = { onDialogResult(template) }
                    )
                }
            },
            buttons = {
                AppDialogButton(
                    title = stringResource(Res.string.alert_cancel),
                    onClick = onCloseDialog
                )
            }
        )
    }
}

@Composable
fun TerminalCodeDialog(
    isVisible: Boolean,
    onCloseDialog: () -> Unit = {},
    onDialogResult: (String) -> Unit = {},
    codeMaxLength: Int = 4,
) {
    var displayedValue by remember { mutableStateOf("") }

    AppDialog(
        isVisible = isVisible,
        onDismissRequest = onCloseDialog,
        contentMaxWidth = 600.dp
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = stringResource(Res.string.terminal_code),
                modifier = Modifier.padding(top = 10.dp),
                style = AppTheme.typography.header(),
                color = AppTheme.colors.primaryText
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = AppTheme.colors.listBorderColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = displayedValue,
                    onValueChange = {
                        val filtered = it.filter { symbol ->
                            symbol.isDigit()
                        }

                        if (codeMaxLength <= 0 || filtered.length <= codeMaxLength) {
                            displayedValue = filtered
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    interactionSource = remember { MutableInteractionSource() },
                )

                if (displayedValue.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.enter_terminal_code),
                        style = AppTheme.typography.bodyNormal(),
                        color = AppTheme.colors.secondaryText
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppPrimaryButton(
                    label = stringResource(Res.string.alert_close),
                    modifier = Modifier.weight(1f),
                    onClick = onCloseDialog
                )

                AppPrimaryButton(
                    label = stringResource(Res.string.alert_save),
                    modifier = Modifier.weight(1f),
                    onClick = { onDialogResult(displayedValue) }
                )
            }
        }
    }
}

@Composable
fun NetworkAddressDialog(
    isVisible: Boolean,
    onCloseDialog: () -> Unit = {},
    onDialogResult: (String) -> Unit = {},
) {
    var displayedValue by remember { mutableStateOf("") }

    AppDialog(
        isVisible = isVisible,
        onDismissRequest = onCloseDialog,
        contentMaxWidth = 600.dp
    ) {
        AppDialogContent(
            title = stringResource(Res.string.network_dialog_title),
            titleTextStyle = AppTheme.typography.bodyNormal(),
            body = {
                AppDialogTextField(
                    value = displayedValue,
                    onValueChange = { displayedValue = it },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    placeholder = stringResource(Res.string.network_dialog_hint)
                )
            },
            buttons = {
                AppDialogButton(
                    title = stringResource(Res.string.alert_cancel),
                    onClick = onCloseDialog
                )

                AppDialogButton(
                    title = stringResource(Res.string.alert_ok),
                    onClick = {
                        onDialogResult(displayedValue)
                        displayedValue = ""
                    }
                )
            }
        )
    }
}

@Composable
fun AppDialogChoiceFromList(
    isVisible: Boolean,
    list: List<String>,
    title: String,
    selectedIndex: Int = 0,
    onDismissRequest: () -> Unit,
    onDialogResult: (Int) -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    titleTextStyle: TextStyle = AppTheme.typography.titleNormal(),
    contentMaxWidth: Dp = 1000.dp,
) {
    var displayedIndex by remember(selectedIndex) {
        mutableStateOf(selectedIndex)
    }

    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        modifier = modifier.padding(10.dp),
        contentMaxWidth = contentMaxWidth,
    ) {
        AppDialogContent(
            title = title,
            titleTextStyle = titleTextStyle,
            body = {
                list.forEachIndexed { index, value ->
                    AppRadioButtonWithText(
                        title = value,
                        selected = index == displayedIndex,
                        isClickable = true,
                        onClick = { displayedIndex = index }
                    )
                }
            },
            buttons = {
                AppDialogButton(
                    title = stringResource(Res.string.alert_cancel),
                    onClick = onDismissRequest
                )

                AppDialogButton(
                    title = stringResource(Res.string.alert_ok),
                    onClick = { onDialogResult(displayedIndex) }
                )
            }
        )
    }
}