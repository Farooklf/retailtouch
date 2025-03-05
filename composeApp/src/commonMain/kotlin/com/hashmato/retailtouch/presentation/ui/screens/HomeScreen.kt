package com.hashmato.retailtouch.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.domain.model.login.AuthenticateDao
import com.hashmato.retailtouch.domain.model.login.RTLoginUser
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.navigation.NavigatorActions.navigateToSettlementScreen
import com.hashmato.retailtouch.presentation.common.AppScreenPadding
import com.hashmato.retailtouch.presentation.common.CustomToast
import com.hashmato.retailtouch.presentation.common.GradientBackgroundScreen
import com.hashmato.retailtouch.presentation.common.ListGridItems
import com.hashmato.retailtouch.presentation.common.dialogs.ActionDialog
import com.hashmato.retailtouch.presentation.common.getGridCell
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.presentation.viewModels.HomeViewModel
import com.hashmato.retailtouch.sync.SyncViewModel
import com.hashmato.retailtouch.utils.ConnectivityObserver
import com.hashmato.retailtouch.utils.DateTimeUtils
import com.hashmato.retailtouch.utils.HomeItemId
import com.hashmato.retailtouch.utils.exitApp
import com.hashmato.retailtouch.utils.getAppVersion
import com.outsidesource.oskitcompose.router.KMPBackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.exit_app_message
import retailtouch.composeapp.generated.resources.home_header
import retailtouch.composeapp.generated.resources.retail_pos
import retailtouch.composeapp.generated.resources.sync_progress


data class HomeScreen(val isSplash: Boolean): Screen {
    @Composable
    override fun Content() {
       val homeViewModel: HomeViewModel = koinInject()
        val appThemeContext=AppTheme.context
        val navigator = appThemeContext.getAppNavigator()
       Home(homeViewModel=homeViewModel,isFromSplash = isSplash,onLogout={mRTUser->
           appThemeContext.navigateBackToLoginScreen(navigator,mRTUser)
       })
    }
}

