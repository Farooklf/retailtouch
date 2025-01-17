package com.lfssolutions.retialtouch.presentation.ui.common.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleTransactionDetailsState
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale
import com.lfssolutions.retialtouch.domain.model.printer.PrinterTemplates
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.products.PosUIState
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.AppBaseCard
import com.lfssolutions.retialtouch.presentation.ui.common.AppCloseButton
import com.lfssolutions.retialtouch.presentation.ui.common.AppDialogTextField
import com.lfssolutions.retialtouch.presentation.ui.common.AppHorizontalDivider
import com.lfssolutions.retialtouch.presentation.ui.common.AppPrimaryButton
import com.lfssolutions.retialtouch.presentation.ui.common.AppRadioButtonWithText
import com.lfssolutions.retialtouch.presentation.ui.common.BaseButton
import com.lfssolutions.retialtouch.presentation.ui.common.DiscountTabCard
import com.lfssolutions.retialtouch.presentation.ui.common.InputType
import com.lfssolutions.retialtouch.presentation.ui.common.ListCenterText
import com.lfssolutions.retialtouch.presentation.ui.common.ListText
import com.lfssolutions.retialtouch.presentation.ui.common.MemberList
import com.lfssolutions.retialtouch.presentation.ui.common.NumberPad
import com.lfssolutions.retialtouch.presentation.ui.common.SearchableTextWithBg
import com.lfssolutions.retialtouch.presentation.ui.common.StokesListItem
import com.lfssolutions.retialtouch.presentation.ui.common.VectorIcons
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getDateTimeFromEpochMillSeconds
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getEpochTimestamp
import com.lfssolutions.retialtouch.utils.DiscountType
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.lfssolutions.retialtouch.utils.formatPrice
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.lib.ValRef
import com.outsidesource.oskitcompose.popup.Modal
import com.outsidesource.oskitcompose.popup.ModalStyles
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.alert_cancel
import retailtouch.composeapp.generated.resources.alert_close
import retailtouch.composeapp.generated.resources.alert_ok
import retailtouch.composeapp.generated.resources.alert_save
import retailtouch.composeapp.generated.resources.alert_yes
import retailtouch.composeapp.generated.resources.app_logo
import retailtouch.composeapp.generated.resources.barcode
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.choose_printer_template
import retailtouch.composeapp.generated.resources.clear_promotions
import retailtouch.composeapp.generated.resources.close
import retailtouch.composeapp.generated.resources.confirm
import retailtouch.composeapp.generated.resources.date
import retailtouch.composeapp.generated.resources.delete_payment
import retailtouch.composeapp.generated.resources.description
import retailtouch.composeapp.generated.resources.enter_terminal_code
import retailtouch.composeapp.generated.resources.error
import retailtouch.composeapp.generated.resources.grid_view_option_value
import retailtouch.composeapp.generated.resources.held_tickets
import retailtouch.composeapp.generated.resources.ic_add
import retailtouch.composeapp.generated.resources.ic_printer
import retailtouch.composeapp.generated.resources.ic_success
import retailtouch.composeapp.generated.resources.in_stock
import retailtouch.composeapp.generated.resources.network_dialog_hint
import retailtouch.composeapp.generated.resources.network_dialog_title
import retailtouch.composeapp.generated.resources.new_order
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.payment_success
import retailtouch.composeapp.generated.resources.price
import retailtouch.composeapp.generated.resources.print_receipts
import retailtouch.composeapp.generated.resources.promotion_discounts
import retailtouch.composeapp.generated.resources.qty
import retailtouch.composeapp.generated.resources.receipt_no
import retailtouch.composeapp.generated.resources.round_off_description
import retailtouch.composeapp.generated.resources.search_items
import retailtouch.composeapp.generated.resources.sku
import retailtouch.composeapp.generated.resources.sync_all
import retailtouch.composeapp.generated.resources.terminal_code
import retailtouch.composeapp.generated.resources.yes

