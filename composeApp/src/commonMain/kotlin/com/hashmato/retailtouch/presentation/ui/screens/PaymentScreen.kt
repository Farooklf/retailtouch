package com.hashmato.retailtouch.presentation.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.domain.model.AppState
import com.hashmato.retailtouch.domain.model.paymentType.PaymentMethod
import com.hashmato.retailtouch.domain.model.products.PosUIState
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.common.AppCircleProgressIndicator
import com.hashmato.retailtouch.presentation.common.BasicScreen
import com.hashmato.retailtouch.presentation.common.ButtonRowCard
import com.hashmato.retailtouch.presentation.common.dialogs.DeletePaymentModeDialog
import com.hashmato.retailtouch.presentation.common.ImagePlaceholder
import com.hashmato.retailtouch.presentation.common.PaymentCollectorDialog
import com.hashmato.retailtouch.presentation.common.dialogs.PaymentSuccessDialog
import com.hashmato.retailtouch.presentation.common.getGridCell
import com.hashmato.retailtouch.presentation.viewModels.SharedPosViewModel
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.utils.LocalAppState
import com.hashmato.retailtouch.utils.payment.PaymentLibTypes
import com.hashmato.retailtouch.utils.payment.PaymentProvider
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.lib.rememberValRef
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.balance
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.error_title
import retailtouch.composeapp.generated.resources.grand_total
import retailtouch.composeapp.generated.resources.order_summary
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.payment_total
import retailtouch.composeapp.generated.resources.scan
import retailtouch.composeapp.generated.resources.tender
import kotlin.math.abs

object PaymentTypeScreen:Screen{
    @Composable
    override fun Content() {
        Payment()
    }
}

@Composable
fun Payment(
    viewModel: SharedPosViewModel = koinInject()
){
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val state by viewModel.posUIState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

    LaunchedEffect(Unit){
        viewModel.fetchPaymentList()
        viewModel.getEmployee()
    }

    LaunchedEffect(state.isExecutePosSaving){
        if (state.isExecutePosSaving) {
            viewModel.callTender(true)
        }
    }

    if (state.startPaymentLib){
        startPayment(viewModel,state)
    }

    LaunchedEffect(state.paymentFromLib) {
        if (state.paymentFromLib) {
            viewModel.applyPaymentValue(state.paymentFromLibAmount)
            viewModel.resetPaymentLibValues()
        }
    }

    LaunchedEffect(state.isPaymentClose){
        if(state.isPaymentClose){
            viewModel.updateSales()
            viewModel.resetScreenState()
            NavigatorActions.backToCashierScreen(navigator)
        }
    }

    LaunchedEffect(state.isError) {
        if (state.isError) {
            val errorTitle=getString(Res.string.error_title)
            snackbarHostState.value.showSnackbar("$errorTitle ${state.errorMsg}")
            delay(1000)
            viewModel.resetError()
        }
    }


    BasicScreen(
        modifier = Modifier.systemBarsPadding(),
        title = stringResource(Res.string.payment),
        isTablet = appState.isTablet,
        contentMaxWidth = Int.MAX_VALUE.dp,
        onBackClick = {
            navigator.pop()
        }
    ){
        Column(modifier = Modifier.weight(1f)) {
            if (!appState.isPortrait) {
                LandscapePaymentScreen(
                    state,
                    viewModel
                )
            } else {
                PortraitPaymentScreen(
                    state,
                    viewModel
                )
            }
        }
        AppCircleProgressIndicator(
            isVisible=state.isLoading
        )
        SnackbarHost(
            hostState = snackbarHostState.value,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if(state.showPaymentCollectorDialog){
        //println("state_remainingBalance:${state.remainingBalance}")
        PaymentCollectorDialog(
            isVisible = state.showPaymentCollectorDialog,
            paymentAmount = state.remainingBalance,
            paymentName = state.availablePayments.find { it.id == state.selectedPaymentTypesId }?.name ?: "Payment Amount",
            onPayClick = {payment->
                //println("dialogAmount:$payment")
                viewModel.updatePaymentCollectorDialogVisibility(false)
                viewModel.applyPaymentValue(payment)
            },
            onDismiss = {
                viewModel.updatePaymentCollectorDialogVisibility(false)
            }
        )
    }

    DeletePaymentModeDialog(
        isVisible = state.showDeletePaymentModeDialog,
        onDismiss = { viewModel.updateDeletePaymentModeDialog(false) },
        onConfirm = {
            viewModel.updateDeletePaymentModeDialog(false)
            viewModel.deletePayment()
        },
    )

    PaymentSuccessDialog(
        isVisible = state.showPaymentSuccessDialog,
        interactorRef=rememberValRef(viewModel),
        onDismiss = {
            viewModel.updatePaymentSuccessDialog(false)
            viewModel.clearSale()
        },
        appliedPayments = state.createdPayments.sumOf { it.amount },
        balance = abs(state.remainingBalance),
        onPrinting = {
            viewModel.updatePaymentSuccessDialog(false)
        }
    )
}

@Composable
fun startPayment(viewModel: SharedPosViewModel, state: PosUIState) {
    viewModel.resetStartPaymentLibState()
    state.apply {
        val processorName = availablePayments.find { it.id == selectedPaymentTypesId }?.paymentProcessorName?:""
        val paymentName = availablePayments.find { it.id ==selectedPaymentTypesId }?.name?:""
        //val isAutoPayment = state.selectedPayment.autoPayment
        //paymentName should be replace with processorName

        if (paymentName.equals("RFM", ignoreCase = true)) {
            PaymentProvider().launchExternalApp(remainingBalance, PaymentLibTypes.RFM, "")
        }else if (paymentName.equals("Ascan", ignoreCase = true)){
            PaymentProvider().launchExternalApp(
                remainingBalance,
                PaymentLibTypes.ASCAN,
                "dbs"//processorName
            )
        }else if (paymentName.equals("Paytm", ignoreCase = true)) {
            //PaymentProvider().launchApi(PaymentLibTypes.PAYTM, httpClient = httpClient)
        }else if (paymentName.equals("Nets", ignoreCase = true)) {
            PaymentProvider().launchExternalApp(
                remainingBalance,
                PaymentLibTypes.PAYTM,
                paymentName
            )
        }else{
            //Normal Payment flow
            viewModel.updatePaymentCollectorDialogVisibility(true)
        }
    }
}

@Composable
private fun PortraitPaymentScreen(
    state:PosUIState,
    viewModel: SharedPosViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        PaymentSelectionView(
            modifier = Modifier.weight(1f),
            availablePayments = state.availablePayments,
            onPaymentClick = { selectedPayment ->
                viewModel.onPaymentClicked(selectedPayment)
            }
        )

        OrderSummary(
            viewModel,
            onDeletePaymentClick = { id ->
                viewModel.updateDeletePaymentModeDialog(true)
                viewModel.updateSelectedPaymentToDeleteId(id)
            },
            subTotal = state.cartSubTotal,
            appliedTax = state.globalTax,
            granTotal = state.grandTotal,
            appliedDiscountTotal = ((state.globalDiscount)),
            payment = state.paymentTotal,
            balance = state.remainingBalance,
            payments = state.createdPayments
        )

    }
}

@Composable
fun LandscapePaymentScreen(
    state:PosUIState,
    viewModel: SharedPosViewModel
){

    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ){

            PaymentSelectionView(
                modifier = Modifier.weight(1f),
                availablePayments = state.availablePayments,
                onPaymentClick = { selectedPayment ->
                    viewModel.onPaymentClicked(selectedPayment)
                }
            )

            OrderSummary(
                viewModel=viewModel,
                onDeletePaymentClick = { id ->
                    viewModel.updateDeletePaymentModeDialog(true)
                    viewModel.updateSelectedPaymentToDeleteId(id)
                },
                subTotal = state.cartSubTotal,
                appliedTax = state.globalTax,
                granTotal = state.grandTotal,
                appliedDiscountTotal = ((state.globalDiscount)),
                payment = state.paymentTotal,
                balance = state.remainingBalance,
                payments = state.createdPayments
            )
        }
    }
}

