package com.lfssolutions.retialtouch.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState


object NavigatorActions {


    fun navigateBack(navigator: Navigator) {
        navigator.pop()
    }
    @Composable
    fun navigateToLoginScreen() {
        val navigator = LocalNavigator.currentOrThrow
        navigator.push(Route.LoginScreen.toVoyagerScreen())
    }

    @Composable
    fun navigateToHomeScreen(isSplash : Boolean) {
        val navigator = LocalNavigator.currentOrThrow
        navigator.push(Route.HomeScreen(isSplash).toVoyagerScreen())
    }


    fun navigateToPOSScreen(navigator: Navigator) {
        navigator.push(Route.POSScreen.toVoyagerScreen())
    }


    fun navigateToPaymentScreen(navigator: Navigator/*,memberId: Int, totalAmount:Double,mPosUIState: PosUIState*/) {
        navigator.push(Route.PaymentType.toVoyagerScreen())
    }
}