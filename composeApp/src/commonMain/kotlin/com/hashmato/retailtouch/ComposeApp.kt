package com.hashmato.retailtouch

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import com.hashmato.retailtouch.navigation.Route
import com.hashmato.retailtouch.navigation.toVoyagerScreen
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.presentation.viewModels.BaseViewModel
import com.hashmato.retailtouch.utils.LocalAppState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

object ComposeApp : KoinComponent{
    @Composable
    @Preview
    fun RootContent(baseViewModel: BaseViewModel = koinInject()
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
}