@Composable
fun CartLoader(
    isVisible: Boolean = false,
    label: String = "Getting Menus"
) {

    Modal(
        isVisible = isVisible,
        modifier = Modifier.padding(10.dp),
        onDismissRequest = { } ,
        dismissOnBackPress = false,
        dismissOnExternalClick = false,
        styles = ModalStyles.UserDefinedContent,
        isFullScreen = false
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .size(500.dp)
                .background(AppTheme.colors.cardBgColor)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier.size(300.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.secondaryBg)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier,
                    color = AppTheme.colors.brand,
                    strokeWidth = 5.dp
                )

                androidx.compose.material3.Text(
                    text = label,
                    modifier = Modifier.padding(start = 10.dp),
                    style = AppTheme.typography.h1Bold(),
                    color = AppTheme.colors.primaryText
                )
            }
        }
    }
}

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
fun StockDialog(
    isVisible:Boolean,
    interactorRef: ValRef<SharedPosViewModel>,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    onDismiss: () -> Unit,
    onItemClick: (Product) -> Unit,
){
    val state by interactorRef.value.posUIState.collectAsStateWithLifecycle()
    val viewModel =interactorRef.value

    val appState = LocalAppState.current
    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding20

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
        if(state.stockList.isEmpty())
            viewModel.loadAllProducts()

        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().background(AppTheme.colors.screenBackground)
        ){
            SearchableTextWithBg(
                value = state.searchQuery,
                leadingIcon=AppIcons.searchIcon,
                placeholder = stringResource(Res.string.search_items),
                label = stringResource(Res.string.search_items),
                modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10),
                onValueChange = {
                    viewModel.updateSearchQuery(it)
                }
            )

            //List Content
            CommonListHeader()
            // Display filtered products in a LazyColumn
            LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(AppTheme.colors.secondaryBg)){
                // Filter the product tax list based on the search query
                val filteredProducts = state.stockList.filter { it.matches(state.searchQuery) }.toMutableList()
                itemsIndexed(filteredProducts){ index, product ->
                    StokesListItem(position=index,product=product,currencySymbol=state.currencySymbol, onClick = { selectedItem->
                        onItemClick.invoke(selectedItem)
                    })
                }
            }
        }

    }
}


@Composable
fun CommonListHeader(){
    val appState = LocalAppState.current
    val textStyle=if(appState.isPortrait)
        AppTheme.typography.bodyBold()
    else
        AppTheme.typography.titleBold()

    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding20

    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)

    ){
        //SKU
        // Adjust the weight proportions
        ListText(
            label = stringResource(Res.string.sku).uppercase(),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1.2f)
        )
        ListText(
            label = stringResource(Res.string.barcode),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1.2f))
        ListText(
            label = stringResource(Res.string.price),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1f))
        ListText(
            label = stringResource(Res.string.in_stock),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1f))

        if(!appState.isPortrait)
        {
            ListText(
                label = stringResource(Res.string.description),
                color = AppTheme.colors.textBlack,
                textStyle = textStyle,
                modifier = Modifier.weight(1.5f))
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
    interactorRef: ValRef<SharedPosViewModel>,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = 600.dp,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit,
){
    val state by interactorRef.value.posUIState.collectAsStateWithLifecycle()
    val viewModel =interactorRef.value
    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
    ){
        MemberList(
            posUIState=state,
            posViewModel=viewModel,
            onMemberCreate = {
                //create Member dialog
                viewModel.updateCreateMemberDialogState(true)
            }
        )
    }
}

