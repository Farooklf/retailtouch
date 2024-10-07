package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.GradientBackgroundScreen
import com.lfssolutions.retialtouch.presentation.ui.common.HomeItemGrid
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.presentation.viewModels.HomeViewModel
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
    onLogout: @Composable () -> Unit,
    )
{
    val navigator = LocalNavigator.currentOrThrow
    var currentTime by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) }
    var showColon by remember { mutableStateOf(true) }

    val homeUIState by homeViewModel.homeUIState.collectAsStateWithLifecycle()

    println("hasEmployeeLoggedIn : ${homeUIState.hasEmployeeLoggedIn}")

    when(homeUIState.hasEmployeeLoggedIn){
        true -> {
            homeViewModel.updateSyncRotation(5)
        }
        false -> {
            homeViewModel.initialiseSplash(isFromSplash)
        }
    }

    LaunchedEffect(homeUIState.isSync){
        if(homeUIState.isSync){
            homeViewModel.onSyncClick()
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
            Column(
                modifier = Modifier
                    .fillMaxSize() // Ensures bounded height()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Top section with time and status
                TopSection(currentTime, showColon,homeUIState.authUser)



                HomeItemGrid(homeUIState){id->

                    //onListItem.invoke(id)
                    when(id){
                        1->{ //Cashier
                            NavigatorActions.navigateToPOSScreen(navigator)
                        }
                        5->{ //Sync
                            homeViewModel.updateSyncRotation(id)
                        }
                    }
                }

            }
        }

        if (homeUIState.isFromSplash && !homeUIState.hasEmployeeLoggedIn) {
            EmployeeScreen(
                onNavigateLogout = {
                    onLogout.invoke()
                   //navigator.navigateToLogin()
                },
                onDismiss = {
                    homeViewModel.onEmployeeLoggedIn() }
            )
        }
    }

}





@Composable
fun TopSection(currentTime: LocalDateTime, showColon: Boolean, user: AuthenticateDao) {

    //Location Row
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(imageVector = vectorResource(AppIcons.locationIcon),
            tint = AppTheme.colors.textWhite,
            contentDescription = "",
            modifier = Modifier
                .size(50.dp)
                .padding(4.dp))

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "${user.loginDao.defaultLocation} | ${user.loginDao.locationCode}",
            color = AppTheme.colors.textWhite,
            style = AppTheme.typography.titleMedium(),
        )

    }

    Spacer(modifier = Modifier.height(25.dp))

    //Employee
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.loginDao.userName.uppercase(),
            color = AppTheme.colors.textWhite,
            style = AppTheme.typography.titleMedium(),
        )

        Icon(imageVector = vectorResource(AppIcons.empRoleIcon),
            tint = AppTheme.colors.textWhite,
            contentDescription = "",
            modifier = Modifier
                .size(50.dp)
                .padding(horizontal = 10.dp))

    }

    Spacer(modifier = Modifier.height(25.dp))


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        CurrentTimeDisplay(currentTime, showColon)

        Text(text = stringResource(Res.string.home_header),
            style = AppTheme.typography.titleBold(),
            color = AppTheme.colors.textWhite
        )
    }

    Spacer(modifier = Modifier.height(25.dp))
}

// Get the current time
@Composable
fun CurrentTimeDisplay(currentTime: LocalDateTime, showColon: Boolean) {

    // Format hours and minutes
    val hours = currentTime.hour.toString().padStart(2, '0')
    val minutes = currentTime.minute.toString().padStart(2, '0')

    Text(
        text = if (showColon) "$hours : $minutes" else "$hours : $minutes",
        style = AppTheme.typography.timerHeader()
    )
}
