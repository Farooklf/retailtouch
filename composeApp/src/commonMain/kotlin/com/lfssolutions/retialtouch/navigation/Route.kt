package com.lfssolutions.retialtouch.navigation




sealed class Route {

    data object SplashScreen : Route()
    data object LoginScreen : Route()
    data class  HomeScreen(val isSplash : Boolean) : Route()
    data object POSScreen: Route()
    data object PaymentType : Route()
    data object Printer : Route()
}