@Composable
private fun PaymentSelectionView(
    modifier: Modifier = Modifier,
    availablePayments: List<PaymentMethod>,
    onPaymentClick: (PaymentMethod) -> Unit = {},
) {
    //val appState = LocalAppState.current
    val appThemeContext=AppTheme.context
    val gridCount=getGridCell(appThemeContext.deviceType)
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridCount),
        modifier = modifier
    ) {
        items(availablePayments) { payment ->
            PaymentMethodItem(
                payment = payment,
                onClick = { paymentMethod ->
                    onPaymentClick(paymentMethod)
                }
            )
        }
    }
}

@Composable
fun PaymentMethodItem(
    payment: PaymentMethod,
    onClick: (PaymentMethod) -> Unit = {},
) {
    val appState = LocalAppState.current
    val padding=if(appState.isTablet){
        AppTheme.dimensions.padding15
    }else{
        AppTheme.dimensions.padding10
    }
    val (cardColor,contentColor) = if (payment.isSelected) {
        AppTheme.colors.primaryColor to AppTheme.colors.appWhite
    } else {
        AppTheme.colors.textLightGrey to AppTheme.colors.textBlack
    }

    Card(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(padding)
            .clickable {
                onClick(payment)
            },
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.appWhite),
        shape = AppTheme.appShape.card
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
        ) {
            KamelImage(
                resource = asyncPainterResource(""),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2.5f),
                contentScale = ContentScale.Crop,
                onFailure = { ImagePlaceholder() },
                onLoading = { ImagePlaceholder() }
            )

            Box(
                modifier = Modifier
                    .background(cardColor)
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.padding10),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = payment.name?:"",
                    style = AppTheme.typography.bodyNormal(),
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun OrderSummary(
    viewModel: SharedPosViewModel,
    onDeletePaymentClick: (Int) -> Unit = {},
    subTotal: Double,
    granTotal: Double,
    appliedTax: Double,
    appliedDiscountTotal: Double,
    payment: Double,
    balance: Double,
    payments: List<PaymentMethod>
) {
    val appState = LocalAppState.current
    val textStyleHeader=if(appState.isPortrait)
        AppTheme.typography.bodyBold()
    else
        AppTheme.typography.titleBold()

    Card(
        modifier = Modifier.fillMaxWidth().padding(2.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cartItemBg),
        elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
        shape = AppTheme.appShape.card
    ){
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Text(
                text = stringResource(Res.string.order_summary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                style = AppTheme.typography.h1Medium(),
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )


            OrderSummaryRow(
                label = stringResource(Res.string.grand_total),
                value = granTotal,
                textStyle = textStyleHeader,
                viewModel=viewModel
            )

            OrderSummaryRow(
                label = stringResource(Res.string.payment_total),
                value = payment,
                textStyle = textStyleHeader,
                viewModel=viewModel
            )

            if (payments.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    payments.forEach { payment ->
                        PaymentView(
                            onDeleteClick = {onDeletePaymentClick.invoke(payment.id)},
                            payment = payment
                        )
                    }
                }

            }

            OrderSummaryRow(
                label = stringResource(Res.string.balance),
                value = balance,
                textStyle = AppTheme.typography.titleBold().copy(fontSize = 20.sp),
                primaryText = AppTheme.colors.appRed,
                viewModel=viewModel
            )
        }
    }

}

