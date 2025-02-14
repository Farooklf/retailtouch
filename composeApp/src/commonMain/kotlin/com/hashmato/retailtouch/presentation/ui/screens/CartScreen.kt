package com.hashmato.retailtouch.presentation.ui.screens


import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.hashmato.retailtouch.presentation.viewModels.SharedPosViewModel
import com.outsidesource.oskitcompose.lib.rememberValRef
import org.koin.compose.koinInject

object CartScreen: Screen {
    @Composable
    override fun Content() {
        CartUI()
    }
}

@Composable
fun CartUI(
    viewModel: SharedPosViewModel = koinInject()
) {
    CartView(interactorRef = rememberValRef(viewModel))
}