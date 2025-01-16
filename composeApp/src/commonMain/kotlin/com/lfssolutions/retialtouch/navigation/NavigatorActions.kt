package com.lfssolutions.retialtouch.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleRecord


object NavigatorActions {


    fun navigateBack(navigator: Navigator) {
        navigator.pop()
    }

    fun navigateBackToHomeScreen(navigator: Navigator,isSplash: Boolean) {
        navigator.popUntilRoot() // Clear the back stack
        navigator.replace(Route.HomeScreen(isSplash).toVoyagerScreen())
    }

    @Composable
    fun navigateToLoginScreen(navigator: Navigator) {
        navigator.popUntilRoot() // Clear the back stack
        navigator.replace(Route.LoginScreen.toVoyagerScreen()) // Replace with Login screen
    }

    fun navigateToHomeScreen(navigator: Navigator,isSplash : Boolean) {
        navigator.popUntilRoot()
        navigator.push(Route.HomeScreen(isSplash).toVoyagerScreen())
    }


    fun navigateToPOSScreen(navigator: Navigator) {
        navigator.push(Route.CashierScreen.toVoyagerScreen())
    }

    fun backToCashierScreen(navigator: Navigator) {
        navigator.popUntilRoot()
        navigator.replace(Route.CashierScreen.toVoyagerScreen())
    }

    fun navigateToCartScreen(navigator: Navigator) {
        navigator.push(Route.CartScreen.toVoyagerScreen())
    }

    fun navigateToPaymentScreen(navigator: Navigator) {
        navigator.push(Route.PaymentType.toVoyagerScreen())
    }

    fun navigateToPrinterScreen(navigator: Navigator) {
        navigator.push(Route.Printer.toVoyagerScreen())
    }

    fun navigateToTransactionScreen(navigator: Navigator) {
        navigator.push(Route.Transaction.toVoyagerScreen())
    }

    fun navigateToTransactionDetailsScreen(navigator: Navigator, mSaleRecord: SaleRecord) {
        navigator.push(Route.TransactionDetails(mSaleRecord).toVoyagerScreen())
    }


    fun navigateToSettlementScreen(navigator: Navigator) {
        navigator.push(Route.Settlement.toVoyagerScreen())
    }

    fun navigateToSettingScreen(navigator: Navigator) {
        navigator.push(Route.Setting.toVoyagerScreen())
    }
}