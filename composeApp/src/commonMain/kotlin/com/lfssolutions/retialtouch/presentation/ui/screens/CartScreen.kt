package com.lfssolutions.retialtouch.presentation.ui.screens


import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.ActionDialog
import com.lfssolutions.retialtouch.presentation.ui.common.CashierBasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.DiscountDialog
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.HoldSaleDialog
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.ItemDiscountDialog
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.MemberListDialog
import com.lfssolutions.retialtouch.presentation.ui.common.dialogs.StockDialog
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.outsidesource.oskitcompose.lib.rememberValRef
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.clear_scanned_message
import retailtouch.composeapp.generated.resources.retail_pos

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

    /*CashierBasicScreen(
        modifier = Modifier
            .systemBarsPadding(),
        isScrollable = false,
        contentMaxWidth = Int.MAX_VALUE.dp
    ) {
        CartView(interactorRef = rememberValRef(viewModel))
        SnackbarHost(
            hostState = snackbarHostState.value,
            modifier = Modifier
                .align(Alignment.TopCenter)

        )
    }*/

}