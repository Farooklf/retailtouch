package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.popup.Modal
import com.outsidesource.oskitcompose.popup.ModalStyles
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.alert_cancel
import retailtouch.composeapp.generated.resources.alert_ok
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.close
import retailtouch.composeapp.generated.resources.delete_payment
import retailtouch.composeapp.generated.resources.dialog_message
import retailtouch.composeapp.generated.resources.error
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.yes

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(Res.string.error),
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
fun SearchableTextFieldWithDialog(
    isVisible:Boolean,
    query: String="",
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
){

    if(isVisible){
        Dialog(onDismissRequest = { onDismiss() },
            properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false
        )){
            // Surface allows customization of the dialog's appearance
            Surface(
                modifier = Modifier.fillMaxWidth(0.80f)
                    .wrapContentHeight()
                    .padding(20.dp),
                shape = AppTheme.appShape.dialog,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                ){

                    SearchableTextWithBg(
                        value = query,
                        onValueChange = {
                            onQueryChange(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    content()
                }
            }

        }
    }

}


@Composable
fun AppDialog(
    isVisible: Boolean,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = 1000.dp,
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
        modifier = modifier.systemBarsPadding(),
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
            color = AppTheme.colors.backgroundDialog,
            shadowElevation = 10.dp
        ){
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()){
                content()
            }
        }
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
            contentColor = AppTheme.colors.textPrimaryBlue
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
    contentMaxWidth: Dp = 1000.dp,
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
fun ActionDialog(
    isVisible: Boolean,
    dialogTitle:String,
    dialogMessage:String,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = 1000.dp,
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
                color = AppTheme.colors.textColor,
                minLines= 1,
                maxLines = 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(Res.string.dialog_message,dialogMessage),
                style = AppTheme.typography.bodyNormal(),
                color = AppTheme.colors.textColor,
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
                    backgroundColor = AppTheme.colors.textPrimaryBlue,
                    contentColor = AppTheme.colors.textWhite
                )

            }

        }
    }
}




@Composable
fun CreateMemberDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = 1000.dp,
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
fun DiscountDialog(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = 1000.dp,
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
                    title = stringResource(Res.string.alert_ok),
                    onClick = onConfirm
                )
            }
        )
    }
}