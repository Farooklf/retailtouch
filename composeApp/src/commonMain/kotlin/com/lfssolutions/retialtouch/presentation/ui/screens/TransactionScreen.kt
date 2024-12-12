package com.lfssolutions.retialtouch.presentation.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.App
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.AppCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.AppDropdownMenu
import com.lfssolutions.retialtouch.presentation.ui.common.AppHorizontalDivider
import com.lfssolutions.retialtouch.presentation.ui.common.AppPrimaryButton
import com.lfssolutions.retialtouch.presentation.ui.common.BasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.ClickableAppOutlinedTextField
import com.lfssolutions.retialtouch.presentation.ui.common.ListCenterText
import com.lfssolutions.retialtouch.presentation.ui.common.ListText
import com.lfssolutions.retialtouch.presentation.ui.common.PendingSaleDialog
import com.lfssolutions.retialtouch.presentation.ui.common.ShowDateRangePicker
import com.lfssolutions.retialtouch.presentation.viewModels.TransactionViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DateTime.formatDateForUI
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.date
import retailtouch.composeapp.generated.resources.end_date
import retailtouch.composeapp.generated.resources.hash
import retailtouch.composeapp.generated.resources.member
import retailtouch.composeapp.generated.resources.members
import retailtouch.composeapp.generated.resources.no_pending_sales
import retailtouch.composeapp.generated.resources.receipt_no
import retailtouch.composeapp.generated.resources.start_date
import retailtouch.composeapp.generated.resources.status
import retailtouch.composeapp.generated.resources.sync_pending
import retailtouch.composeapp.generated.resources.sync_trans
import retailtouch.composeapp.generated.resources.sync_transaction
import retailtouch.composeapp.generated.resources.total
import retailtouch.composeapp.generated.resources.transaction
import retailtouch.composeapp.generated.resources.type

object TransactionScreen : Screen {
    @Composable
    override fun Content() {
        TransactionUI()
    }
}

