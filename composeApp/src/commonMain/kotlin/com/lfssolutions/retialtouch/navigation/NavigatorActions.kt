package com.lfssolutions.retialtouch.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow


object NavigatorActions {


    fun navigateBack(navigator: Navigator) {
        navigator.pop()
    }

    fun navigateBackToHomeScreen(navigator: Navigator,isSplash: Boolean) {
        navigator.push(Route.HomeScreen(isSplash).toVoyagerScreen())
    }

    @Composable
    fun navigateToLoginScreen() {
        val navigator = LocalNavigator.currentOrThrow
        navigator.push(Route.LoginScreen.toVoyagerScreen())
    }

    @Composable
    fun navigateToHomeScreen(isSplash : Boolean) {
        val navigator = LocalNavigator.currentOrThrow
        navigator.replace(Route.HomeScreen(isSplash).toVoyagerScreen())
    }


    fun navigateToPOSScreen(navigator: Navigator) {
        navigator.push(Route.POSScreen.toVoyagerScreen())
    }


    fun navigateToPaymentScreen(navigator: Navigator) {
        navigator.push(Route.PaymentType.toVoyagerScreen())
    }

    fun navigateToPrinterScreen(navigator: Navigator) {
        navigator.push(Route.Printer.toVoyagerScreen())
    }
}