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
import com.lfssolutions.retialtouch.utils.LocalAppState
import kotlinx.coroutines.delay
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
       Home(homeViewModel=homeViewModel,isFromSplash = isSplash,onLogout={
           NavigatorActions.navigateToLoginScreen()
       })
    }
}

@Composable
fun Home(
    homeViewModel: HomeViewModel,
    isFromSplash:Boolean,
    onLogout: @Composable () -> Unit
    )
{
    val navigator = LocalNavigator.currentOrThrow
    var currentTime by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    var showColon by remember { mutableStateOf(true) }

    val homeUIState by homeViewModel.homeUIState.collectAsStateWithLifecycle()
    val appState = LocalAppState.current
    val syncInProgress by homeViewModel.syncInProgress.collectAsStateWithLifecycle()

    LaunchedEffect(isFromSplash) {
           println("callIsSplash :$isFromSplash")
        if (isFromSplash && !homeUIState.hasEmployeeLoggedIn) {
            homeViewModel.initialiseSplash(true)
        }
    }


    /*when(homeUIState.hasEmployeeLoggedIn){
        true -> {
            //homeViewModel.updateSyncRotation(5)
        }
        false -> {
            homeViewModel.initialiseSplash(isFromSplash)
        }
    }*/

    LaunchedEffect(syncInProgress){
        if(syncInProgress){
            //homeViewModel.onSyncClick()
        }
    }

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            delay(1000L) // Update every second
        }
    }

    // Blink effect for the colon every second
    LaunchedEffect(Unit) {
        while (true) {
            showColon = !showColon // Toggle colon visibility
            delay(1000L) // Blink every second
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
                            modifier = Modifier.fillMaxSize().padding(vertical = AppTheme.dimensions.verticalItemPadding),
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
                            ListGridItems(homeUIState.homeItemList,syncInProgress){id->
                                when(id){
                                    1->{ //Cashier
                                        NavigatorActions.navigateToPOSScreen(navigator)
                                    }
                                    5->{ //Sync
                                        homeViewModel.updateSyncRotation(id)
                                    }

                                    8->{
                                        NavigatorActions.navigateToPrinterScreen(navigator)
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