package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeUIState
import com.lfssolutions.retialtouch.navigation.Navigator
import com.lfssolutions.retialtouch.presentation.ui.common.AppCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.AppDialog
import com.lfssolutions.retialtouch.presentation.ui.common.AppLeftSideMenu
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedDropDown
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedTextField
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonCard
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonRowCard
import com.lfssolutions.retialtouch.presentation.ui.common.ClickableAppOutlinedTextField
import com.lfssolutions.retialtouch.presentation.ui.common.LazyVerticalGrid
import com.lfssolutions.retialtouch.presentation.ui.common.ListItemText
import com.lfssolutions.retialtouch.presentation.ui.common.NumberPad
import com.lfssolutions.retialtouch.presentation.viewModels.PaymentTypeViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DiscountType
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.collection_date_time
import retailtouch.composeapp.generated.resources.discount
import retailtouch.composeapp.generated.resources.remarks
import retailtouch.composeapp.generated.resources.scan
import retailtouch.composeapp.generated.resources.status
import retailtouch.composeapp.generated.resources.tender
import retailtouch.composeapp.generated.resources.type
import retailtouch.composeapp.generated.resources.zip_code

data class PaymentTypeScreen(val memberId: Int,val totalAmount: Double):Screen{
    @Composable
    override fun Content() {
        Payment(memberId = memberId,totalAmount=totalAmount)
    }

}
@Composable
fun Payment(
    memberId: Int,
    totalAmount: Double,
    viewModel : PaymentTypeViewModel = koinInject()
){
    // Access appState from CompositionLocal
    val appState = LocalAppState.current
     println("Screen width is ${appState.screenWidth}")

    val screenUIState by viewModel.screenUIState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        viewModel.loadDataFromDatabases(memberId,totalAmount)
    }


    AppLeftSideMenu(
        modifier = Modifier.fillMaxSize(),
        onMenuItemClick={
            //navigator.handleNavigation(it)
        },
        content = {

            ScreenTopContent(
                screenUIState=screenUIState,
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
                }
            )


        })

    AppDialog(
        isVisible = screenUIState.isShowCalculator,
        onDismissRequest = {
            viewModel.dismissNumberPadDialog()
        },
        content = {
            NumberPadContent(
                viewModel=viewModel,
                screenState=screenUIState
            ) }
    )

}

@Composable
fun ScreenTopContent(
    screenUIState : PaymentTypeUIState,
    appState: AppState,
    onSelection: (Any) -> Unit,
    onValueChange: (String) -> Unit,
    onListItemClick: (PaymentTypeItem) -> Unit,
    onListIconClick: (PaymentTypeItem) -> Unit,
) {
    val (textColor, textLabel) = when {
        screenUIState.remainingAmount == 0.0 -> {
            AppTheme.colors.textSecondary to "Amount Cleared"
        }
        screenUIState.remainingAmount < screenUIState.totalAmount -> {
            AppTheme.colors.textError to "Remaining"
        }
        screenUIState.remainingAmount == screenUIState.totalAmount -> {
            AppTheme.colors.textError to "Remaining"
        }
        else -> {
            AppTheme.colors.textError to "Unknown State"
        }
    }


    Box(modifier = Modifier.fillMaxSize().padding(if(appState.isTablet) AppTheme.dimensions.tabPadding else AppTheme.dimensions.phonePadding)) {

        Column(modifier=Modifier
            .fillMaxHeight()
            .weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {

            Row(modifier=Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top){

                AppOutlinedDropDown(
                    selectedValue = screenUIState.selectedDeliveryType.name,
                    options = screenUIState.deliveryTypeList,
                    label = stringResource(Res.string.type),
                    labelExtractor = {it.name},
                    modifier = Modifier.wrapContentHeight().weight(1f),
                    onValueChangedEvent = {selectedValue ->
                        onSelection.invoke(selectedValue)
                    }
                )

                AppOutlinedDropDown(
                    selectedValue = screenUIState.selectedStatusType.name,
                    options = screenUIState.statusTypeList,
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
                    value = screenUIState.remark,
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
                    enabled = !screenUIState.isLoading
                )

                ClickableAppOutlinedTextField(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f),
                    value = screenUIState.selectedDateTime,
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
            }

            Row(modifier=Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.spaceBetweenPadded(5.dp), verticalAlignment = Alignment.Top) {

                Text(
                    text = "${screenUIState.currencySymbol} ${screenUIState.remainingAmount.roundTo()}",
                    style = AppTheme.typography.amountLarge(),
                    color = textColor,
                    minLines=1,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                )


                Text(
                    text = "${screenUIState.currencySymbol} ${screenUIState.totalAmount.roundTo()}",
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
                    text = screenUIState.totalLabel,
                    style = AppTheme.typography.titleNormal(),
                    color = AppTheme.colors.textColor,
                    minLines=1,
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                )
            }

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                items=screenUIState.paymentList,
                onClick = {item->
                    onListItemClick.invoke(item)
                },
                onIconClick = {item->
                    onListIconClick.invoke(item)
                }
            )

        }

        AppCircleProgressIndicator(
            isVisible=screenUIState.isLoading
        )

        BottomButton(modifier  = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalScroll(rememberScrollState())
            .align(Alignment.BottomStart),
            appState=appState,
            onTenderClick = {

            },
            onCancelClick = {
            },
            onScanClick = {

            }
        )
    }


}

