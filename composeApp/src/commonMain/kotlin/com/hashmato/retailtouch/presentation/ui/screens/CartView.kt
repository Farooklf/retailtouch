package com.hashmato.retailtouch.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.ui.common.AppPrimaryButton
import com.hashmato.retailtouch.presentation.ui.common.BottomTex
import com.hashmato.retailtouch.presentation.ui.common.CartHeaderImageButton
import com.hashmato.retailtouch.presentation.ui.common.CartListItem
import com.hashmato.retailtouch.presentation.ui.common.CashierBasicScreen
import com.hashmato.retailtouch.presentation.ui.common.ListCenterText
import com.hashmato.retailtouch.presentation.ui.common.dialogs.ActionDialog
import com.hashmato.retailtouch.presentation.ui.common.dialogs.HoldSaleDialog
import com.hashmato.retailtouch.presentation.ui.common.dialogs.MemberListDialog
import com.hashmato.retailtouch.presentation.ui.common.dialogs.PromotionAndDiscountDialog
import com.hashmato.retailtouch.presentation.ui.common.dialogs.StockDialog
import com.hashmato.retailtouch.presentation.ui.common.dialogs.TicketDiscountDialog
import com.hashmato.retailtouch.presentation.viewModels.SharedPosViewModel
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.utils.formatPrice
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.lib.ValRef
import com.outsidesource.oskitcompose.lib.rememberValRef
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.clear_item_message
import retailtouch.composeapp.generated.resources.clear_scanned_message
import retailtouch.composeapp.generated.resources.discount
import retailtouch.composeapp.generated.resources.discount_str
import retailtouch.composeapp.generated.resources.discount_value
import retailtouch.composeapp.generated.resources.held_tickets
import retailtouch.composeapp.generated.resources.hold_sale
import retailtouch.composeapp.generated.resources.img_empty_cart
import retailtouch.composeapp.generated.resources.items
import retailtouch.composeapp.generated.resources.items_discount
import retailtouch.composeapp.generated.resources.no_products_added
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.price
import retailtouch.composeapp.generated.resources.qty
import retailtouch.composeapp.generated.resources.qty_value
import retailtouch.composeapp.generated.resources.retail_pos
import retailtouch.composeapp.generated.resources.review_cart
import retailtouch.composeapp.generated.resources.selected_member
import retailtouch.composeapp.generated.resources.sub_total
import retailtouch.composeapp.generated.resources.tax_value
import retailtouch.composeapp.generated.resources.total_value

