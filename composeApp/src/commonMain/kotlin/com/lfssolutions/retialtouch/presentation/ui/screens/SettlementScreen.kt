package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.settlement.PosPaymentTypeSummary
import com.lfssolutions.retialtouch.domain.model.settlement.SettlementUIState
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.ActionDialog
import com.lfssolutions.retialtouch.presentation.ui.common.AppHorizontalDivider
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedTextField
import com.lfssolutions.retialtouch.presentation.ui.common.AppPrimaryButton
import com.lfssolutions.retialtouch.presentation.ui.common.AppScreenCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.BasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.ListText
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.PendingSaleDialog
import com.lfssolutions.retialtouch.presentation.viewModels.SettlementViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.lfssolutions.retialtouch.utils.formatPrice
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.app_logo
import retailtouch.composeapp.generated.resources.pending
import retailtouch.composeapp.generated.resources.print
import retailtouch.composeapp.generated.resources.retail_pos
import retailtouch.composeapp.generated.resources.settlement
import retailtouch.composeapp.generated.resources.submit
import retailtouch.composeapp.generated.resources.sync
import retailtouch.composeapp.generated.resources.sync_desc

object SettlementScreen : Screen {

    @Composable
    override fun Content() {
        SettlementUI()
    }

    @Composable
    fun SettlementUI(
        viewModel : SettlementViewModel = koinInject()
    ) {

        val screenState by viewModel.settlementState.collectAsStateWithLifecycle()
        val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
        val navigator = LocalNavigator.currentOrThrow
        val appState = LocalAppState.current
        val currencySymbol by viewModel.currencySymbol.collectAsState()

       LaunchedEffect(Unit){
           viewModel.loadDataFromDb()
       }

        LaunchedEffect(screenState.isError) {
            if (screenState.isError) {
                snackbarHostState.value.showSnackbar(screenState.errorDesc)
                delay(1000)
                viewModel.updateError(error="",isError = false)
            }
        }

        BasicScreen(
            modifier = Modifier.systemBarsPadding(),
            screenBackground = AppTheme.colors.screenBackground,
            title = stringResource(Res.string.settlement),
            isTablet = appState.isTablet,
            contentMaxWidth = Int.MAX_VALUE.dp,
            onBackClick = {
                navigator.pop()
            }
        ){
            val txtStyle=if(appState.isTablet)
                AppTheme.typography.bodyBold()
            else
                AppTheme.typography.captionBold()


            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(screenState.localSettlement,key={item -> item.paymentTypeId!!}) {payment ->
                        PaymentItem(
                            localPayment = payment,
                            screenState = screenState,
                            viewModel=viewModel,
                            onValueChanged = { amount,paymentItem->
                                viewModel.updateAmount(amount,paymentItem)
                            },
                            isTablet = appState.isTablet
                        )
                    }
                }

                //Action Button
                Row(modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(if(appState.isTablet) 10.dp else 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if(appState.isTablet) 10.dp else 5.dp)) {

                    //Print Button
                    AppPrimaryButton(
                        label = stringResource(Res.string.print),
                        backgroundColor = AppTheme.colors.textPrimary,
                        disabledBackgroundColor = AppTheme.colors.textPrimary,
                        style = txtStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                           viewModel.connectAndPrintTemplate()
                        })

                    //Pending Button
                    AppPrimaryButton(
                        label = stringResource(Res.string.pending),
                        backgroundColor = AppTheme.colors.appRed,
                        disabledBackgroundColor = AppTheme.colors.appRed,
                        style = txtStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                         viewModel.updatePendingSalesDialog(true)
                        })

                    //submit Button
                    AppPrimaryButton(
                        label = stringResource(Res.string.submit),
                        backgroundColor = AppTheme.colors.appGreen,
                        disabledBackgroundColor = AppTheme.colors.appGreen,
                        style = txtStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                           viewModel.submitPOSSettlement()
                        })
                }
            }

            /*AppBaseCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = if (appState.isTablet) 20.dp else 0.dp,
                        top = if (appState.isTablet) 10.dp else 0.dp,
                        bottom = if (appState.isTablet) 10.dp else 0.dp,
                        end = if (appState.isTablet) 20.dp else 0.dp
                    )
            ){

            }*/

            AppScreenCircleProgressIndicator(isVisible = screenState.isLoading)
        }

        ActionDialog(
            isVisible = screenState.showPendingSalesMessage,
            dialogTitle = stringResource(Res.string.retail_pos),
            dialogMessage = stringResource(Res.string.sync_desc),
            confirmButtonTxt = stringResource(Res.string.sync),
            onDismissRequest = {
                viewModel.updatePendingSalesMessage(false)
            },
            onCancel = {
                viewModel.updatePendingSalesMessage(false)
            },
            onConfirm = {
                viewModel.updatePendingSalesMessage(false)
               viewModel.syncPendingSales()
            }
        )

        PendingSaleDialog(
            isVisible = screenState.showPendingSales,
            pendingSales = screenState.pendingSales,
            currency=currencySymbol,
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            onDismiss = {
                viewModel.updatePendingSalesDialog(false)
            },
            onRemove = {saleItem->
             viewModel.deletePendingSaleItem(saleItem)
            },
            onSyncAll={
                viewModel.updateSyncProgress(true)
                viewModel.updatePendingSalesDialog(false)
                 viewModel.syncPendingSales()
            }
        )
    }
}

