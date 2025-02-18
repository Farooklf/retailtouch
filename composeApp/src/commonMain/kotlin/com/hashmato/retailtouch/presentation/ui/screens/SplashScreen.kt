package com.hashmato.retailtouch.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.common.AppCircleProgressIndicator
import com.hashmato.retailtouch.presentation.common.SmallTextComponent
import com.hashmato.retailtouch.presentation.viewModels.BaseViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.app_logo

object SplashScreen:Screen{
    @Composable
    override fun Content() {
        val baseViewModel: BaseViewModel = koinInject()
        var isLoading by remember { mutableStateOf(true) }
        val isUserLoggedIn by baseViewModel.isUserLoggedIn.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.align(Alignment.Center)
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight().align(Alignment.BottomCenter),
            ) {
                SmallTextComponent(text = "RetailTouch", modifier = Modifier.wrapContentWidth())

            }

            AppCircleProgressIndicator(isVisible = isLoading)
        }

        when (isUserLoggedIn) {
            true -> {
                // If user is logged in, navigate to Dashboard
                isLoading = false
                NavigatorActions.navigateToHomeScreen(navigator  ,true)
            }

            false -> {
                // If user is not logged in, navigate to Login screen
                isLoading = false
                NavigatorActions.navigateToLoginScreen(navigator)
            }

            null -> {
                // While isUserLoggedIn is null, keep showing the loading indicator
                isLoading = true
            }
        }

    }

}


