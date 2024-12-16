package com.lfssolutions.retialtouch.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.lfssolutions.retialtouch.presentation.ui.screens.CashierScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.HomeScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.LoginScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.PaymentTypeScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.PayoutScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.PosScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.PrinterScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.SplashScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.TransactionDetailsScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.TransactionScreen

fun Route.toVoyagerScreen(): Screen = when (this) {
    is Route.SplashScreen -> SplashScreen
    is Route.HomeScreen -> HomeScreen(isSplash)
    is Route.LoginScreen -> LoginScreen
    is Route.POSScreen -> CashierScreen
    is Route.PaymentType -> PaymentTypeScreen
    is Route.Printer -> PrinterScreen
    is Route.Transaction -> TransactionScreen
    is Route.TransactionDetails-> TransactionDetailsScreen(mSaleRecord)
    is Route.Payout->PayoutScreen
}