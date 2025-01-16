package com.lfssolutions.retialtouch.presentation.ui.screens

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.navigation.NavigatorActions.navigateToSettlementScreen
import com.lfssolutions.retialtouch.presentation.ui.common.AppScreenPadding
import com.lfssolutions.retialtouch.presentation.ui.common.GradientBackgroundScreen
import com.lfssolutions.retialtouch.presentation.ui.common.ListGridItems
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.ActionDialog
import com.lfssolutions.retialtouch.presentation.ui.common.getGridCell
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.presentation.viewModels.HomeViewModel
import com.lfssolutions.retialtouch.sync.SyncViewModel
import com.lfssolutions.retialtouch.utils.DateTimeUtils
import com.lfssolutions.retialtouch.utils.HomeItemId
import com.lfssolutions.retialtouch.utils.exitApp
import com.outsidesource.oskitcompose.router.KMPBackHandler
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.exit_app_message
import retailtouch.composeapp.generated.resources.home_header
import retailtouch.composeapp.generated.resources.retail_pos


data class HomeScreen(val isSplash: Boolean): Screen{
    @Composable
    override fun Content() {
       val homeViewModel: HomeViewModel = koinInject()
        val navigator = LocalNavigator.currentOrThrow
       Home(homeViewModel=homeViewModel,isFromSplash = isSplash,onLogout={
           NavigatorActions.navigateToLoginScreen(navigator)
       })
    }
}

@Composable
fun Home(
    homeViewModel: HomeViewModel,
    syncViewModel: SyncViewModel= koinInject(),
    isFromSplash:Boolean,
    onLogout: @Composable () -> Unit
    )
{
    val appThemeContext = AppTheme.context
    val navigator=appThemeContext.getAppNavigator()
    val homeUIState by homeViewModel.homeUIState.collectAsStateWithLifecycle()
    //val appState = LocalAppState.current
    val syncDataState by syncViewModel.syncDataState.collectAsStateWithLifecycle()

    KMPBackHandler(true, onBack = {
        homeViewModel.updateExitFormDialogState(true)
    })

    LaunchedEffect(Unit){
        println("${homeViewModel.isCallCompleteSync()}")
        syncViewModel.reSync(completeSync = homeViewModel.isCallCompleteSync())
    }


    LaunchedEffect(isFromSplash) {
        if (isFromSplash && !homeUIState.hasEmployeeLoggedIn) {
            homeViewModel.prepareHomeData()
            homeViewModel.initialiseEmpScreen(isFromSplash)
        }
    }


    LaunchedEffect(syncDataState.syncInProgress){
        if(!syncDataState.syncInProgress){
           homeViewModel.stopSyncRotation(syncDataState.syncInProgress)
        }
    }


    Box(modifier = Modifier.fillMaxSize()){
        GradientBackgroundScreen(
            modifier = Modifier.fillMaxSize(),
            isBlur = homeUIState.isBlur
        ){
            // The main screen
            AppScreenPadding(
                content = {horizontalPadding, verticalPadding ->
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
                            homeUIState.authUser)


                        LazyVerticalGrid(
                            columns = GridCells.Fixed( getGridCell(appThemeContext.deviceType)),
                            modifier = Modifier.fillMaxSize().padding(vertical = AppTheme.dimensions.padding10),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement=Arrangement.SpaceEvenly
                            ) {
                             item(span = { GridItemSpan(getGridCell(appThemeContext.deviceType)) }) {
                                 CurrentTimeDisplay()
                             }
                             item(span = { GridItemSpan(getGridCell(appThemeContext.deviceType))}) {
                                Text(text = stringResource(Res.string.home_header),
                                    style = AppTheme.typography.titleBold(),
                                    color = AppTheme.colors.appWhite,
                                    textAlign = TextAlign.Center
                                )
                            }
                            ListGridItems(homeUIState.homeItemList,syncDataState.syncInProgress){id->
                                when(id){
                                    HomeItemId.CASHIER_ID->{ //Cashier
                                        NavigatorActions.navigateToPOSScreen(navigator)
                                    }
                                    HomeItemId.MEMBER_ID->{

                                    }
                                    HomeItemId.STOCK_ID->{

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
                                        syncViewModel.reSync(true)
                                    }
                                    HomeItemId.PRINTER_ID->{
                                        NavigatorActions.navigateToPrinterScreen(navigator)
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
                    }
                }
            )
        }

        if (homeUIState.isFromSplash && !homeUIState.hasEmployeeLoggedIn) {
            EmployeeScreen(
                onNavigateLogout = {
                    onLogout.invoke()
                },
                onDismiss = {
                    homeViewModel.onEmployeeLoggedIn() }
            )
        }
    }

    ActionDialog(
        isVisible = homeUIState.showExitConfirmationDialog,
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
