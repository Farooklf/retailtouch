package com.hashmato.retailtouch.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleTransactionDetailsState
import com.hashmato.retailtouch.domain.model.paymentType.PaymentMethod
import com.hashmato.retailtouch.domain.model.products.PosInvoiceDetail
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.ui.common.AppBaseCard
import com.hashmato.retailtouch.presentation.ui.common.AppCircleProgressIndicator
import com.hashmato.retailtouch.presentation.ui.common.AppHorizontalDivider
import com.hashmato.retailtouch.presentation.ui.common.AppPrimaryButton
import com.hashmato.retailtouch.presentation.ui.common.BasicScreen
import com.hashmato.retailtouch.presentation.ui.common.ListText
import com.hashmato.retailtouch.presentation.ui.common.dialogs.PaymentModeDialog
import com.hashmato.retailtouch.presentation.viewModels.TransactionDetailsViewModel
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.utils.DateTimeUtils.formatDateForUI
import com.hashmato.retailtouch.utils.DateTimeUtils.parseDateTimeFromApiStringUTC
import com.hashmato.retailtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.re_print
import retailtouch.composeapp.generated.resources.save
import retailtouch.composeapp.generated.resources.transaction_details

data class TransactionDetailsScreen(val mSaleRecord: SaleRecord):Screen{
    @Composable
    override fun Content() {
        TransactionDetailsUI(mSaleRecord)
    }
 }

@Composable
fun TransactionDetailsUI(
    mSaleRecord: SaleRecord,
    viewModel:  TransactionDetailsViewModel= koinInject()
){
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val uiUpdateStatus by viewModel.uiUpdateStatus.collectAsStateWithLifecycle()


    LaunchedEffect(Unit){
        viewModel.getPosInvoiceForEdit(mSaleRecord)
    }
    LaunchedEffect(uiUpdateStatus){
        if(uiUpdateStatus){
            viewModel.updateLoader(false)
            NavigatorActions.navigateBack(navigator)
        }
    }

    val (vertPadding,horPadding)=if(appState.isPortrait)
        AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20


    val (textStyleHeader,btnStyle)=if(appState.isPortrait)
        AppTheme.typography.bodyBold() to AppTheme.typography.captionBold()
    else
        AppTheme.typography.titleBold() to AppTheme.typography.bodyBold()

    BasicScreen(
        modifier = Modifier.systemBarsPadding(),
        title = stringResource(Res.string.transaction_details),
        isTablet = appState.isTablet,
        contentMaxWidth = Int.MAX_VALUE.dp,
        onBackClick = {
            NavigatorActions.navigateBack(navigator)
        }
    ){

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding10),
        ){
            //Filter Status,Type row
            /*Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding10),
                verticalAlignment = Alignment.CenterVertically,) {

                //Type
                AppDropdownMenu(
                    modifier = Modifier.weight(1f),
                    selectedIndex = screenState.posInvoice?.type?:0,
                    label = stringResource(Res.string.type),
                    labelExtractor = { it.name },
                    items = screenState.typeList,
                    onItemSelected = { type, selectedValue ->
                        viewModel.updateType(selectedValue)
                    })

                //Status
                AppDropdownMenu(
                    modifier = Modifier.weight(1f),
                    selectedIndex = screenState.posInvoice?.status?:0,
                    label = stringResource(Res.string.status),
                    labelExtractor = { it.name },
                    items = screenState.statusList,
                    onItemSelected = { status, selectedValue ->
                        viewModel.updateStatus(selectedValue)
                    })

            }*/

            //Main Body Content
            Column(modifier = Modifier.weight(1f)) {
                AppBaseCard(modifier = Modifier.fillMaxWidth().wrapContentHeight()){
                    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding10)) {

                        if(appState.isPortrait){
                            buildBodyContent(
                                modifier  = Modifier.fillMaxWidth().wrapContentHeight().padding(AppTheme.dimensions.padding10),
                                screenState=screenState,
                                mSaleRecord=mSaleRecord,
                                viewModel=viewModel
                            )
                        }else{
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding10)) {

                                //invoiceNo,total ,tax, date
                                buildBodyContent(
                                    modifier  = Modifier.weight(1.5f).wrapContentHeight().padding(AppTheme.dimensions.padding5),
                                    screenState=screenState,
                                    mSaleRecord=mSaleRecord,
                                    viewModel=viewModel
                                )
                            }
                        }

                        buildPosPaymentContent(
                            modifier  = Modifier.fillMaxWidth().wrapContentHeight(),
                            screenState=screenState,
                            viewModel=viewModel
                        )

                        //PosInvoice Details
                        LazyColumn(modifier = Modifier.fillMaxWidth().padding(AppTheme.dimensions.padding10)){
                            val posDetailsList=screenState.posInvoice?.posInvoiceDetails?: emptyList()
                            itemsIndexed(posDetailsList
                            ){index, sale ->
                                PosDetailsItem(
                                    index = index,
                                    item = sale,
                                    viewModel=viewModel,
                                    isPortrait = true,
                                    horizontalPadding=horPadding,
                                    verticalPadding=vertPadding
                                )
                            }
                        }
                    }
                }
            }

            //Bottom Button
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                //reprint
                AppPrimaryButton(
                    label = stringResource(Res.string.re_print),
                    leftIcon = AppIcons.printerIcon,
                    backgroundColor = AppTheme.colors.primaryColor,
                    disabledBackgroundColor = AppTheme.colors.primaryColor,
                    style = btnStyle,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    onClick = {
                        viewModel.rePrintAndCloseReceipt()
                    }
                )

                //save
                AppPrimaryButton(
                    isVisible = screenState.isFilterApplied,
                    label = stringResource(Res.string.save),
                    leftIcon = AppIcons.paymentIcon,
                    backgroundColor = AppTheme.colors.appGreen,
                    disabledBackgroundColor = AppTheme.colors.appGreen,
                    style = btnStyle,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    onClick = {
                        viewModel.saveAndCloseReceipt()
                    }
                )

                // cancel
                AppPrimaryButton(
                    label = stringResource(Res.string.cancel),
                    leftIcon = AppIcons.cancelIcon,
                    backgroundColor = AppTheme.colors.appRed,
                    disabledBackgroundColor = AppTheme.colors.appRed,
                    style = btnStyle,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    onClick = {
                        NavigatorActions.navigateBack(navigator)
                    }
                )

            }
        }

        SnackbarHost(
            hostState = snackbarHostState.value,
            modifier = Modifier
                .align(Alignment.TopCenter))

        AppCircleProgressIndicator(
            isVisible= screenState.isLoading
        )
    }

    PaymentModeDialog(
        state = screenState,
        isVisible = screenState.showPaymentModeDialog,
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
        onDismiss = {
            viewModel.updatePaymentDialogState(false)
        },
        onItemClick = {
            viewModel.updatePaymentDialogState(false)
            viewModel.updateSelectedPaymentMode(it)
        }
    )
}

