package com.lfssolutions.retialtouch.presentation.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeUIState
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosPayment
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState
import com.lfssolutions.retialtouch.navigation.Navigator
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.AppCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.AppDialog
import com.lfssolutions.retialtouch.presentation.ui.common.AppLeftSideMenu
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedDropDown
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedTextField
import com.lfssolutions.retialtouch.presentation.ui.common.AppScreenPadding
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonCard
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonRowCard
import com.lfssolutions.retialtouch.presentation.ui.common.ClickableAppOutlinedTextField
import com.lfssolutions.retialtouch.presentation.ui.common.DeletePaymentModeDialog
import com.lfssolutions.retialtouch.presentation.ui.common.LazyVerticalGrid
import com.lfssolutions.retialtouch.presentation.ui.common.ListItemText
import com.lfssolutions.retialtouch.presentation.ui.common.NumberPad
import com.lfssolutions.retialtouch.presentation.viewModels.PaymentTypeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DiscountType
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.lfssolutions.retialtouch.utils.LocalSharedViewModel
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.collection_date_time
import retailtouch.composeapp.generated.resources.discount
import retailtouch.composeapp.generated.resources.ic_trash
import retailtouch.composeapp.generated.resources.remarks
import retailtouch.composeapp.generated.resources.scan
import retailtouch.composeapp.generated.resources.status
import retailtouch.composeapp.generated.resources.tender
import retailtouch.composeapp.generated.resources.type
import retailtouch.composeapp.generated.resources.zip_code

object PaymentTypeScreen:Screen{
    @Composable
    override fun Content() {
        Payment()
    }

}
@Composable
fun Payment(
    viewModel : PaymentTypeViewModel = koinInject(),
    sharedViewModel: SharedPosViewModel = koinInject()
){
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val state by viewModel.screenUIState.collectAsStateWithLifecycle()
    val posUIState by sharedViewModel.posUIState.collectAsState() // Collect state
     println("current_state: $posUIState")

    LaunchedEffect(Unit){
        viewModel.loadDataFromDatabases(posUIState)
    }

    LaunchedEffect(Unit){
        viewModel.fetchPaymentList()
    }

    LaunchedEffect(state.isPaymentClose){
        if(state.isPaymentClose){
            NavigatorActions.navigateBack(navigator)
        }
    }



    AppLeftSideMenu(
        modifier = Modifier.fillMaxSize(),
        onMenuItemClick={
            //navigator.handleNavigation(it)
        },
        content = {
            ScreenTopContent(
                state=state,
                appState=appState,
                onSelection = {selectedValue->
                    when(selectedValue){
                        is DeliveryType ->{
                            viewModel.updateDeliveryType(selectedValue)
                        }
                        is StatusType ->{
                            viewModel.updateStatusType(selectedValue)
                        }
                    }
                },
                onValueChange = {remark->
                    viewModel.updateRemark(remark)
                },
                onListItemClick = { selectedItem->
                    viewModel.updatePaymentById(selectedItem)
                },
                onListIconClick = {selectedItem->
                    viewModel.omPaymentIconClick(selectedItem)
                },
                onTenderClick = {
                    viewModel.onTenderClick()
                },
                onDeletePaymentClick = {id ->
                    viewModel.updateDeletePaymentModeDialog(true)
                    viewModel.updateSelectedPaymentToDeleteId(id)
                }
            )
        })

    AppDialog(
        isVisible = state.showPaymentCollectorDialog,
        onDismissRequest = {
            viewModel.dismissNumberPadDialog()
        },
        content = {
            NumberPadContent(
                viewModel=viewModel,
                screenState=state
            ) }
    )

    DeletePaymentModeDialog(
        isVisible = state.showDeletePaymentModeDialog,
        onDismiss = { viewModel.updateDeletePaymentModeDialog(false) },
        onConfirm = {
            viewModel.updateDeletePaymentModeDialog(false)
            viewModel.deletePayment()
        },
    )

}