@Composable
fun ActionDialog(
    isVisible: Boolean,
    dialogTitle:String,
    dialogMessage:String,
    confirmButtonTxt:String?=null,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxSmallWidth,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(dismissOnBackPress = false,dismissOnClickOutside = false),
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
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
                text = dialogMessage,
                style = AppTheme.typography.bodyNormal(),
                color = AppTheme.colors.primaryText,
                minLines= 1,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )

            Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {

                AppPrimaryButton(
                    onClick = {
                       onCancel()
                    },
                    modifier=Modifier.wrapContentHeight().padding(horizontal = 10.dp),
                    label = stringResource(Res.string.cancel),
                    backgroundColor = AppTheme.colors.appRed
                )

                AppPrimaryButton(
                    onClick = {
                        onConfirm()
                    },
                    modifier=Modifier.wrapContentHeight().padding(horizontal = 10.dp),
                    label = confirmButtonTxt?:stringResource(Res.string.yes),
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
fun HoldSaleDialog(
    posState:PosUIState,
    isVisible: Boolean,
    isPortrait: Boolean=true,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxMidWidth,
    dialogBgColor: Color = AppTheme.colors.backgroundDialog,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(),
    onDismiss: () -> Unit,
    onItemClick: (CRSaleOnHold) -> Unit,
    onRemove : (Long) -> Unit,
){
    val (vertPadding,horPadding)=if(isPortrait)
        AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20

    val textStyleHeader=if(isPortrait)
        AppTheme.typography.bodyBold().copy(fontSize = 18.sp)
    else
        AppTheme.typography.titleBold().copy(fontSize = 20.sp)

    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismiss,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
        bgColor = dialogBgColor
    ){
        Column(modifier=Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = AppTheme.dimensions.padding10, horizontal = AppTheme.dimensions.padding5), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            VectorIcons(
                icons = AppIcons.cancelIcon,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                iconSize = AppTheme.dimensions.smallIcon,
                iconColor=AppTheme.colors.appRed,
                onClick = {
                    onDismiss.invoke()
                })

            Text(
                text = stringResource(Res.string.held_tickets),
                color = AppTheme.colors.textBlack,
                style = textStyleHeader
            )

            LazyColumn(modifier = Modifier.fillMaxWidth().wrapContentHeight()){
                itemsIndexed(posState.salesOnHold.toList()
                ){index, (key, saleOnHold) ->

                    HoldTicketListItem(
                        index = index,
                        item = saleOnHold,
                        symbol = posState.currencySymbol,
                        isPortrait = isPortrait,
                        horizontalPadding=horPadding,
                        verticalPadding=vertPadding,
                        onRemove = {
                          onRemove.invoke(key)
                        },
                        onAdd = {
                            onItemClick.invoke(saleOnHold)
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun HoldTicketListItem(
    index:Int,
    item: CRSaleOnHold,
    symbol: String,
    isPortrait: Boolean,
    horizontalPadding:Dp,
    verticalPadding:Dp,
    onAdd: (CRSaleOnHold) -> Unit,
    onRemove: () -> Unit,
){
    val (textStyle,headerStyle)=if(isPortrait)
        AppTheme.typography.captionMedium() to AppTheme.typography.bodyBold()
    else
        AppTheme.typography.bodyMedium() to AppTheme.typography.titleBold()

    AppBaseCard(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(5.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = verticalPadding).clickable { onAdd.invoke(item) },
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding5), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.fillMaxWidth(),Arrangement.spacedBy(5.dp)) {
                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(
                        text = "${index+1}. $symbol${item.grandTotal.roundTo(2)}",
                        style = headerStyle,
                        color = AppTheme.colors.textPrimary
                    )

                    VectorIcons(
                        modifier = Modifier.wrapContentWidth(),
                        icons = AppIcons.removeIcon,
                        iconSize = AppTheme.dimensions.smallXIcon,
                        onClick = {
                            onRemove.invoke()
                        })
                }

                item.items.forEachIndexed { index,element->
                    Text(
                        text = "${index+1}.${element.stock.name}[${element.stock.inventoryCode}]",
                        style = textStyle,
                        color = AppTheme.colors.textDarkGrey,
                        modifier = Modifier.fillMaxWidth().padding(AppTheme.dimensions.padding3)
                    )
                }
            }

        }
    }
}

@Composable
fun PendingSaleDialog(
    isVisible: Boolean,
    isSync: Boolean=false,
    pendingSales: List<PendingSale>,
    currency:String,
    modifier: Modifier = Modifier.fillMaxWidth().fillMaxHeight(),
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxWidth,
    dialogBgColor: Color = AppTheme.colors.backgroundDialog,
    isFullScreen: Boolean = true,
    properties: DialogProperties = DialogProperties(),
    onDismiss: () -> Unit,
    onRemove : (PendingSale) -> Unit={},
    onSyncAll : () -> Unit={},
){
    val appState = LocalAppState.current
    val (vertPadding,horPadding)=if(appState.isPortrait)
        AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20

    val textStyleHeader=if(appState.isPortrait)
        AppTheme.typography.bodyBold()
    else
        AppTheme.typography.titleBold()

    val iconSize= if(appState.isPortrait) AppTheme.dimensions.smallXIcon else AppTheme.dimensions.small24Icon

    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismiss,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
        bgColor = dialogBgColor
    ){
        Column(modifier=Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding5), horizontalAlignment = Alignment.CenterHorizontally) {
            //List UI Header
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = AppTheme.dimensions.padding10, horizontal = AppTheme.dimensions.padding5),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ){
                if(appState.isPortrait){
                    ListCenterText(label = stringResource(Res.string.receipt_no),arrangement = Arrangement.Start, textStyle = textStyleHeader, modifier = Modifier.weight(1f))
                    ListCenterText(label = stringResource(Res.string.date),textStyle = textStyleHeader, modifier = Modifier.weight(1f))
                    ListCenterText(label = stringResource(Res.string.price),textStyle = textStyleHeader, modifier = Modifier.weight(.5f))
                    ListCenterText(label = stringResource(Res.string.qty), textStyle = textStyleHeader,modifier = Modifier.weight(.5f))
                    VectorIcons(
                        icons = AppIcons.removeIcon,
                        modifier = Modifier.weight(.5f),
                        iconSize = iconSize,
                        iconColor=AppTheme.colors.appRed,
                        onClick = {

                        })
                }
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(AppTheme.dimensions.padding5)) {
                itemsIndexed(
                    pendingSales
                ) { index,pendingSale  ->

                    PendingTicketListItem(
                        index = index,
                        item = pendingSale,
                        currencySymbol=currency,
                        isPortrait = appState.isPortrait,
                        horizontalPadding = horPadding,
                        verticalPadding = vertPadding,
                        onRemove = { selectedSale->
                           onRemove.invoke(selectedSale)
                        },
                    )
                }
            }

            Row(modifier=Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AppPrimaryButton(
                    label = stringResource(Res.string.cancel),
                    leftIcon = AppIcons.cancelIcon,
                    backgroundColor = AppTheme.colors.appRed,
                    disabledBackgroundColor = AppTheme.colors.appRed,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    onClick = {
                        onDismiss.invoke()
                    })

                AppPrimaryButton(
                    label = stringResource(Res.string.sync_all),
                    enabled = pendingSales.isNotEmpty(),
                    syncInProgress = isSync,
                    leftIcon = AppIcons.syncIcon,
                    backgroundColor = AppTheme.colors.primaryColor,
                    disabledBackgroundColor = AppTheme.colors.primaryColor,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    onClick = {
                        onSyncAll.invoke()
                    })
            }
        }
    }
}

