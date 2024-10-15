package com.lfssolutions.retialtouch.navigation

import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState


sealed class Route {

    data object SplashScreen : Route()
    data object LoginScreen : Route()
    data class HomeScreen(val isSplash : Boolean) : Route()
    data object POSScreen: Route()
    data object PaymentType/*(*//*val memberId: Int, val totalAmount:Double,val mPosUIState: PosUIState*//*)*/ :
        Route()
    data class TicketDetailsScreen(val ticket: MemberItem) : Route()
}