@Composable
fun buildPosPaymentContent(
    modifier: Modifier,
    screenState: SaleTransactionDetailsState,
    viewModel: TransactionDetailsViewModel,
){
    val appState = LocalAppState.current
    if(!screenState.posInvoice?.posPayments.isNullOrEmpty()){
        Row(modifier = modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding10)) {
            screenState.posInvoice?.posPayments?.map {element->
                //viewModel.buildPaymentMethodTile(element)

                val payment=screenState.paymentModes.find {
                    it.id==element.paymentTypeId
                }?: PaymentMethod(id=element.paymentTypeId, name = element.paymentTypeName)

                Row(modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(horizontal = AppTheme.dimensions.padding10)
                    .clip(AppTheme.appShape.card)
                    .background(AppTheme.colors.primaryColor)
                    .border(width = 2.dp, color = AppTheme.colors.primaryDarkColor, shape = AppTheme.appShape.card)
                    .clickable {
                        viewModel.updatePaymentDialogState(true)
                        viewModel.updateClickedPayment(element)
                               },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly){
                     val icon=if(payment.name=="CASH")AppIcons.cashIcon else AppIcons.cardIcon

                    Spacer(modifier = Modifier.width(AppTheme.dimensions.padding5))
                    Image(painter = painterResource(icon),
                        contentDescription = payment.name,
                        colorFilter = ColorFilter.tint(AppTheme.colors.appWhite),
                        modifier = Modifier.size(AppTheme.dimensions.standerIcon)
                    )

                    Column(modifier = Modifier.wrapContentWidth().padding(10.dp)) {

                        ListText(label = payment.name?:"",
                            textStyle = if(appState.isPortrait)AppTheme.typography.captionMedium() else AppTheme.typography.titleNormal(),
                            color = AppTheme.colors.appWhite,
                            modifier = Modifier.wrapContentWidth())

                        ListText(label = viewModel.formatPriceForUI(element.amount),
                            textStyle = if(appState.isPortrait)AppTheme.typography.captionBold() else AppTheme.typography.bodyBold(),
                            color = AppTheme.colors.appWhite,
                            modifier = Modifier.wrapContentWidth())
                    }

                    Icon(imageVector = vectorResource(AppIcons.editIcon),
                        contentDescription = element.name,
                        tint = AppTheme.colors.appWhite,
                        modifier = Modifier.size(AppTheme.dimensions.smallXIcon)
                    )
                    Spacer(modifier = Modifier.width(AppTheme.dimensions.padding5))
                }

            }
        }
    }
}

