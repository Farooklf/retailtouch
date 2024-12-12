package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.lfssolutions.retialtouch.presentation.viewModels.PayoutViewModel
import org.koin.compose.koinInject


object PayoutScreen : Screen {
    @Composable
    override fun Content() {
        PayoutUI()
    }
}


@Composable
fun PayoutUI(
    viewModel: PayoutViewModel = koinInject()
){

}