@Composable
fun PendingTicketListItem(
    index:Int,
    item: PendingSale,
    currencySymbol:String,
    isPortrait: Boolean,
    horizontalPadding:Dp,
    verticalPadding:Dp,
    onRemove: (PendingSale) -> Unit,
){
    val (textStyle,invoiceNoStyle)=if(isPortrait)
        AppTheme.typography.captionMedium() to AppTheme.typography.bodyBold()
    else
        AppTheme.typography.bodyMedium() to AppTheme.typography.titleBold()

    val (borderColor,rowBgColor)=when(index%2 == 0){
        true->  AppTheme.colors.borderColor to AppTheme.colors.listRowBgColor
        false ->AppTheme.colors.appWhite to AppTheme.colors.appWhite
    }
    val iconSize= if(isPortrait) AppTheme.dimensions.smallXIcon else AppTheme.dimensions.small24Icon

    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite).clickable { }){
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor),
            verticalArrangement = Arrangement.spaceBetweenPadded(10.dp)) {
            AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {

                //Invoice No.
                ListCenterText(
                    label = item.invoiceNo,
                    textStyle = invoiceNoStyle,
                    color = AppTheme.colors.textBlack,
                    modifier = Modifier.weight(1f)
                )

                //Invoice Date
                ListCenterText(
                    label = item.invoiceDate,
                    textStyle = textStyle,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                //Price
                ListCenterText(
                    label = formatPrice(item.invoiceTotal,currencySymbol),
                    textStyle = textStyle,
                    color = AppTheme.colors.appRed,
                    modifier = Modifier.weight(.5f)
                )

                //Qty
                ListCenterText(
                    label = "${item.qty}",
                    textStyle = textStyle,
                    color = AppTheme.colors.textBlack,
                    modifier = Modifier.weight(.5f)
                )
                //Remove Icon
                VectorIcons(
                    icons = AppIcons.cancelIcon,
                    modifier = Modifier.weight(.5f),
                    iconSize = iconSize,
                    iconColor=AppTheme.colors.appRed,
                    onClick = {
                        onRemove.invoke(item)
                    })
            }
            AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
        }
    }

    /*AppBaseCard(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(AppTheme.dimensions.padding3)) {
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding5)) {

            Column(modifier = Modifier.weight(1f),Arrangement.spacedBy(AppTheme.dimensions.padding5)) {

                ListText(label = "${index+1}. ${item.invoiceNo}", textStyle = textStyle, color  = AppTheme.colors.textBlack, modifier = Modifier.wrapContentWidth())
                ListText(label = item.invoiceDate, textStyle = textStyle, color  = AppTheme.colors.textPrimary, modifier = Modifier.wrapContentWidth().padding(horizontal = AppTheme.dimensions.padding5))


                ListText(label = stringResource(Res.string.items), textStyle = amountStyle, color  = AppTheme.colors.textBlack, modifier = Modifier.wrapContentWidth().padding(horizontal = AppTheme.dimensions.padding5, vertical = AppTheme.dimensions.padding5))

                Column(modifier = Modifier.fillMaxWidth().background(AppTheme.colors.listRowBgColor).padding(horizontal = horizontalPadding)) {
                    AppHorizontalDivider(color = AppTheme.colors.borderColor, modifier = Modifier.fillMaxWidth())
                    item.posInvoiceDetailRecord?.forEachIndexed { index,element->
                        ListText(
                            label = "${index+1}.${element.inventoryName}[${element.inventoryCode}]",
                            textStyle = textStyle,
                            color = AppTheme.colors.textDarkGrey,
                            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimensions.padding2)
                        )
                    }
                    AppHorizontalDivider(color = AppTheme.colors.borderColor, modifier = Modifier.fillMaxWidth())
                }

                ListText(label = stringResource(Res.string.payment), textStyle = amountStyle, color  = AppTheme.colors.textBlack, modifier = Modifier.wrapContentWidth().padding(horizontal = AppTheme.dimensions.padding5, vertical = AppTheme.dimensions.padding5))

                Column(modifier = Modifier.fillMaxWidth().background(AppTheme.colors.listRowBgColor).padding(horizontal = horizontalPadding)) {
                    AppHorizontalDivider(color = AppTheme.colors.borderColor, modifier = Modifier.fillMaxWidth())
                    item.posPaymentConfigRecord?.forEachIndexed { index,element->
                        ListText(
                            label = "${index+1}.${element.name}",
                            textStyle = textStyle,
                            color = AppTheme.colors.textDarkGrey,
                            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimensions.padding2)
                        )
                    }
                    AppHorizontalDivider(color = AppTheme.colors.borderColor, modifier = Modifier.fillMaxWidth())
                }
            }
            ListText(label = formatPrice(item.grandTotal,currencySymbol), textStyle = amountStyle, color  = AppTheme.colors.appRed, modifier = Modifier.wrapContentWidth())

        }
    }*/
}