@Composable
fun BottomButton(modifier: Modifier, appState: AppState,  onTenderClick: () -> Unit, onCancelClick: () -> Unit, onScanClick: () -> Unit) {

    Row(modifier=modifier, horizontalArrangement = Arrangement.Center) {
        if(appState.isTablet){
            Row(modifier=Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {

                ButtonRowCard(
                    modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                    label = stringResource(Res.string.scan),
                    icons = AppIcons.settingIcon,
                    iconSize = AppTheme.dimensions.smallIcon,
                    backgroundColor=AppTheme.colors.appGreen,
                    innerPaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                    onClick = {
                        onScanClick.invoke()
                    }
                )

                ButtonRowCard(
                    modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                    label = stringResource(Res.string.cancel),
                    icons = AppIcons.settingIcon,
                    iconSize = AppTheme.dimensions.smallIcon,
                    backgroundColor=AppTheme.colors.textError,
                    innerPaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                    onClick = {
                        onCancelClick.invoke()
                    }
                )

                ButtonRowCard(
                    modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                    label = stringResource(Res.string.tender),
                    icons = AppIcons.applyIcon,
                    iconSize = AppTheme.dimensions.smallIcon,
                    backgroundColor=AppTheme.colors.textPrimaryBlue,
                    innerPaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                    onClick = {
                        onTenderClick.invoke()
                    }
                )
            }
        }
        else{
            Column(modifier=Modifier.fillMaxWidth().wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                ButtonRowCard(
                    modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                    label = stringResource(Res.string.scan),
                    icons = AppIcons.settingIcon,
                    iconSize = AppTheme.dimensions.smallIcon,
                    backgroundColor=AppTheme.colors.appGreen,
                    innerPaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                    onClick = {
                        onScanClick.invoke()
                    }
                )

                ButtonRowCard(
                    modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                    label = stringResource(Res.string.cancel),
                    icons = AppIcons.settingIcon,
                    iconSize = AppTheme.dimensions.smallIcon,
                    backgroundColor=AppTheme.colors.textError,
                    innerPaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                    onClick = {
                        onCancelClick.invoke()
                    }
                )

                ButtonRowCard(
                    modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                    label = stringResource(Res.string.tender),
                    icons = AppIcons.applyIcon,
                    iconSize = AppTheme.dimensions.smallIcon,
                    backgroundColor=AppTheme.colors.textPrimaryBlue,
                    innerPaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                    onClick = {
                        onTenderClick.invoke()
                    }
                )
            }
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
                viewModel.onNumberPadApplyClick()
            }, onCancelClick = {
                viewModel.dismissNumberPadDialog()
            }
        )
    }
}