@Composable
fun ScreenTopContent(
    state : PaymentTypeUIState,
    appState: AppState,
    onSelection: (Any) -> Unit,
    onValueChange: (String) -> Unit,
    onTenderClick: () -> Unit,
    onListItemClick: (PaymentTypeItem) -> Unit,
    onListIconClick: (PaymentTypeItem) -> Unit,
    onDeletePaymentClick: (Int) -> Unit = {},
) {
    val (textColor, textLabel) = when {
        state.remainingBalance == 0.0 -> {
            AppTheme.colors.textSecondary to "Amount Cleared"
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
                            focusedBorderColor = AppTheme.colors.textPrimaryBlue,
                            unfocusedBorderColor = AppTheme.colors.textPrimaryBlue,
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
                            text = "${state.currencySymbol} ${state.remainingBalance.roundTo()}",
                            style = AppTheme.typography.amountLarge(),
                            color = textColor,
                            minLines=1,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        )


                        Text(
                            text = "${state.currencySymbol} ${state.grandTotal.roundTo()}",
                            style = AppTheme.typography.amountLarge(),
                            color = AppTheme.colors.textColor,
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
                            color = textColor,
                            minLines=1,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        )

                        Text(
                            text = state.totalLabel,
                            style = AppTheme.typography.titleNormal(),
                            color = AppTheme.colors.textColor,
                            minLines=1,
                            maxLines = 1,
                            softWrap = true,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        )
                    }

                    if (state.isPaid) {
                        state.createdPayments.forEach { payment ->
                            PaymentView(
                                onDeleteClick = {
                                    onDeletePaymentClick.invoke(it)
                                },
                                payment = payment
                            )
                        }
                    }

                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f),
                        items=state.paymentList,
                        onClick = {item->
                            onListItemClick.invoke(item)
                        },
                        onIconClick = {item->
                            //onListIconClick.invoke(item)
                        }
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
                innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),
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
                innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),
                onClick = {
                    onCancelClick.invoke()
                }
            )

            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().weight(1f),
                label = stringResource(Res.string.tender),
                icons = AppIcons.applyIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.textPrimaryBlue,
                innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),
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
                innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),
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
                innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),
                onClick = {
                    onCancelClick.invoke()
                }
            )

            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                label = stringResource(Res.string.tender),
                icons = AppIcons.applyIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                backgroundColor=AppTheme.colors.textPrimaryBlue,
                innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonHorizontalPadding, vertical = AppTheme.dimensions.buttonVerticalPadding),
                onClick = {
                    onTenderClick.invoke()
                }
            )
        }
    }

}

@Composable
fun NumberPadContent(
    viewModel: PaymentTypeViewModel,
    screenState: PaymentTypeUIState
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
            textValue=screenState.inputAmount,
            onValueChange = {amount->
                viewModel.updateEnterAmountValue(amount)
            },
            inputError=screenState.inputDiscountError,
            onNumberPadClick = {symbol->
                viewModel.onNumberPadClick(symbol)
            }, onApplyClick = {
                viewModel.applyPaymentValue()
            }, onCancelClick = {
                viewModel.dismissNumberPadDialog()
            }
        )
    }
}

@Composable
fun PaymentView(
    onDeleteClick: (Int) -> Unit = {},
    payment: PosPayment
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = payment.name,
            modifier = Modifier.weight(1f),
            style = AppTheme.typography.captionBold(),
            color = AppTheme.colors.textPrimaryBlue
        )

        Text(
            text = payment.amount.toString(),
            modifier = Modifier.weight(1f),
            style = AppTheme.typography.captionBold(),
            color = AppTheme.colors.textPrimaryBlue,
            textAlign = TextAlign.End
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(AppTheme.colors.textWhite)
                .clickable(
                    role = Role.Button,
                ) {
                    onDeleteClick(payment.paymentTypeId)
                }
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(AppIcons.removeIcon),
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimensions.smallXIcon)
            )
        }
    }
}




