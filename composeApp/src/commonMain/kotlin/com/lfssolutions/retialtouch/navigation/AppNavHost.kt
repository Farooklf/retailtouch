package com.lfssolutions.retialtouch.navigation


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    val navigator = remember { Navigator(navController) }

    NavHost(
        navController = navController,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        startDestination = NavigationScreen.Splash
    ){
        composable<NavigationScreen.POS> {
            //PosScreen(navigator = navigator)
        }
    }

        // Splash screen
       /* composable<NavigationScreen.Splash>{
            Splash(onNavigateDashBoard = {
                navigator.navigateToHome(isSplash=true)
            }, onNavigateLogin = {
                navigator.navigateToLogin()
            })
        }

        composable<NavigationScreen.Login> {
            LoginScreen(onNavigateDashBoard = {
                navigator.navigateToHome(isSplash = true)
            })
        }

        composable<NavigationScreen.Home>{ backStackEntry ->
            val detail: NavigationScreen.Home = backStackEntry.toRoute()
            val isSplash = detail.isSplash
            HomeScreen(
                isFromSplash = isSplash,
                navigator = navigator
            )
        }

        composable<NavigationScreen.MenuScreen>{ backStackEntry ->
            val mMenuScreen: NavigationScreen.MenuScreen = backStackEntry.toRoute()
            val screen = mMenuScreen.screen
            MenuScreen(
                screen = screen,
                memberId = mMenuScreen.memberId,
                navigator = navigator
            )
        }


        composable<NavigationScreen.POS> {
            PosScreen(navigator = navigator)
        }

        composable<NavigationScreen.PaymentType>{ backStackEntry ->
            val mPaymentType: NavigationScreen.PaymentType = backStackEntry.toRoute()
            val memberId = mPaymentType.memberId
            val totalAmount = mPaymentType.totalAmount
            PaymentTypeScreen(navigator=navigator, memberId = memberId, totalAmount = totalAmount)
        }
    }*/

}

fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
        inclusive = true
    }
}
