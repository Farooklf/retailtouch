package com.lfssolutions.retialtouch.navigation

import kotlinx.serialization.Serializable





@Serializable
sealed class NavigationItem{
    @Serializable
    data object Splash : NavigationItem()
    @Serializable
    data object Login : NavigationItem()
    @Serializable
    internal data class Home(val isSplash : Boolean) : NavigationItem()
    @Serializable
    data object POS : NavigationItem()
    @Serializable
    data object PaymentType : NavigationItem()
    @Serializable
    data object Back : NavigationItem()

}

@Serializable
sealed class NavigationScreen{
    @Serializable
    data object Splash : NavigationScreen()
    @Serializable
    data object Login : NavigationScreen()
    @Serializable
    data class Home(val isSplash : Boolean) : NavigationScreen()
    @Serializable
    data object POS : NavigationScreen()
    @Serializable
    data class MenuScreen(val screen: String, val memberId:Int) : NavigationScreen()
    @Serializable
    data class PaymentType(val memberId: Int, val totalAmount:Double) : NavigationScreen()
    @Serializable
    data object Category : NavigationScreen()
}