@Composable
fun Home(
    homeViewModel: HomeViewModel,
    syncViewModel: SyncViewModel= koinInject(),
    isFromSplash:Boolean,
    onLogout: @Composable (RTLoginUser?) -> Unit
    )
{
    val appThemeContext = AppTheme.context
    val navigator=appThemeContext.getAppNavigator()
    val state by homeViewModel.homeUIState.collectAsStateWithLifecycle()
    val syncDataState by syncViewModel.syncDataState.collectAsStateWithLifecycle()
    //val isConnected by ConnectivityObserver.isConnected.collectAsState()

    val coroutineScope= rememberCoroutineScope()
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val appVersion = remember { getAppVersion() }


    KMPBackHandler(true, onBack = {
        homeViewModel.updateExitFormDialogState(true)
    })


    LaunchedEffect(state.isSyncEveryThing){
        println("${homeViewModel.isCallCompleteSync()}")
        if(state.isSyncEveryThing)
        {
            syncViewModel.startCompleteSync()
        }
    }

    LaunchedEffect(state.isError) {
        if (state.isError) {
            snackbarHostState.value.showSnackbar(state.errorMsg)
            delay(1000)
            homeViewModel.resetError()
        }
    }

    LaunchedEffect(isFromSplash) {
        if (isFromSplash && !state.hasEmployeeLoggedIn) {
            homeViewModel.prepareHomeData()
            homeViewModel.initialiseEmpScreen(isFromSplash)
        }
    }


    LaunchedEffect(syncDataState.syncInProgress){
        if(!syncDataState.syncInProgress){
            homeViewModel.updateSyncEverythingState(false)
            homeViewModel.stopSyncRotation(syncDataState.syncInProgress)
        }
    }


    Box(modifier = Modifier.fillMaxSize()){
        GradientBackgroundScreen(
            modifier = Modifier.fillMaxSize(),
            isBlur = state.isBlur
        ){
            val (syncTitle,syncDescription)= if(syncDataState.syncInProgress) stringResource(Res.string.sync_progress) to "(${syncDataState.syncCount} /${syncDataState.syncTotalCount} ${syncDataState.syncProgressStatus})" else stringResource(Res.string.home_header) to ""

            // The main screen
            AppScreenPadding(content = {horizontalPadding, verticalPadding ->

                Box(modifier = Modifier.fillMaxSize()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // Top section with time and status
                        TopSection(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            state.authUser)


                        LazyVerticalGrid(
                            columns = GridCells.Fixed( getGridCell(appThemeContext.deviceType)),
                            modifier = Modifier.weight(1f).padding(vertical = AppTheme.dimensions.padding10),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement=Arrangement.SpaceEvenly
                        ) {
                            item(span = { GridItemSpan(getGridCell(appThemeContext.deviceType)) }) {
                                CurrentTimeDisplay()
                            }

                            if(syncTitle.trim().isNotEmpty()){
                                item(span = { GridItemSpan(getGridCell(appThemeContext.deviceType))}) {
                                    Text(text = syncTitle,
                                        style = AppTheme.typography.titleBold(),
                                        color = AppTheme.colors.appWhite,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            if(syncDescription.trim().isNotEmpty()){
                                item(span = { GridItemSpan(getGridCell(appThemeContext.deviceType))}) {
                                    Text(text = syncDescription,
                                        style = AppTheme.typography.titleMedium(),
                                        color = AppTheme.colors.appWhite,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }


                            ListGridItems(state.homeItemList,syncDataState.syncInProgress){id->
                                when(id){
                                    HomeItemId.CASHIER_ID->{ //Cashier
                                        NavigatorActions.navigateToPOSScreen(navigator)
                                    }
                                    HomeItemId.MEMBER_ID->{
                                        appThemeContext.navigateToMemberScreen(navigator)
                                    }
                                    HomeItemId.STOCK_ID->{
                                        appThemeContext.navigateToStockScreen(navigator)
                                    }
                                    HomeItemId.RECEIPT_ID->{
                                        NavigatorActions.navigateToTransactionScreen(navigator)
                                    }
                                    HomeItemId.SETTLEMENT_ID->{
                                        navigateToSettlementScreen(navigator)
                                    }
                                    HomeItemId.PAYOUT_ID->{
                                        appThemeContext.navigateToPayoutScreen(navigator)
                                    }

                                    HomeItemId.SYNC_ID->{ //Sync
                                        homeViewModel.updateSyncRotation(id)
                                        coroutineScope.launch {
                                            homeViewModel.updateSyncEverythingState(true)
                                        }
                                    }
                                    HomeItemId.PRINTER_ID->{
                                        NavigatorActions.navigateToPrinterScreen(navigator)
                                    }
                                    HomeItemId.DRAWER_ID->{
                                        homeViewModel.openDrawerModule()
                                    }
                                    HomeItemId.SETTING_ID->{
                                        NavigatorActions.navigateToSettingScreen(navigator)
                                    }
                                    HomeItemId.LOGOUT_ID->{
                                        homeViewModel.updateEmployeeStatus()
                                    }
                                }
                            }
                        }

                        Text(
                            text = "App Version : $appVersion",
                            style = AppTheme.typography.titleMedium(),
                            color = AppTheme.colors.appWhite,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = state.authUser.serverURL,
                            style = AppTheme.typography.titleMedium(),
                            color = AppTheme.colors.appWhite,
                            textAlign = TextAlign.Center
                        )
                    }

                    SnackbarHost(
                        hostState = snackbarHostState.value,
                        modifier = Modifier
                            .align(Alignment.TopCenter))
                  }

                  CustomToast() // Always included to show messages
                }
            )
        }

        if (state.isFromSplash && !state.hasEmployeeLoggedIn) {
            EmployeeScreen(
                onNavigateLogout = {mRTUser->
                    onLogout.invoke(mRTUser)
                },
                onDismiss = {
                    homeViewModel.onEmployeeLoggedIn() }
            )
        }
    }

    ActionDialog(
        isVisible = state.showExitConfirmationDialog,
        dialogTitle = stringResource(Res.string.retail_pos),
        dialogMessage = stringResource(Res.string.exit_app_message),
        onDismissRequest = {
            homeViewModel.updateExitFormDialogState(false)
        },
        onCancel = {
            homeViewModel.updateExitFormDialogState(false)
        },
        onConfirm = {
            homeViewModel.updateExitFormDialogState(false)
            exitApp() // Calls the platform-specific `exitApp` implementation
        }
    )
}


@Composable
fun TopSection(modifier : Modifier, user: AuthenticateDao) {

    //Location Row
    Row(modifier=modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(modifier = Modifier.wrapContentWidth().wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = vectorResource(AppIcons.locationIcon),
                tint = AppTheme.colors.appWhite,
                contentDescription = "",
                modifier = Modifier
                    .size(AppTheme.dimensions.smallIcon)
            )
             //Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "${user.loginDao.defaultLocation} | ${user.loginDao.locationCode}",
                color = AppTheme.colors.appWhite,
                style = AppTheme.typography.titleMedium(),
            )
        }
        //Employee
        Row(modifier = Modifier.wrapContentWidth().wrapContentHeight(), horizontalArrangement = Arrangement.spacedBy(10.dp),verticalAlignment = Alignment.CenterVertically){

            Text(
                text = user.loginDao.userName.uppercase(),
                color = AppTheme.colors.appWhite,
                style = AppTheme.typography.titleMedium(),
            )

            Icon(imageVector = vectorResource(AppIcons.empRoleIcon),
                tint = AppTheme.colors.appWhite,
                contentDescription = "",
                modifier = Modifier
                    .size(AppTheme.dimensions.smallIcon))
        }

    }
}

// Get the current time
@Composable
fun CurrentTimeDisplay() {

    val currentTime by remember { mutableStateOf(DateTimeUtils.getCurrentTime()) }
    //val showColon by remember { mutableStateOf(true) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            text = currentTime,
            style = AppTheme.typography.timerHeader()
        )
    }
}