@Composable
fun buildBodyContent(
    modifier: Modifier,
    screenState: SaleTransactionDetailsState,
    mSaleRecord: SaleRecord,
    viewModel: TransactionDetailsViewModel
){
    val appState = LocalAppState.current
    val (textStyleHeader,btnStyle)=if(appState.isPortrait)
        AppTheme.typography.bodyBold() to AppTheme.typography.captionBold()
    else
        AppTheme.typography.titleBold() to AppTheme.typography.bodyBold()

    //invoiceNo,total ,tax, date
    Column(modifier = modifier,verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding10)){

        //invoice no
        ListText(label = screenState.posInvoice?.invoiceNo?:mSaleRecord.receiptNumber,
            textStyle = if(appState.isPortrait)AppTheme.typography.titleBold() else AppTheme.typography.titleBold().copy(fontSize = 24.sp),
            color = AppTheme.colors.textBlack,
            modifier = Modifier.wrapContentWidth())

        //date
        ListText(label = formatDateForUI(parseDateTimeFromApiStringUTC(screenState.posInvoice?.creationTime)),
            textStyle = textStyleHeader,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.wrapContentWidth())

        //total
        ListText(label = "Total : ${viewModel.formatPriceForUI(screenState.posInvoice?.invoiceNetTotal)}",
            textStyle = textStyleHeader,
            color = AppTheme.colors.textError,
            modifier = Modifier.wrapContentWidth()
        )

        //tax
        ListText(label = "Tax : ${viewModel.formatPriceForUI(screenState.posInvoice?.invoiceTax)}",
            textStyle = textStyleHeader,
            color = AppTheme.colors.textBlack,
            modifier = Modifier.wrapContentWidth()
        )
    }
}

@Composable
fun PosDetailsItem(
    index:Int,
    item: PosInvoiceDetail,
    isPortrait:Boolean,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    viewModel: TransactionDetailsViewModel,
    onItemClick:(PosInvoiceDetail)->Unit={}
){

    val (borderColor,rowBgColor)=when(index%2 == 0){
        true->  AppTheme.colors.borderColor to AppTheme.colors.listRowBgColor
        false ->AppTheme.colors.appWhite to AppTheme.colors.appWhite
    }

    val textStyle=if(isPortrait)
        AppTheme.typography.bodyMedium()
    else
        AppTheme.typography.titleMedium()

    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite).clickable { onItemClick.invoke(item) }){
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(start = horizontalPadding).background(rowBgColor),
            verticalArrangement = Arrangement.spaceBetweenPadded(10.dp)) {
            AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                //Items name
                ListText(
                    label ="${index+1}.${item.inventoryName} [${item.inventoryCode}]",
                    textStyle = textStyle ,
                    color = AppTheme.colors.textBlack,
                    singleLine = false,
                    modifier = Modifier.wrapContentWidth().padding(start = AppTheme.dimensions.padding5)
                )
                //Items total
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {

                    ListText(
                        label = "${item.qty} x ${viewModel.formatPriceForUI(item.price)}",
                        textStyle = textStyle,
                        color = AppTheme.colors.textDarkGrey,
                        modifier = Modifier.wrapContentWidth().padding(start = AppTheme.dimensions.padding5)
                    )

                    ListText(
                        label = viewModel.formatPriceForUI(item.totalAmount),
                        textStyle = textStyle,
                        color = AppTheme.colors.textError,
                        modifier = Modifier.wrapContentWidth().padding(end = AppTheme.dimensions.padding5)
                    )
                }
            }
            AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
        }
    }
}