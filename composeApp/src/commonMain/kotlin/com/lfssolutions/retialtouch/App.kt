package com.lfssolutions.retialtouch

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cafe.adriel.voyager.navigator.Navigator
import com.lfssolutions.retialtouch.navigation.AppNavHost
import com.lfssolutions.retialtouch.navigation.Route
import com.lfssolutions.retialtouch.navigation.toVoyagerScreen
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.HomeViewModel
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.lfssolutions.retialtouch.utils.LocalHomeViewModel
import com.lfssolutions.retialtouch.utils.LocalSharedViewModel
import com.outsidesource.oskitcompose.lib.rememberInjectForRoute
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject


@Composable
@Preview
fun App(baseViewModel: BaseViewModel = koinInject()
) {
    val appState by baseViewModel.composeAppState.collectAsStateWithLifecycle()

    AppTheme {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ){

            LaunchedEffect(maxWidth, maxHeight) {
                baseViewModel.updateScreenMode(maxWidth,maxHeight)
            }

            // Provide the appState globally using CompositionLocalProvider
            CompositionLocalProvider(LocalAppState provides appState) {
                Navigator(screen = Route.SplashScreen.toVoyagerScreen(),
                    onBackPressed = {
                        true
                    }
                )
            }
        }
    }
}