@Composable
fun OrderSummaryRow(
    label: String,
    value: Double,
    textStyle: TextStyle = AppTheme.typography.bodyNormal(),
    primaryText: Color = AppTheme.colors.textDarkGrey,
    viewModel:SharedPosViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = textStyle,
            color = primaryText
        )

        Text(
            text = viewModel.formatPriceForUI(value),
            modifier = Modifier.weight(1f),
            style = textStyle,
            color = primaryText,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun PaymentView(
    onDeleteClick: (Int) -> Unit = {},
    payment: PaymentMethod
) {
    val appState = LocalAppState.current
    Card(
        modifier = Modifier.height(AppTheme.dimensions.defaultCardMinSize).wrapContentWidth().padding(vertical = if(appState.isTablet) AppTheme.dimensions.padding10
        else AppTheme.dimensions.padding5, horizontal = AppTheme.dimensions.padding5)
            .clickable{
                onDeleteClick.invoke(payment.id)
            },
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.brand),
        elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
        shape = AppTheme.appShape.cardRound
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = AppTheme.dimensions.padding5, horizontal = AppTheme.dimensions.padding5),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spaceBetweenPadded(AppTheme.dimensions.padding5)
        ){

            Text(
                text = "${payment.name} - ${payment.amount}",
                modifier = Modifier.wrapContentWidth(),
                style = AppTheme.typography.captionBold(),
                color = AppTheme.colors.appWhite
            )

            Icon(
                painter = painterResource(AppIcons.removeIcon),
                contentDescription = null,
                tint = AppTheme.colors.appWhite,
                modifier = Modifier.size(AppTheme.dimensions.icon16)
            )
        }

    }
}



@Composable
fun BottomButton(modifier: Modifier, appState: AppState,  onTenderClick: () -> Unit, onCancelClick: () -> Unit, onScanClick: () -> Unit) {

    if(appState.isTablet || !appState.isPortrait){
        Row(modifier=Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().weight(1f),
                label = stringResource(Res.string.scan),
                icons = AppIcons.settingIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.appGreen,
                /*innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),*/
                onClick = {
                    onScanClick.invoke()
                }
            )

            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().weight(1f),
                label = stringResource(Res.string.cancel),
                icons = AppIcons.settingIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.textError,
                /*innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),*/
                onClick = {
                    onCancelClick.invoke()
                }
            )

            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().weight(1f),
                label = stringResource(Res.string.tender),
                icons = AppIcons.applyIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.primaryColor,
                /*innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),*/
                onClick = {
                    onTenderClick.invoke()
                }
            )
        }
    }
    else{
        Column(modifier=Modifier.fillMaxWidth().wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            ButtonRowCard(
                modifier = Modifier.fillMaxWidth(1f).wrapContentHeight(),
                label = stringResource(Res.string.scan),
                icons = AppIcons.settingIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.appGreen,
                /*innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),*/
                onClick = {
                    onScanClick.invoke()
                }
            )

            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().fillMaxWidth(1f),
                label = stringResource(Res.string.cancel),
                icons = AppIcons.settingIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.textError,
                /*innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),*/
                onClick = {
                    onCancelClick.invoke()
                }
            )

            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                label = stringResource(Res.string.tender),
                icons = AppIcons.applyIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.primaryColor,
                /*innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),*/
                onClick = {
                    onTenderClick.invoke()
                }
            )
        }
    }

}