@Composable
fun PaymentModeDialog(
    state: SaleTransactionDetailsState,
    isVisible: Boolean,
    isPortrait: Boolean=true,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxMidWidth,
    dialogBgColor: Color = AppTheme.colors.backgroundDialog,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(),
    onDismiss: () -> Unit,
    onItemClick : (PaymentMethod) -> Unit={},
){
    val (vertPadding,horPadding)=if(isPortrait)
        AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20

    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismiss,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
        bgColor = dialogBgColor
    ){
        Column(modifier=Modifier.fillMaxWidth().wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding5),
            horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                itemsIndexed(
                    state.paymentModes
                ) { index,paymentMode  ->

                    PaymentModeListItem(
                        index = index,
                        item = paymentMode,
                        isPortrait = isPortrait,
                        horizontalPadding = horPadding,
                        verticalPadding = vertPadding,
                        onItemClick = {
                          onItemClick.invoke(paymentMode)
                        }
                    )
                    if(index < state.paymentModes.lastIndex)
                        AppHorizontalDivider(color = AppTheme.colors.borderColor, modifier = Modifier.fillMaxWidth())
                }
            }
        }

    }
}

@Composable
fun PaymentModeListItem(
    index:Int,
    item: PaymentMethod,
    isPortrait: Boolean,
    horizontalPadding:Dp,
    verticalPadding:Dp,
    onItemClick: () -> Unit,
){
    val textStyle=if(isPortrait)
        AppTheme.typography.bodyMedium()
    else
        AppTheme.typography.titleMedium()

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = horizontalPadding)
        .clickable { onItemClick.invoke() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
       Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = verticalPadding),
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.Center) {

           val icon=if(item.name?.uppercase()=="CASH")AppIcons.cashIcon else AppIcons.cardIcon
           Image(painter = painterResource(icon),
               contentDescription = item.name,
               colorFilter = ColorFilter.tint(AppTheme.colors.textDarkGrey),
               modifier = Modifier.size(AppTheme.dimensions.standerIcon)
           )
           ListText(label = item.name?:"", textStyle = textStyle, color  = AppTheme.colors.textDarkGrey, modifier = Modifier.wrapContentWidth().padding(AppTheme.dimensions.padding10))
       }
    }
}

