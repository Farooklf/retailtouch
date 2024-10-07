package com.lfssolutions.retialtouch.navigation

import com.lfssolutions.retialtouch.domain.model.members.MemberItem


sealed class Route {

    data object SplashScreen : Route()
    data object LoginScreen : Route()
    data class HomeScreen(val isSplash : Boolean) : Route()
    data object POSScreen: Route()
    data class PaymentType(val memberId: Int, val totalAmount:Double): Route()
    data class TicketDetailsScreen(val ticket: MemberItem) : Route()
}