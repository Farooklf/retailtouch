package com.lfssolutions.retialtouch.presentation.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.products.PosUIState
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.AppCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.AppLeftSideMenu
import com.lfssolutions.retialtouch.presentation.ui.common.AppScreenPadding
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonRowCard
import com.lfssolutions.retialtouch.presentation.ui.common.DeletePaymentModeDialog
import com.lfssolutions.retialtouch.presentation.ui.common.ImagePlaceholder
import com.lfssolutions.retialtouch.presentation.ui.common.LazyVerticalGridG
import com.lfssolutions.retialtouch.presentation.ui.common.NumberPad
import com.lfssolutions.retialtouch.presentation.ui.common.PaymentCollectorDialog
import com.lfssolutions.retialtouch.presentation.ui.common.PaymentSuccessDialog
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.lfssolutions.retialtouch.utils.payment.PaymentLibTypes
import com.lfssolutions.retialtouch.utils.payment.PaymentProvider
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.add_more
import retailtouch.composeapp.generated.resources.balance
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.error_title
import retailtouch.composeapp.generated.resources.grand_total
import retailtouch.composeapp.generated.resources.order_summary
import retailtouch.composeapp.generated.resources.payment_total
import retailtouch.composeapp.generated.resources.scan
import retailtouch.composeapp.generated.resources.subtotal
import retailtouch.composeapp.generated.resources.tax
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
            NavigatorActions.navigateBack(navigator)
        }
    }

    LaunchedEffect(state.isError) {
        if (state.isError) {
            val errorTitle=getString(Res.string.error_title)
            snackbarHostState.value.showSnackbar("$errorTitle ${state.errorMsg}")
            viewModel.dismissErrorDialog()
        }
    }


    AppLeftSideMenu(
        syncInProgress = state.isLoading,
        modifier = Modifier.fillMaxSize(),
        content = {

            AppScreenPadding(
                content = { horizontalPadding, verticalPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalPadding, vertical = verticalPadding)) {

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

                        AppCircleProgressIndicator(
                            isVisible=state.isLoading
                        )

                        SnackbarHost(
                            hostState = snackbarHostState.value,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                })

        }
    )

    if(state.showPaymentCollectorDialog){
        println("state_remainingBalance:${state.remainingBalance}")
        PaymentCollectorDialog(
            isVisible = state.showPaymentCollectorDialog,
            totalValue = state.remainingBalance,
            paymentName = state.availablePayments.find { it.id == state.selectedPaymentTypesId }?.name ?: "Payment Amount",
            onPayClick = {payment->
                println("dialogAmount:$payment")
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
        onDismiss = {
            viewModel.updatePaymentSuccessDialog(false)
            viewModel.clearSale()
        },
        appliedPayments =abs(state.paymentTotal),
        balance = abs(state.remainingBalance),
        onPrinting = {
            viewModel.updatePaymentSuccessDialog(false)
            //viewModel.printPosPaymentReceipt()
        }
    )

}
@Composable
fun startPayment(viewModel: SharedPosViewModel, state: PosUIState) {
    viewModel.resetStartPaymentLibState()
    state.apply {
        val processorName = availablePayments.find { it.id == selectedPaymentTypesId }?.paymentProcessorName?:""
        val paymentName = availablePayments.find { it.id ==selectedPaymentTypesId }?.name?:""

        //paymentName should be replace with processorName
        if (paymentName.equals("RFM", ignoreCase = true)) {
            PaymentProvider().launchExternalApp(grandTotal, PaymentLibTypes.RFM, "")
        }else if (paymentName.equals("Ascan", ignoreCase = true)){
            PaymentProvider().launchExternalApp(
                paymentTotal,
                PaymentLibTypes.ASCAN,
                paymentName//processorName
            )
        }else if (paymentName.equals("Paytm", ignoreCase = true)) {
            //PaymentProvider().launchApi(PaymentLibTypes.PAYTM, httpClient = httpClient)
        }else if (paymentName.equals("Nets", ignoreCase = true)) {
            PaymentProvider().launchExternalApp(
                paymentTotal,
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
            .fillMaxSize()
            .padding(horizontal = AppTheme.dimensions.paddingH)
    ) {
        PaymentSelectionView(
            modifier = Modifier.weight(1f),
            availablePayments = state.availablePayments,
            onPaymentClick = { selectedPayment ->
                viewModel.onPaymentClicked(selectedPayment )
            }
        )

        OrderSummary(
            viewModel,
            onDeletePaymentClick = { id ->
                viewModel.updateDeletePaymentModeDialog(true)
                viewModel.updateSelectedPaymentToDeleteId(id)
            },
            subTotal = state.cartTotal,
            appliedTax = state.globalTax,
            granTotal = state.grandTotal,
            appliedDiscountTotal = ((state.globalDiscount)),
            payment = state.paymentTotal,
            balance = state.remainingBalance,
            payments = state.createdPayments
        )

        ButtonRowCard(
            modifier = Modifier.fillMaxWidth().height(AppTheme.dimensions.defaultButtonSize).padding(vertical = 10.dp),
            label = stringResource(Res.string.add_more),
            icons = AppIcons.addIcon,
            iconSize = AppTheme.dimensions.smallIcon,
            backgroundColor=AppTheme.colors.primaryColor,

            onClick = {
                viewModel.onPaymentClose()
            }
            //innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),
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
                .weight(1f)
                .padding(horizontal = 10.dp)
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
                subTotal = state.cartTotal,
                appliedTax = state.globalTax,
                granTotal = state.grandTotal,
                appliedDiscountTotal = ((state.globalDiscount)),
                payment = state.paymentTotal,
                balance = state.remainingBalance,
                payments = state.createdPayments
            )

            ButtonRowCard(
                modifier = Modifier.fillMaxWidth(7f).height(AppTheme.dimensions.defaultButtonSize).padding(vertical = 10.dp),
                label = stringResource(Res.string.add_more),
                icons = AppIcons.addIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.primaryColor,
                /*innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),*/
                onClick = {
                    viewModel.onPaymentClose()
                }
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(
            top = 4.dp,
            bottom = 60.dp
        )
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
    val cardColor = if (payment.isSelected) {
        AppTheme.colors.listItemSelectedCardColor
    } else {
        AppTheme.colors.listItemCardColor
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onClick(payment)
            },
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.appWhite),
        shape = AppTheme.appShape.card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = payment.name?:"",
                    style = AppTheme.typography.bodyNormal(),
                    color = AppTheme.colors.appWhite,
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
    Card(
        modifier = Modifier.fillMaxWidth().padding(2.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cartItemBg),
        elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
        shape = AppTheme.appShape.card
    ){
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {

            Text(
                text = stringResource(Res.string.order_summary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                style = AppTheme.typography.h1Medium(),
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            OrderSummaryRow(
                label = stringResource(Res.string.subtotal),
                value = subTotal,
                viewModel=viewModel
            )
            if (appliedDiscountTotal >0.0) {
                OrderSummaryRow(
                    label = "Discount",
                    value = appliedDiscountTotal,
                    viewModel=viewModel
                )
            }


            OrderSummaryRow(
                label = stringResource(Res.string.tax),
                value = appliedTax,
                viewModel=viewModel
            )

            OrderSummaryRow(
                label = stringResource(Res.string.grand_total),
                value = granTotal,
                viewModel=viewModel
            )

            OrderSummaryRow(
                label = stringResource(Res.string.payment_total),
                value = payment,
                viewModel=viewModel
            )

            if (payments.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppTheme.dimensions.paddingV)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    payments.forEach { payment ->
                        PaymentView(
                            onDeleteClick = onDeletePaymentClick,
                            payment = payment
                        )
                    }
                }

            }

            OrderSummaryRow(
                label = stringResource(Res.string.balance),
                value = balance,
                textStyle = AppTheme.typography.h1Medium(),
                primaryText = AppTheme.colors.textPrimary,
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
    primaryText: Color = AppTheme.colors.primaryText,
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
fun NumberPadContent(
    viewModel: SharedPosViewModel,
    screenState: PosUIState
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){

        NumberPad(
            textValue=screenState.inputDiscount,
            onValueChange = {amount->
                viewModel.updateEnterAmountValue(amount)
            },
            inputError=screenState.inputDiscountError,
            onNumberPadClick = {symbol->
                viewModel.onNumberPadClick(symbol)
            }, onApplyClick = {
                //viewModel.applyPaymentValue(payment)
            }, onCancelClick = {
                //viewModel.dismissNumberPadDialog()
            }
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
        modifier = Modifier.height(AppTheme.dimensions.defaultCardMinSize).wrapContentWidth().padding(vertical = if(appState.isTablet) AppTheme.dimensions.tabListVerPadding else AppTheme.dimensions.phoneListVerPadding, horizontal = AppTheme.dimensions.listHorPadding)
            .clickable{},
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.listItemCardColor),
        elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
        shape = AppTheme.appShape.cardRound
    ) {
        Row(modifier = Modifier
            .wrapContentWidth()
            .padding(vertical = AppTheme.dimensions.paddingV, horizontal = AppTheme.dimensions.paddingH),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spaceBetweenPadded(AppTheme.dimensions.paddingV)
        ){

            Text(
                text = "${payment.name} - ${payment.amount}",
                modifier = Modifier.wrapContentWidth(),
                style = AppTheme.typography.captionBold(),
                color = AppTheme.colors.appWhite
            )


            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AppTheme.colors.appWhite)
                    .clickable(
                        role = Role.Button,
                    ) {
                        onDeleteClick(payment.id)
                    }
            ) {
                Icon(
                    painter = painterResource(AppIcons.removeIcon),
                    contentDescription = null,
                    tint = AppTheme.colors.textError,
                    modifier = Modifier.size(AppTheme.dimensions.smallXIcon).padding(3.dp)
                )
            }
        }

    }
}

@Composable
fun ScreenTopContent(
    state : PosUIState,
    viewModel:SharedPosViewModel,
    appState: AppState,
    onSelection: (Any) -> Unit,
    onValueChange: (String) -> Unit,
    onTenderClick: () -> Unit,
    onListItemClick: (PaymentMethod) -> Unit,
    onDeletePaymentClick: (Int) -> Unit = {},
) {
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

    val (primaryText, textLabel) = when {
        state.remainingBalance == 0.0 -> {
            AppTheme.colors.secondaryText to "Amount Cleared"
        }
        state.remainingBalance < state.grandTotal -> {
            AppTheme.colors.textError to "Remaining"
        }
        state.remainingBalance == state.grandTotal -> {
            AppTheme.colors.textError to "Remaining"
        }
        else -> {
            AppTheme.colors.textError to "Unknown State"
        }
    }


    AppScreenPadding(
        content = { horizontalPadding, verticalPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalPadding, vertical = verticalPadding)) {

                Column(modifier=Modifier
                    .fillMaxHeight()
                    .weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {

                    /*Row(modifier=Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top){

                        AppOutlinedDropDown(
                            selectedValue = state.selectedDeliveryType.name,
                            options = state.deliveryTypeList,
                            label = stringResource(Res.string.type),
                            labelExtractor = {it.name},
                            modifier = Modifier.wrapContentHeight().weight(1f),
                            onValueChangedEvent = {selectedValue ->
                                onSelection.invoke(selectedValue)
                            }
                        )

                        AppOutlinedDropDown(
                            selectedValue = state.selectedStatusType.name,
                            options = state.statusTypeList,
                            label = stringResource(Res.string.status),
                            labelExtractor = {it.name},
                            modifier = Modifier.wrapContentHeight().weight(1f),
                            onValueChangedEvent = {selectedValue ->
                                onSelection.invoke(selectedValue)
                            }
                        )
                    }

                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top){

                        AppOutlinedTextField(
                            modifier = Modifier
                                .wrapContentHeight()
                                .weight(1f),
                            value = state.remark,
                            onValueChange = { remark ->
                                onValueChange(remark)
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType= KeyboardType.Text
                            ),
                            label = stringResource(Res.string.remarks),
                            singleLine = false,
                            minLines = 2,
                            focusedBorderColor = AppTheme.colors.primaryColor,
                            unfocusedBorderColor = AppTheme.colors.primaryColor,
                            error = null,
                            enabled = !state.isLoading
                        )

                        ClickableAppOutlinedTextField(
                            modifier = Modifier
                                .wrapContentHeight()
                                .weight(1f),
                            value = state.selectedDateTime,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType= KeyboardType.Text
                            ),
                            label = stringResource(Res.string.collection_date_time),
                            singleLine = true,
                            error = null,
                            leadingIcon = AppIcons.calenderIcon,
                            onClick = {
                                //open Calender
                            })
                    }*/

                    Row(modifier=Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.spaceBetweenPadded(5.dp), verticalAlignment = Alignment.Top) {

                        Text(
                            text = viewModel.formatPriceForUI(state.remainingBalance),
                            style = AppTheme.typography.amountLarge(),
                            color = primaryText,
                            minLines=1,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        )


                        Text(
                            text = viewModel.formatPriceForUI(state.grandTotal),
                            style = AppTheme.typography.amountLarge(),
                            color = AppTheme.colors.primaryText,
                            minLines=1,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        )
                    }

                    Row(modifier=Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.spaceBetweenPadded(5.dp), verticalAlignment = Alignment.Top) {

                        Text(
                            text = textLabel,
                            style = AppTheme.typography.titleNormal(),
                            color = primaryText,
                            minLines=1,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        )

                        Text(
                            text = state.totalLabel,
                            style = AppTheme.typography.titleNormal(),
                            color = AppTheme.colors.primaryText,
                            minLines=1,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        )
                    }

                    if (state.createdPayments.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppTheme.dimensions.paddingV)
                                .horizontalScroll(rememberScrollState()),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ){
                            /*state.createdPayments.forEach { payment ->
                                PaymentView(
                                    onDeleteClick = {
                                        onDeletePaymentClick.invoke(it)
                                    },
                                    payment = payment
                                )
                            }*/
                        }
                    }

                    LazyVerticalGridG(
                        modifier = Modifier.weight(1f),
                        items=state.availablePayments,
                        onClick = {item->
                            onListItemClick.invoke(item)
                        },
                    )

                    BottomButton(modifier  = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                        appState=appState,
                        onTenderClick = {
                            onTenderClick.invoke()
                        },
                        onCancelClick = {
                        },
                        onScanClick = {

                        }
                    )

                }

                AppCircleProgressIndicator(
                    isVisible=state.isLoading
                )

                SnackbarHost(
                    hostState = snackbarHostState.value,
                    modifier = Modifier.align(Alignment.TopCenter)

                )
            }
        }
    )
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




