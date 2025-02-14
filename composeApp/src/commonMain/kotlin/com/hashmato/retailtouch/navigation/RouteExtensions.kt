package com.hashmato.retailtouch.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.hashmato.retailtouch.presentation.ui.members.MemberScreen
import com.hashmato.retailtouch.presentation.ui.payout.PayoutScreen
import com.hashmato.retailtouch.presentation.ui.screens.CartScreen
import com.hashmato.retailtouch.presentation.ui.screens.CashierScreen
import com.hashmato.retailtouch.presentation.ui.screens.HomeScreen
import com.hashmato.retailtouch.presentation.ui.screens.LoginScreen
import com.hashmato.retailtouch.presentation.ui.screens.PaymentTypeScreen
import com.hashmato.retailtouch.presentation.ui.screens.PrinterScreen
import com.hashmato.retailtouch.presentation.ui.settings.SettingScreen
import com.hashmato.retailtouch.presentation.ui.screens.SettlementScreen
import com.hashmato.retailtouch.presentation.ui.screens.SplashScreen
import com.hashmato.retailtouch.presentation.ui.screens.TransactionDetailsScreen
import com.hashmato.retailtouch.presentation.ui.screens.TransactionScreen
import com.hashmato.retailtouch.presentation.ui.stocks.StockScreen

fun Route.toVoyagerScreen(): Screen = when (this) {
    is Route.SplashScreen -> SplashScreen
    is Route.HomeScreen -> HomeScreen(isSplash)
    is Route.LoginScreen -> LoginScreen
    is Route.CashierScreen -> CashierScreen
    is Route.CartScreen -> CartScreen
    is Route.PaymentType -> PaymentTypeScreen
    is Route.Printer -> PrinterScreen
    is Route.Transaction -> TransactionScreen
    is Route.TransactionDetails-> TransactionDetailsScreen(mSaleRecord)
    is Route.Payout-> PayoutScreen
    is Route.Settlement-> SettlementScreen
    is Route.Setting-> SettingScreen
    is Route.Stock-> StockScreen
    is Route.Member-> MemberScreen
}