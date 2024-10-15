package com.lfssolutions.retialtouch.navigation

import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.lfssolutions.retialtouch.utils.AppConstants.POS_SCREEN

class Navigator(private val navController: NavHostController) {


    fun navigateBack(){
        navController.navigate(NavigationScreen.Home(false)){popUpToTop()}
    }

    fun navigateToLogin() {
        navController.navigate(route = NavigationScreen.Login){
            popUpToTop()
        }
    }

    fun navigateToHome(isSplash: Boolean) {
        navController.navigate(NavigationScreen.Home(isSplash)){popUpToTop()}
    }

    fun navigateToMenuScreen(screen :String,memberId:Int=0) {
        navController.navigate(route =NavigationScreen.MenuScreen(screen,memberId))
    }

    fun navigateToPOS() {
        navController.navigate(route =NavigationScreen.POS)
    }

    fun navigateToPaymentType(memberId:Int,totalAmount:Double) {
        navController.navigate(route = NavigationScreen.PaymentType(memberId,totalAmount))
    }


    // Helper method to simplify popUpTo
    private fun NavOptionsBuilder.popUpToTop() {
        popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
            inclusive = true
        }
    }

    fun handleNavigation(route: String) {
        /*when(route){
            NavigationItem.Back->{
                navigateBack()
            }
        }*/
    }

    fun handleNavigation(route: NavigationScreen) {
        when(route){
             NavigationScreen.Category ->{
                navigateBack()
            }
            else->{

            }
        }

    }

}