@Composable
fun ItemDiscountDialog(
    isVisible: Boolean,
    inputValue:String,
    inputError:String?,
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxSmallWidth,
    isFullScreen: Boolean = false,
    trailingIcon:DrawableResource?=null,
    selectedDiscountType : DiscountType=DiscountType.FIXED_AMOUNT,
    properties: DialogProperties = DialogProperties(),
    onDismissRequest: () -> Unit={},
    onTabClick: (DiscountType) -> Unit={},
    onApply: () -> Unit={},
    onClearDiscountClick: () -> Unit={},
    onCancel: () -> Unit={},
    onDiscountChange: (discount: String) -> Unit={},
    onNumberPadClick: (symbol: String) -> Unit={},
){
    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismissRequest,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(AppTheme.colors.appWhite)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ){

            //Tab Selector
            DiscountTabCard(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().border(
                    width = 1.dp, // Border thickness
                    color = AppTheme.colors.borderColor
                ).padding(AppTheme.dimensions.padding10),
                selectedDiscountType=selectedDiscountType,
                onTabClick = {
                    onTabClick.invoke(it)
                }
            )

            NumberPad(
                textValue=inputValue,
                onValueChange = {discount->
                    onDiscountChange.invoke(discount)
                },
                trailingIcon = trailingIcon,
                inputError=inputError,
                onNumberPadClick = {symbol->
                    onNumberPadClick.invoke(symbol)
                }, onApplyClick = {
                    onApply.invoke()
                }, onCancelClick = {
                    onCancel.invoke()
                },
                onClearDiscountClick = {
                    onClearDiscountClick.invoke()
                }
            )
        }
    }
}

