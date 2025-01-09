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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.lfssolutions.retialtouch.presentation.ui.common.AppScreenPadding
import com.lfssolutions.retialtouch.presentation.ui.common.GradientBackgroundScreen
import com.lfssolutions.retialtouch.presentation.ui.common.ListGridItems
import com.lfssolutions.retialtouch.presentation.ui.common.getGridCell
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.presentation.viewModels.HomeViewModel
import com.lfssolutions.retialtouch.sync.SyncViewModel
import com.lfssolutions.retialtouch.utils.HomeItemId
import com.lfssolutions.retialtouch.utils.LocalAppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.home_header


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

    val navigator = LocalNavigator.currentOrThrow
    val currentTime by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    val showColon by remember { mutableStateOf(true) }

    val homeUIState by homeViewModel.homeUIState.collectAsStateWithLifecycle()
    val appState = LocalAppState.current
    val syncDataState by syncViewModel.syncDataState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        println("${homeViewModel.isCallCompleteSync()}")
        syncViewModel.reSync(completeSync = homeViewModel.isCallCompleteSync())
    }

    /*DisposableEffect(Unit) {
        //println("${homeViewModel.isCallCompleteSync()}")
        // Start the periodic timer
        //syncViewModel.startPeriodicSync()
        syncViewModel.reSync(completeSync = true)
        onDispose {
            // Stop the timer when the composable is removed from the composition
            syncViewModel.stopPeriodicSync()
        }
    }*/

    LaunchedEffect(isFromSplash) {
        if (isFromSplash && !homeUIState.hasEmployeeLoggedIn) {
            homeViewModel.prepareHomeData()
            homeViewModel.initialiseEmpScreen(isFromSplash)
        }
    }

    /*LaunchedEffect(homeUIState.hasEmployeeLoggedIn) {
        if (homeUIState.hasEmployeeLoggedIn) {
            homeViewModel.prepareHomeData()
        }
    }*/


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
                            columns = GridCells.Fixed( getGridCell(appState)),
                            modifier = Modifier.fillMaxSize().padding(vertical = AppTheme.dimensions.padding10),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement=Arrangement.SpaceEvenly
                            ) {
                             item(span = { GridItemSpan(getGridCell(appState)) }) {
                                 CurrentTimeDisplay(currentTime, showColon)
                             }
                             item(span = { GridItemSpan(getGridCell(appState))}) {
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
                                        NavigatorActions.navigateToSettlementScreen(navigator)
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
fun CurrentTimeDisplay(currentTime: LocalDateTime, showColon: Boolean) {

    // Format hours and minutes
    val hours = currentTime.hour.toString().padStart(2, '0')
    val minutes = currentTime.minute.toString().padStart(2, '0')

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            text = if (showColon) "$hours : $minutes" else "$hours : $minutes",
            style = AppTheme.typography.timerHeader()
        )
    }

}
