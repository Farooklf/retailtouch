package com.hashmato.retailtouch.navigation

import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.hashmato.retailtouch.domain.model.login.RTLoginUser


sealed class Route {

    data object SplashScreen : Route()
    data class LoginScreen(val mRTUser: RTLoginUser?) : Route()
    data class  HomeScreen(val isSplash : Boolean) : Route()
    data object CashierScreen: Route()
    data object CartScreen: Route()
    data object PaymentType : Route()
    data object Printer : Route()
    data object Transaction : Route()
    data class TransactionDetails(val mSaleRecord: SaleRecord) : Route()
    data object Payout : Route()
    data object Settlement : Route()
    data object Setting : Route()
    data object Stock : Route()
    data object Member : Route()
}