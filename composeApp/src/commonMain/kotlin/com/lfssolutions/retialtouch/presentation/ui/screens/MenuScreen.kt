package com.lfssolutions.retialtouch.presentation.ui.screens


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lfssolutions.retialtouch.navigation.NavigationItem
import com.lfssolutions.retialtouch.navigation.NavigationScreen
import com.lfssolutions.retialtouch.navigation.Navigator
import com.lfssolutions.retialtouch.presentation.ui.common.AppLeftSideMenu
import com.lfssolutions.retialtouch.utils.AppConstants


@Composable
fun MenuScreen(
    screen:String,
    memberId:Int=1,
    navigator: Navigator,
    onMenuItemClick: (NavigationScreen) -> Unit ={}
) {

    AppLeftSideMenu(
        modifier = Modifier.fillMaxSize(),
        onMenuItemClick = {
            navigator.handleNavigation(it)
        },
        content = { menuHeightPadding->
            when(screen){
                 AppConstants.POS_SCREEN -> {

                }
                AppConstants.PAYMENT_SCREEN -> {

                }
                AppConstants.MEMBER_SCREEN -> {

                }

            }
        },
        holdSaleContent = {

        }
    )
}

