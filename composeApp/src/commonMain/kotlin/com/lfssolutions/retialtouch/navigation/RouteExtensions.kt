package com.lfssolutions.retialtouch.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.lfssolutions.retialtouch.presentation.ui.screens.HomeScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.LoginScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.PaymentTypeScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.PosScreen
import com.lfssolutions.retialtouch.presentation.ui.screens.SplashScreen

fun Route.toVoyagerScreen(): Screen = when (this) {
    is Route.SplashScreen -> SplashScreen
    is Route.HomeScreen -> HomeScreen(isSplash)
    is Route.LoginScreen -> LoginScreen
     is Route.POSScreen -> PosScreen
    is Route.PaymentType -> PaymentTypeScreen(memberId,totalAmount)
    else->{
        LoginScreen
    }

}