@Composable
fun PromotionAndDiscountDialog(
    isVisible: Boolean,
    isPortrait: Boolean=true,
    promotions : MutableList<Promotion>,
    modifier: Modifier = Modifier.wrapContentHeight().wrapContentWidth(),
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxSmallWidth,
    isFullScreen: Boolean = false,
    properties: DialogProperties = DialogProperties(dismissOnBackPress = true,dismissOnClickOutside = false,usePlatformDefaultWidth = false),
    onDismiss: () -> Unit,
    onItemClick: (Promotion) -> Unit,
    onClearPromotionClick: () -> Unit,
){
    val (vertPadding,horPadding)=if(isPortrait)
        AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20

    val textStyleHeader=if(isPortrait)
        AppTheme.typography.titleMedium()
    else
        AppTheme.typography.titleBold()

    AppDialog(
        isVisible = isVisible,
        properties = properties,
        onDismissRequest = onDismiss,
        modifier = modifier,
        contentMaxWidth = contentMaxWidth,
        isFullScreen = isFullScreen,
    ){
        Column(modifier=Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            VectorIcons(icons = AppIcons.cancelIcon,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                iconSize = AppTheme.dimensions.smallIcon,
                iconColor=AppTheme.colors.appRed,
                onClick = {
                    onDismiss.invoke()
                })

            Icon(
                imageVector = vectorResource(AppIcons.promotionIcon),
                contentDescription = "Discounts",
                tint = AppTheme.colors.appRed,
                modifier = Modifier.width(AppTheme.dimensions.mediumIcon)
            )

            Text(
                text = stringResource(Res.string.promotion_discounts),
                color = AppTheme.colors.textBlack,
                style = textStyleHeader
            )

            /*Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){

                filteredPromotion.forEachIndexed { index, promotion ->
                    PromotionListItem(
                        index = index,
                        item = promotion,
                        isPortrait = isPortrait,
                        horizontalPadding=horPadding,
                        verticalPadding=vertPadding,
                        onClick = {onItemClick.invoke(promotion)}
                    )
                }
            }*/
            LazyColumn(modifier =Modifier.fillMaxWidth().wrapContentHeight().padding(AppTheme.dimensions.padding5)){
                val filteredPromotion = promotions.filter { it.promotionType ==3 }.toMutableList()
                itemsIndexed(filteredPromotion){index, promotion ->
                    PromotionListItem(
                        index = index,
                        item = promotion,
                        isPortrait = isPortrait,
                        horizontalPadding=horPadding,
                        verticalPadding=vertPadding,
                        onClick = {onItemClick.invoke(promotion)}
                    )
                }
            }
            AppPrimaryButton(
                enabled = promotions.isNotEmpty(),
                label = stringResource(Res.string.clear_promotions),
                leftIcon = AppIcons.removeIcon,
                backgroundColor = AppTheme.colors.appRed,
                disabledBackgroundColor = AppTheme.colors.appRed,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(AppTheme.dimensions.padding10),
                onClick = {
                    onClearPromotionClick.invoke()
                }
            )

        }

    }
}


@Composable
fun PromotionListItem(
    index:Int,
    item:Promotion,
    isPortrait: Boolean,
    horizontalPadding:Dp,
    verticalPadding:Dp,
    onClick: () -> Unit
){
    val textStyle=if(isPortrait)
        AppTheme.typography.bodyMedium()
    else
        AppTheme.typography.titleMedium()

    AppBaseCard(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = verticalPadding).clickable{onClick.invoke()},
            horizontalArrangement = Arrangement.spaceBetweenPadded(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${index+1}. ${item.name.uppercase()}",
                style = textStyle,
                color = AppTheme.colors.textPrimary
            )

            Text(
                text = "Discount: ${item.amount}",
                style = AppTheme.typography.bodyBold(),
                color = AppTheme.colors.textBlack
            )

        }
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
    contentMaxWidth: Dp = AppTheme.dimensions.contentMaxSmallWidth,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDateRangePicker(onDismiss: () -> Unit, onConfirmClicked: (LocalDate) -> Unit) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis <= getEpochTimestamp()
        }
    })
    val selectedDate = datePickerState.selectedDateMillis?.let {
        getDateTimeFromEpochMillSeconds(it)
    }

    DatePickerDialog(
        colors = DatePickerDefaults.colors(containerColor = AppTheme.colors.backgroundDialog),
        shape = AppTheme.appShape.dialog,
        modifier = Modifier.wrapContentHeight().wrapContentWidth().widthIn(AppTheme.dimensions.contentMaxSmallWidth),
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(modifier = Modifier.wrapContentWidth().padding(horizontal = 5.dp, vertical = 10.dp), onClick = {
                if (selectedDate != null) {
                    onConfirmClicked(selectedDate)
                }
                onDismiss()
            }, shape = AppTheme.appShape.button, colors = ButtonDefaults.textButtonColors(
                backgroundColor = AppTheme.colors.primaryColor,
                contentColor = AppTheme.colors.appWhite
            )) {
                Text(text = stringResource(Res.string.confirm), style = AppTheme.typography.bodyNormal())
            }
        },
        dismissButton = {
            TextButton(modifier = Modifier.wrapContentWidth().padding(horizontal = 5.dp, vertical = 10.dp),onClick = {
                onDismiss()
            },shape = AppTheme.appShape.button, colors = ButtonDefaults.textButtonColors(
                backgroundColor = AppTheme.colors.appRed,
                contentColor = AppTheme.colors.appWhite
            )) {
                Text(text = stringResource(Res.string.cancel),style = AppTheme.typography.bodyNormal())
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().background(AppTheme.colors.primaryColor)) {
            DatePicker(
                state = datePickerState,
                title = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = AppTheme.colors.primaryColor,
                    headlineContentColor=AppTheme.colors.appWhite,
                    navigationContentColor=AppTheme.colors.appWhite,
                    subheadContentColor=AppTheme.colors.appWhite,
                    weekdayContentColor=AppTheme.colors.appWhite,

                    yearContentColor=AppTheme.colors.appWhite,
                    currentYearContentColor=AppTheme.colors.appWhite,
                    selectedYearContentColor=AppTheme.colors.textPrimary,
                    selectedYearContainerColor = AppTheme.colors.appWhite,

                    dayContentColor = AppTheme.colors.appWhite,
                    disabledDayContentColor = AppTheme.colors.appWhite.copy(.5f),
                    todayDateBorderColor = AppTheme.colors.appWhite,
                    todayContentColor = AppTheme.colors.appWhite,
                    selectedDayContainerColor = AppTheme.colors.appWhite,
                    selectedDayContentColor = AppTheme.colors.textPrimary,
                ),
                modifier = Modifier.wrapContentHeight()
                    .fillMaxWidth().padding(10.dp) // Adjust weight as needed
            )
        }
    }
}