@Composable
fun CartView(interactorRef: ValRef<SharedPosViewModel>) {
    val viewModel = interactorRef.value
    val state by viewModel.posUIState.collectAsStateWithLifecycle()
    val navigator = LocalNavigator.currentOrThrow
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }

    LaunchedEffect(state.cartList) {
        viewModel.recomputeSale()
    }

    LaunchedEffect(state) {
        viewModel.updateSecondDisplay()
    }

    LaunchedEffect(state.isError) {
        if (state.isError) {
            snackbarHostState.value.showSnackbar(state.errorMsg)
            delay(1000)
            viewModel.resetError()
        }
    }

    val (holdSaleText,icon) = if(state.cartList.isEmpty() && state.salesOnHold.isEmpty()){
        stringResource(Res.string.hold_sale) to AppIcons.pauseIcon
    }else if(state.cartList.isEmpty() &&  state.salesOnHold.isNotEmpty()){
        //val grandTotal = posUIState.salesOnHold.entries.sumOf { it.value.grandTotal }
        //val text = "#${posUIState.salesOnHold.size}."
        stringResource(Res.string.held_tickets) to null
    }else if(state.cartList.isNotEmpty() &&  state.salesOnHold.isNotEmpty()){
        stringResource(Res.string.hold_sale) to AppIcons.pauseIcon
    }else{
        stringResource(Res.string.hold_sale)  to AppIcons.pauseIcon
    }

    val holdSaleClickable= if(state.cartList.isEmpty() && state.salesOnHold.isEmpty()){
        false
    }else if(state.cartList.isNotEmpty() && state.salesOnHold.isEmpty()){
        true
    }else if(state.cartList.isEmpty() && state.salesOnHold.isNotEmpty()){
        true
    }else{
        true
    }

    val discountText=if(state.globalDiscount>0){
        stringResource(Res.string.discount_str, viewModel.getDiscountValue())
    }else{
        stringResource(Res.string.discount)
    }

    CashierBasicScreen(
        modifier = Modifier
            .systemBarsPadding(),
        isScrollable = false,
        contentMaxWidth = Int.MAX_VALUE.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(AppTheme.colors.secondaryBg)
                .padding(horizontal = AppTheme.dimensions.padding10, vertical = AppTheme.dimensions.padding10),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding10)
        ){

            Row(modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding15)
            ){

                Text(
                    text = stringResource(Res.string.review_cart),
                    modifier = Modifier
                        .weight(1f),
                    style = AppTheme.typography.header().copy(fontSize = 20.sp),
                    color = AppTheme.colors.primaryText
                )

                CartHeaderImageButton(
                    icon = AppIcons.percentageIcon,
                    isVisible = state.cartList.isNotEmpty(),
                    onClick = {
                        viewModel.updatePromotionDiscountDialog(true)
                    }
                )

                CartHeaderImageButton(
                    icon = AppIcons.addCustomer,
                    boxColor = AppTheme.colors.appGreen,
                    isVisible = state.cartList.isNotEmpty(),
                    onClick = {
                        viewModel.updateMemberDialogState(true)
                    }
                )

                CartHeaderImageButton(
                    icon = AppIcons.removeIcon,
                    boxColor = AppTheme.colors.appRed,
                    isVisible = state.cartList.isNotEmpty(),
                    onClick = {
                        viewModel.updateClearCartDialogVisibility(state.cartList.isNotEmpty())
                    }
                )
            }

            /*if(appState.isPortrait){
                Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically,horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding5)) {

                    //Top Search Bar
                    SearchableTextWithBg(
                        value = state.searchQuery,
                        leadingIcon = AppIcons.searchIcon,
                        placeholder = stringResource(Res.string.search_items),
                        label = stringResource(Res.string.search_items),
                        modifier = Modifier.weight(1f).wrapContentHeight(),
                        backgroundColor = AppTheme.colors.screenBackground,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // When done is pressed, open the dialog
                                viewModel.scanBarcode()
                            }
                        ),
                        onValueChange = {
                            viewModel.updateSearchQuery(it)
                        })
                }
            }*/

            val textStyleHeader= AppTheme.typography.captionBold()
            val btnStyle= AppTheme.typography.bodyMedium()

            //List UI Header
            Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = AppTheme.dimensions.padding20),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ){
                //ListCenterText(label = stringResource(Res.string.hash), textStyle = textStyleHeader, modifier = Modifier.weight(.2f))
                ListCenterText(label = stringResource(Res.string.items).uppercase(), textStyle = textStyleHeader, arrangement = Arrangement.Start,modifier = Modifier.weight(1f))
                ListCenterText(label = stringResource(Res.string.price).uppercase(),textStyle = textStyleHeader,arrangement = Arrangement.Start, modifier = Modifier.weight(1f))
                ListCenterText(label = stringResource(Res.string.qty).uppercase(), textStyle = textStyleHeader,arrangement = Arrangement.Start,modifier = Modifier.weight(1f))
                ListCenterText(label = stringResource(Res.string.sub_total).uppercase(),textStyle = textStyleHeader,arrangement = Arrangement.Start, modifier = Modifier.weight(1f))
            }

            if (state.cartList.isEmpty()) {
                EmptyCartForm(modifier = Modifier.weight(1f))
            }else{
                LazyColumn(modifier = Modifier.weight(1f)){
                    itemsIndexed(state.cartList
                    ){index, product ->
                        CartListItem(
                            index = index,
                            item = product,
                            isPortrait = true,
                            horizontalPadding= AppTheme.dimensions.padding10,
                            verticalPadding= AppTheme.dimensions.padding10,
                            posViewModel=viewModel
                        )
                    }
                }
            }

            //Bottom fix content
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(5.dp)){

                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spaceBetweenPadded(5.dp)) {
                    BottomTex(
                        label = stringResource(Res.string.qty_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey,
                        isPortrait = true,
                        modifier = Modifier.wrapContentWidth()
                    )

                    BottomTex(
                        label = "${state.quantityTotal}",
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey,
                        modifier = Modifier.wrapContentWidth()
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BottomTex(
                        label = stringResource(Res.string.sub_total,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey)

                    BottomTex(
                        label = viewModel.formatPriceForUI(state.cartSubTotal),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey)
                }

                if(state.cartItemTotalDiscounts>0.0 && state.cartList.isNotEmpty()){
                    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        BottomTex(
                            label = stringResource(Res.string.items_discount,":"),
                            textStyle = textStyleHeader,
                            color = AppTheme.colors.appRed)

                        BottomTex(
                            label = viewModel.formatPriceForUI(state.cartItemTotalDiscounts),
                            textStyle = textStyleHeader,
                            color = AppTheme.colors.appRed)
                    }
                }

                if(state.globalDiscount>0.0 && state.cartList.isNotEmpty()){
                    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        BottomTex(
                            label = stringResource(Res.string.discount_value,":"),
                            textStyle = textStyleHeader,
                            color = AppTheme.colors.appRed)

                        BottomTex(
                            label = viewModel.getDiscountValue()/*viewModel.getDiscountValue()*/, //state.cartItemsDiscount+state.cartPromotionDiscount
                            textStyle = textStyleHeader,
                            color = AppTheme.colors.appRed)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BottomTex(
                        label = stringResource(Res.string.tax_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey)

                    BottomTex(
                        label =formatPrice(state.globalTax,currencySymbol) /*viewModel.formatPriceForUI(state.globalTax)*/,
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey)
                }

                if(state.selectedMemberId>0 && state.cartList.isNotEmpty()){
                    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        BottomTex(
                            label = stringResource(Res.string.selected_member,":"),
                            textStyle = textStyleHeader,
                            color = AppTheme.colors.appGreen)

                        BottomTex(
                            label = state.selectedMember,
                            textStyle = textStyleHeader,
                            color = AppTheme.colors.appGreen)
                    }
                }

                //Total Value

                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = AppTheme.dimensions.padding10),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                    BottomTex(
                        label = stringResource(Res.string.total_value,":"),
                        textStyle = AppTheme.typography.h1Bold().copy(fontSize = 24.sp),
                        color = AppTheme.colors.textPrimary
                    )

                    BottomTex(
                        label =formatPrice(state.grandTotal,currencySymbol) /*viewModel.formatPriceForUI(state.grandTotal)*/,
                        textStyle = AppTheme.typography.h1Bold().copy(fontSize = 24.sp),
                        color = AppTheme.colors.textPrimary)
                }

                Row(modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    //Hold Button
                    AppPrimaryButton(
                        enabled = holdSaleClickable,
                        label = holdSaleText,
                        leftIcon = icon,
                        backgroundColor = AppTheme.colors.appRed,
                        disabledBackgroundColor = AppTheme.colors.appRed,
                        style = btnStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                            if(state.cartList.isNotEmpty())
                                viewModel.holdCurrentSale()
                            else if(state.cartList.isEmpty() && state.salesOnHold.isNotEmpty())
                                viewModel.updateHoldSalePopupState(true)

                        }
                    )

                    //Discount Button
                    AppPrimaryButton(
                        enabled = state.cartList.isNotEmpty(),
                        label = stringResource(Res.string.discount),
                        leftIcon = AppIcons.discountIcon,
                        backgroundColor = AppTheme.colors.appRed,
                        disabledBackgroundColor = AppTheme.colors.appRed,
                        style = btnStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                            //posViewModel.onTotalDiscountItemClick()
                            viewModel.onGlobalDiscountClick()
                        }
                    )
                    
                    //Payment Button
                    AppPrimaryButton(
                        enabled = state.cartList.isNotEmpty(),
                        label = stringResource(Res.string.payment),
                        leftIcon = AppIcons.paymentIcon,
                        backgroundColor = AppTheme.colors.appGreen,
                        disabledBackgroundColor = AppTheme.colors.appGreen,
                        style = btnStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                            NavigatorActions.navigateToPaymentScreen(navigator)
                        }
                    )

                }
            }
        }
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

    MemberListDialog(
        isVisible = state.isMemberDialog,
        interactorRef = rememberValRef(viewModel),
        onDismissRequest = {
            viewModel.updateMemberDialogState(false)
        })

    //Discount Content
    PromotionAndDiscountDialog(
        isVisible = state.showPromotionDiscountDialog,
        promotions=state.promotions,
        isPortrait=true,
        onDismiss = {
            viewModel.updatePromotionDiscountDialog(false)
        },
        onItemClick = {promotion->
            viewModel.updatePromotionDiscountDialog(false)
            viewModel.applyPromotionDiscounts(promotion)
        },
        onClearPromotionClick = {
            viewModel.updatePromotionDiscountDialog(false)
            viewModel.clearAppliedPromotions()
        }
    )

    ActionDialog(
        isVisible = state.showItemRemoveDialog,
        dialogTitle = stringResource(Res.string.retail_pos),
        dialogMessage = stringResource(Res.string.clear_item_message),
        onDismissRequest = {
            viewModel.updateItemRemoveDialogState(false)
        },
        onCancel = {
            viewModel.updateItemRemoveDialogState(false)
        },
        onConfirm = {
            viewModel.updateItemRemoveDialogState(false)
            viewModel.removedListItem()
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

    //Cart Item Discount Content
    TicketDiscountDialog(
        isVisible = state.showDiscountDialog,
        inputValue = state.itemDiscount,
        inputError = state.inputDiscountError,
        trailingIcon= viewModel.getDiscountTypeIcon(),
        selectedDiscountType = state.selectedDiscountType,
        onDismissRequest = {
            viewModel.resetDiscountDialog()
        },
        onTabClick = {discountType->
            viewModel.updateDiscountType(discountType)
        },
        onDiscountChange = { discount->
            viewModel.updateDiscountValue(discount)
        },
        onApply = {
            viewModel.resetDiscountDialog()
            viewModel.onApplyDiscountClick()
        },
        onCancel = {
            viewModel.resetDiscountDialog()
        },
        onNumberPadClick = {symbol->
            viewModel.onNumberPadClick(symbol)
        },
        onClearDiscountClick = {
           viewModel.clearAppliedItemDiscounts()
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

@Composable
private fun EmptyCartForm(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.img_empty_cart),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = stringResource(Res.string.no_products_added),
                style = AppTheme.typography.h1Bold(),
                color = AppTheme.colors.secondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}