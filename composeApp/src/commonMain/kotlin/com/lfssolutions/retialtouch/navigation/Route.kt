package com.lfssolutions.retialtouch.navigation

import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleRecord


sealed class Route {

    data object SplashScreen : Route()
    data object LoginScreen : Route()
    data class  HomeScreen(val isSplash : Boolean) : Route()
    data object POSScreen: Route()
    data object CartScreen: Route()
    data object PaymentType : Route()
    data object Printer : Route()
    data object Transaction : Route()
    data class TransactionDetails(val mSaleRecord: SaleRecord) : Route()
    data object Payout : Route()
    data object Settlement : Route()
    data object Setting : Route()
}