@Composable
fun TransactionUI(
    viewModel: TransactionViewModel = koinInject()
){

    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val uiUpdateStatus by viewModel.uiUpdateStatus.collectAsStateWithLifecycle()


    LaunchedEffect(uiUpdateStatus){
        if(uiUpdateStatus){
            viewModel.updateTransactionLoading(false)
            viewModel.updatePendingLoading(false)
            viewModel.updateLoader(false)
            viewModel.getSales()
        }
    }

    LaunchedEffect(Unit){
        viewModel.getAuthDetails()
        viewModel.loadFilterData()
        viewModel.getSales()
        viewModel.getPendingSales()
    }

    LaunchedEffect(screenState.isError){
        if(screenState.isError){
            snackbarHostState.value.showSnackbar(screenState.errorMessage)
            delay(2000)
            viewModel.updateError("",false)
        }
    }

    BasicScreen(
        modifier = Modifier.systemBarsPadding(),
        title = stringResource(Res.string.transaction),
        isTablet = appState.isTablet,
        contentMaxWidth = Int.MAX_VALUE.dp,
        onBackClick = {
            NavigatorActions.navigateBack(navigator)
        }
    ){
        val (vertPadding,horPadding)=if(appState.isPortrait)
            AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
        else
            AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20


        val (textStyleHeader,btnStyle)=if(appState.isPortrait)
            AppTheme.typography.bodyBold() to AppTheme.typography.captionBold()
        else
            AppTheme.typography.titleBold() to AppTheme.typography.bodyBold()

       val transactionLabel = if(appState.isPortrait)
            stringResource(Res.string.sync_trans)
        else
            stringResource(Res.string.sync_transaction)

        val pendingSalesError=stringResource(Res.string.no_pending_sales)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            LazyColumn(modifier = Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(5.dp)){
                item {
                    Column(modifier = Modifier.fillParentMaxWidth(),verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if(appState.isPortrait){
                            //Filter first row
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {

                                //Type
                                AppDropdownMenu(
                                    modifier = Modifier.weight(1f),
                                    selectedIndex = screenState.type,
                                    label = stringResource(Res.string.type),
                                    labelExtractor = { it.name },
                                    items = screenState.typeList,
                                    onItemSelected = { index, selectedValue ->
                                        viewModel.onSelectedType(selectedValue)
                                    })

                                //Status
                                AppDropdownMenu(
                                    modifier = Modifier.weight(1f),
                                    selectedIndex = screenState.status,
                                    label = stringResource(Res.string.status),
                                    labelExtractor = { it.name },
                                    items = screenState.statusList,
                                    onItemSelected = { index, selectedValue ->
                                        viewModel.onSelectedStatus(selectedValue)
                                    })

                            }

                            //Members
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically,) {
                                //Member
                                AppDropdownMenu(
                                    modifier = Modifier.weight(1f),
                                    selectedIndex = screenState.member.id,
                                    label = stringResource(Res.string.members),
                                    labelExtractor = { it.name },
                                    items = screenState.memberList,
                                    onItemSelected = { index, selectedValue ->
                                        viewModel.onSelectedMember(selectedValue)
                                    })
                            }
                        }else{
                            //Filter first row
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically,) {

                                //Type
                                AppDropdownMenu(
                                    modifier = Modifier.weight(1f),
                                    selectedIndex = screenState.type,
                                    label = stringResource(Res.string.type),
                                    labelExtractor = { it.name },
                                    items = screenState.typeList,
                                    onItemSelected = { index, selectedValue ->
                                        viewModel.onSelectedType(selectedValue)
                                    })

                                //Status
                                AppDropdownMenu(
                                    modifier = Modifier.weight(1f),
                                    selectedIndex = screenState.status,
                                    label = stringResource(Res.string.status),
                                    labelExtractor = { it.name },
                                    items = screenState.statusList,
                                    onItemSelected = { index, selectedValue ->
                                        viewModel.onSelectedStatus(selectedValue)
                                    })

                                //Member
                                AppDropdownMenu(
                                    modifier = Modifier.weight(1f),
                                    selectedIndex = screenState.member.id,
                                    label = stringResource(Res.string.members),
                                    labelExtractor = { it.name },
                                    items = screenState.memberList,
                                    onItemSelected = { index, selectedValue ->
                                        viewModel.onSelectedMember(selectedValue)
                                    })

                            }

                        }

                        //Date Range
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically,){
                            //start date
                            ClickableAppOutlinedTextField(
                                value = screenState.startDate,
                                label = stringResource(Res.string.start_date),
                                placeholder = stringResource(Res.string.start_date),
                                leadingIcon = AppIcons.calenderIcon,
                                enabled = false,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.updateDatePickerDialog(
                                        newValue = true,
                                        isFromDateValue = true
                                    )
                                }
                            )

                            ClickableAppOutlinedTextField(
                                value = screenState.endDate,
                                label = stringResource(Res.string.end_date),
                                placeholder = stringResource(Res.string.end_date),
                                leadingIcon = AppIcons.calenderIcon,
                                enabled = false,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.updateDatePickerDialog(
                                        newValue = true,
                                        isFromDateValue = false
                                    )
                                }
                            )
                        }

                        //List of transactions
                        //List UI Header
                        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = AppTheme.dimensions.padding15),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ){
                            if(appState.isPortrait){
                                ListCenterText(label = stringResource(Res.string.hash), textStyle = textStyleHeader, modifier = Modifier.wrapContentWidth().padding(end = AppTheme.dimensions.padding5))
                                ListCenterText(label = stringResource(Res.string.receipt_no), textStyle = textStyleHeader,modifier = Modifier.weight(1f))
                                ListCenterText(label = stringResource(Res.string.date),textStyle = textStyleHeader, modifier = Modifier.weight(1f))
                                ListCenterText(label = stringResource(Res.string.member),textStyle = textStyleHeader, modifier = Modifier.weight(1f))
                                ListCenterText(label = stringResource(Res.string.total), textStyle = textStyleHeader,modifier = Modifier.weight(.5f))
                            }else{
                                ListCenterText(
                                    label = stringResource(Res.string.hash),
                                    textStyle = textStyleHeader,
                                    modifier = Modifier.wrapContentWidth().padding(end = AppTheme.dimensions.padding5))
                                ListCenterText(
                                    label = stringResource(Res.string.receipt_no),
                                    textStyle = textStyleHeader,
                                    modifier = Modifier.weight(1f),
                                    arrangement = Arrangement.Start
                                    )

                                ListCenterText(
                                    label = stringResource(Res.string.date),
                                    textStyle = textStyleHeader,
                                    modifier = Modifier.weight(1f))
                                ListCenterText(
                                    label = stringResource(Res.string.member),
                                    textStyle = textStyleHeader,
                                    modifier = Modifier.weight(1f))
                                ListCenterText(
                                    label = stringResource(Res.string.total),
                                    textStyle = textStyleHeader,
                                    modifier = Modifier.weight(.5f)
                                )
                                ListCenterText(
                                    label = stringResource(Res.string.type),
                                    textStyle = textStyleHeader,
                                    modifier = Modifier.weight(.5f)
                                )
                                ListCenterText(
                                    label = stringResource(Res.string.status),
                                    textStyle = textStyleHeader,
                                    modifier = Modifier.weight(.5f),
                                    arrangement=Arrangement.End
                                )
                            }
                        }
                    }
                }

                var filteredSales=screenState.transactionSales

                if(screenState.isTypeFilter){
                    filteredSales  = filteredSales.filter {it.type == screenState.type}.toMutableList()
                }
                if(screenState.isStatusFilter){
                    filteredSales  = filteredSales.filter {it.status == screenState.status}.toMutableList()
                }
                if(screenState.isMemberFilter && screenState.memberId>0){
                    filteredSales  = filteredSales.filter {it.memberId == screenState.memberId}.toMutableList()
                }

                if(screenState.isFromDateFilter){
                    val startDate=LocalDate.parse(screenState.startDate)
                    filteredSales  = filteredSales.filter {it.date > startDate.minus(1, DateTimeUnit.DAY)}.toMutableList()
                }

                if(screenState.isEndDateFilter){
                    val endDate=LocalDate.parse(screenState.endDate)
                    filteredSales  = filteredSales.filter {it.date < endDate.plus(1, DateTimeUnit.DAY)}.toMutableList()
                }

                itemsIndexed(filteredSales
                ){index, sale ->
                    SaleListItem(
                        index = index,
                        item = sale,
                        typeList=screenState.typeList,
                        statusList=screenState.statusList,
                        viewModel=viewModel,
                        isPortrait = appState.isPortrait,
                        horizontalPadding=horPadding,
                        onItemClick = {
                            NavigatorActions.navigateToTransactionDetailsScreen(navigator,it)
                        }
                    )
                }
            }

            //Bottom Button
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                //Sync Pending
                AppPrimaryButton(
                    label = stringResource(Res.string.sync_pending),
                    leftIcon = AppIcons.syncIcon,
                    backgroundColor = AppTheme.colors.primaryColor,
                    disabledBackgroundColor = AppTheme.colors.primaryColor,
                    style = btnStyle,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    syncInProgress = screenState.isSalePendingSync,
                    onClick = {
                        if(screenState.pendingSales.isEmpty()){
                            viewModel.updateError(pendingSalesError,true)
                        }else{
                            viewModel.updatePendingSalePopupState(true)
                        }
                    }
                )

                //sync transaction
                AppPrimaryButton(
                    label = transactionLabel,
                    leftIcon = AppIcons.syncIcon,
                    backgroundColor = AppTheme.colors.appRed,
                    disabledBackgroundColor = AppTheme.colors.appRed,
                    style = btnStyle,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    syncInProgress = screenState.isSaleTransactionSync,
                    onClick = {
                       viewModel.syncTransaction()
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

    if (screenState.isDatePickerDialog) {
        ShowDateRangePicker(
            onDismiss = {
                viewModel.updateDatePickerDialog(
                    newValue = false,
                    isFromDateValue = screenState.isFromDate
                )
            },
            onConfirmClicked = { selectedValue ->
                if (screenState.isFromDate)
                    viewModel.updateStartDate(selectedValue)
                else
                    viewModel.updateEndDate(selectedValue)
            }
        )
    }

    PendingSaleDialog(
        state = screenState,
        viewModel=viewModel,
        isVisible = screenState.showPendingSalePopup,
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
        onDismiss = {
            viewModel.updatePendingSalePopupState(false)
        }
    )
}


@Composable
fun SaleListItem(
    index:Int,
    item: SaleRecord,
    typeList: List<DeliveryType>,
    statusList: List<StatusType>,
    isPortrait:Boolean,
    horizontalPadding: Dp,
    viewModel: TransactionViewModel,
    onItemClick:(SaleRecord)->Unit={}
){

    val (borderColor,rowBgColor)=when(index%2 == 0){
        true->  AppTheme.colors.listRowBorderColor to AppTheme.colors.listRowBgColor
        false ->AppTheme.colors.appWhite to AppTheme.colors.appWhite
    }


    val textStyle=if(isPortrait)
        AppTheme.typography.bodyMedium()
    else
        AppTheme.typography.titleMedium()

    if(isPortrait){
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite).clickable { onItemClick.invoke(item) }){
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor),
                verticalArrangement = Arrangement.spaceBetweenPadded(10.dp)) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    ListText(
                        label = "${index+1}",
                        textStyle = AppTheme.typography.captionMedium(),
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.wrapContentWidth()
                    )
                    //Items total
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spaceBetweenPadded(1.dp), verticalAlignment = Alignment.CenterVertically) {
                        //Items name
                        ListText(
                            label = item.receiptNumber?:"N/A",
                            textStyle = AppTheme.typography.captionBold(),
                            color = AppTheme.colors.textBlack,
                            modifier = Modifier.wrapContentWidth()
                        )

                        ListText(
                            label = viewModel.formatPriceForUI(item.amount),
                            textStyle = textStyle,
                            color = AppTheme.colors.appRed,
                            modifier = Modifier.wrapContentWidth()
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Spacer(modifier = Modifier.weight(.5f))

                    //Date
                    ListText(
                        label = formatDateForUI(item.creationDate),
                        textStyle = AppTheme.typography.captionBold(),
                        color = AppTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    ListText(
                        label = item.memberName?:"",
                        textStyle = AppTheme.typography.captionMedium(),
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.weight(1f)
                    )
                }

                if(item.type>0 || item.status>0){
                    Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        if(item.type>0){
                            ListText(
                                label = "Type : ${typeList.firstOrNull{it.id==item.type}?.name}",
                                textStyle = AppTheme.typography.captionBold(),
                                color = AppTheme.colors.appRed,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                        if(item.status >0){
                            ListText(
                                label = "Status : ${statusList.firstOrNull{it.id==item.status}?.name}",
                                textStyle = AppTheme.typography.captionBold(),
                                color = AppTheme.colors.appGreen,
                                modifier = Modifier.wrapContentWidth()
                            )
                        }
                    }
                }

                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    } else{
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite).clickable { onItemClick.invoke(item)}){
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor),
                verticalArrangement = Arrangement.spaceBetweenPadded(10.dp)) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ListCenterText(
                        label = "${index+1}",
                        textStyle = textStyle,
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.wrapContentWidth().padding(end = AppTheme.dimensions.padding5)
                    )

                    //Items name
                    ListCenterText(
                        label = item.receiptNumber,
                        textStyle = textStyle,
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.weight(1f),
                        arrangement = Arrangement.Start
                    )

                    //Date
                    ListCenterText(
                        label = formatDateForUI(item.creationDate),
                        textStyle = textStyle,
                        color = AppTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    ListCenterText(
                        label = item.memberName?:"",
                        textStyle = textStyle,
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.weight(1f)
                    )
                    //Items total
                    ListCenterText(
                        label = viewModel.formatPriceForUI(item.amount),
                        textStyle = textStyle,
                        color = AppTheme.colors.appRed,
                        modifier = Modifier.weight(.5f)
                    )
                    val type=if(item.type >0)
                        statusList.firstOrNull{it.id==item.type}?.name?:"N/A"
                    else
                        "N/A"

                    ListCenterText(
                        label = type,
                        textStyle = AppTheme.typography.bodyBold(),
                        color = AppTheme.colors.appRed,
                        modifier = Modifier.weight(.5f)
                    )

                    val status=if(item.status >0)
                        statusList.firstOrNull{it.id==item.status}?.name?:"N/A"
                    else
                        "N/A"

                    ListCenterText(
                        label = status,
                        textStyle = AppTheme.typography.bodyBold(),
                        color = AppTheme.colors.appGreen,
                        modifier = Modifier.weight(.5f),
                        arrangement=Arrangement.End
                    )
                }
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    }
}





