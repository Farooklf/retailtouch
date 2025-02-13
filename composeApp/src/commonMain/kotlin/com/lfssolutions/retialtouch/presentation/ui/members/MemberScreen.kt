package com.lfssolutions.retialtouch.presentation.ui.members

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.lfssolutions.retialtouch.presentation.ui.stocks.StockScreenContent

class MemberScreen : Screen {

    @Composable
    override fun Content() {
        StockScreenContent()
    }
}