@Composable
private fun PaymentItem(
    localPayment: PosPaymentTypeSummary,
    screenState: SettlementUIState,
    viewModel: SettlementViewModel,
    isTablet: Boolean = false,
    onValueChanged: (String, PosPaymentTypeSummary) -> Unit,
) {
    //println("ListPaymentMap : $paymentsMap")
    //val tenderedAmount by rememberUpdatedState(paymentsMap[mPaymentTypes.id] ?: 0.0)

    /* val paymentAmounts = remember(paymentsMap, mPaymentTypes) {
         paymentsMap.filterKeys { it == mPaymentTypes.id }
     }*/

    val currencySymbol by viewModel.currencySymbol.collectAsState()

    val paddingHorizontal = if (isTablet) 10.dp else 5.dp
    val paddingVertical = if (isTablet) 6.dp else 5.dp

    val textStyle = if (isTablet) AppTheme.typography.bodyMedium() else AppTheme.typography.captionMedium()

    val imageWidth by remember(localPayment, isTablet) {
        mutableStateOf(if (isTablet) 80.dp else 50.dp)
    }

    val imageHeight by remember(localPayment, isTablet) {
        mutableStateOf(if (isTablet) 50.dp else 30.dp)
    }

    val remoteItem = screenState.remoteSettlement?.items?.firstOrNull{ pts->pts.paymentTypeId==localPayment.paymentTypeId}
        ?:PosPaymentTypeSummary(paymentTypeId= localPayment.paymentTypeId, paymentType= localPayment.paymentType, imageUrl = localPayment.imageUrl, amount=0.0)

    Column(modifier = Modifier.fillMaxWidth().wrapContentWidth().background(AppTheme.colors.secondaryBg), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = paddingHorizontal, vertical = paddingVertical), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {

            Card(
                modifier = Modifier.width(imageWidth).height(imageHeight),
                shape = RoundedCornerShape(5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppTheme.colors.cardBgColor
                )
            ) {

                Image(
                    painter = painterResource(Res.drawable.app_logo),
                    contentDescription = localPayment.paymentType,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(imageHeight),
                    contentScale = ContentScale.Crop,
                )
                /*SubcomposeAsyncImage(
                    model = localPayment.imageUrl?.toImageFiles()?.getOrNull(0) ?: "",
                    contentDescription = localPayment.paymentType,
                    modifier = Modifier
                        .width(imageWidth)
                        .height(imageHeight),
                    contentScale = ContentScale.Crop,
                    error = {
                        Image(
                            painter = painterResource(Res.drawable.app_logo),
                            contentDescription = localPayment.paymentType,
                            modifier = Modifier
                                .width(imageWidth)
                                .height(imageHeight),
                            contentScale = ContentScale.Crop,
                        )
                    }
                )*/
            }

            Column(modifier = Modifier.wrapContentWidth()) {
                ListText(
                    label = "${remoteItem.paymentType} : ${formatPrice(remoteItem.amount,currencySymbol)}",
                    color = AppTheme.colors.primaryText,
                    textStyle = textStyle
                )
                if(remoteItem.paymentType.equals("cash", ignoreCase = true) && screenState.remoteSettlement?.floatMoney!=0.0){
                    ListText(
                        label = "$ Float Amount : ${formatPrice(remoteItem.amount,currencySymbol)}",
                        color = AppTheme.colors.primaryText,
                        textStyle = AppTheme.typography.bodyMedium(),
                        modifier = Modifier.wrapContentWidth().padding(top = 5.dp)
                    )
                }
            }

            AppOutlinedTextField(
                modifier = Modifier
                    .wrapContentHeight()
                    .weight(1f),
                value = localPayment.enteredAmount,
                onValueChange = { newValue ->
                    onValueChanged.invoke(newValue,localPayment)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Decimal
                ),
                label = "",
                placeholder = "",
                singleLine = true,
                focusedBorderColor=AppTheme.colors.primaryColor,
                unfocusedBorderColor =AppTheme.colors.secondaryColor)

            Text(
                text = formatPrice(localPayment.amount?.minus(remoteItem.amount?:0.0),currencySymbol),
                style = AppTheme.typography.h1Medium()
                    .copy(fontSize = if (isTablet) 25.sp else 20.sp),
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(.5f).padding(start = if (isTablet) 5.dp else 0.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AppHorizontalDivider(
            color = AppTheme.colors.borderColor
        )
     }

}