@Composable
fun GridViewOptionsDialog(
    title: String,
    isVisible: Boolean,
    values: List<Int>,
    selectedValue: Int,
    onCloseDialog: () -> Unit = {},
    onDialogResult: (Int) -> Unit = {},
) {

    AppDialog(
        isVisible = isVisible,
        onDismissRequest = onCloseDialog,
    ) {
        AppDialogContent(
            title = title,
            modifier = Modifier.padding(bottom = 10.dp),
            body = {
                values.forEachIndexed { index, value ->
                    AppRadioButtonWithText(
                        title = stringResource(Res.string.grid_view_option_value, value),
                        selected = value == selectedValue,
                        isClickable = true,
                        onClick = { onDialogResult(value) }
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
fun ActionTextFiledDialog(
    isVisible: Boolean,
    value: String,
    title: String,
    inputType: InputType =InputType.Any,
    onCloseDialog: () -> Unit = {},
    onDialogResult: (String) -> Unit = {},
) {
    var displayedValue by remember(value) {
        mutableStateOf(value)
    }
    val focusRequester = remember { FocusRequester() }

    AppDialog(
        isVisible = isVisible,
        onDismissRequest = onCloseDialog,
    ) {
        AppDialogContent(
            title = title,
            modifier = Modifier.padding(bottom = 10.dp),
            body = {
                AppDialogTextField(
                    value = displayedValue,
                    onValueChange = { displayedValue = it },
                    inputType=inputType,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            focusRequester.requestFocus()
                        }
                )
            },
            buttons = {
                AppDialogButton(
                    title = stringResource(Res.string.alert_cancel),
                    onClick = onCloseDialog
                )

                AppDialogButton(
                    title = stringResource(Res.string.alert_ok),
                    onClick = { onDialogResult(displayedValue) }
                )
            }
        )
    }
}


@Composable
fun RoundOffOptionsDialog(
    title: String,
    isVisible: Boolean,
    values: List<Int>,
    selectedValue: Int,
    onCloseDialog: () -> Unit = {},
    onDialogResult: (Int) -> Unit = {},
) {
    //val options = values.map { mapIntToText(it) }

    AppDialog(
        isVisible = isVisible,
        onDismissRequest = onCloseDialog,
    ) {
        AppDialogContent(
            title = title,
            modifier = Modifier.padding(bottom = 10.dp),
            body = {

                values.forEach { value ->
                    AppRadioButtonWithText(
                        title = mapIntToText(value),
                        selected = value == selectedValue,
                        isClickable = true,
                        onClick = { onDialogResult(value) }
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
fun mapIntToText(value: Int): String {
    return when (value) {
        1 -> "Default"
        2 -> "Round Up"
        3 -> "Round Down"
        else -> stringResource(Res.string.round_off_description)
    }
}