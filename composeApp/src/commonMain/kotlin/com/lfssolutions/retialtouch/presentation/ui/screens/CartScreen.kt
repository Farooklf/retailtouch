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

    val state by viewModel.posUIState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }


    LaunchedEffect(state.isError) {
        if (state.isError) {
            snackbarHostState.value.showSnackbar(state.errorMsg)
            delay(2000)
            viewModel.dismissErrorDialog()
        }
    }

    LaunchedEffect(state.cartList) {
        viewModel.recomputeSale()
    }

    CashierBasicScreen(
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
    }

    StockDialog(
        isVisible = state.showDialog,
        interactorRef = rememberValRef(viewModel),
        onDismiss = {
            viewModel.updateDialogState(false)
        },
        onItemClick = {selectedItem->
            viewModel.updateDialogState(false)
            viewModel.clearSearch()
            viewModel.addSearchProduct(selectedItem)
        }
    )

    ActionDialog(
        isVisible = state.isRemoveDialog,
        dialogTitle = stringResource(Res.string.retail_pos),
        dialogMessage = stringResource(Res.string.clear_scanned_message),
        onDismissRequest = {
            viewModel.updateClearCartDialogVisibility(false)
        },
        onCancel = {
            viewModel.updateClearCartDialogVisibility(false)
        },
        onConfirm = {
            viewModel.removedScannedItem()
        }
    )

    MemberListDialog(
        isVisible = state.isMemberDialog,
        interactorRef = rememberValRef(viewModel),
        onDismissRequest = {
            viewModel.updateMemberDialogState(false)
        })

    //Discount Content
    DiscountDialog(
        isVisible = state.showDiscountDialog,
        promotions=state.promotions,
        isPortrait=true,
        onDismiss = {
            viewModel.updateDiscountDialog(false)
        },
        onItemClick = {promotion->
            viewModel.updateDiscountDialog(false)
            viewModel.updateDiscount(promotion)
        }
    )

    //Cart Item Discount Content
    ItemDiscountDialog(
        isVisible = state.showItemDiscountDialog,
        inputValue = state.inputDiscount,
        inputError = state.inputDiscountError,
        trailingIcon= viewModel.getDiscountTypeIcon(),
        isPortrait=true,
        selectedDiscountType = state.selectedDiscountType,
        onDismissRequest = {
            viewModel.dismissDiscountDialog()
        },
        onTabClick = {discountType->
            viewModel.updateDiscountType(discountType)
        },
        onDiscountChange = { discount->
            viewModel.updateDiscountValue(discount)
        },
        onApply = {
            viewModel.onApplyDiscountClick()
        },
        onCancel = {
            viewModel.dismissDiscountDialog()
        },
        onNumberPadClick = {symbol->
            viewModel.onNumberPadClick(symbol)
        }
    )


    HoldSaleDialog(
        posState=state,
        isVisible = state.showHoldSalePopup,
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
        onDismiss = {
            viewModel.updateHoldSalePopupState(false)
        },
        onRemove = { id->
            viewModel.removeHoldSale(id)
        },
        onItemClick = {collection->
            viewModel.reCallHoldSale(collection)
            if(state.salesOnHold.isEmpty()){
                viewModel.updateHoldSalePopupState(false)
            }
        }
    )
}