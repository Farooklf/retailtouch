package com.lfssolutions.retialtouch.presentation.ui.screens

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.AppPrimaryButton
import com.lfssolutions.retialtouch.presentation.ui.common.BottomTex
import com.lfssolutions.retialtouch.presentation.ui.common.CartHeaderImageButton
import com.lfssolutions.retialtouch.presentation.ui.common.CartListItem
import com.lfssolutions.retialtouch.presentation.ui.common.ListCenterText
import com.lfssolutions.retialtouch.presentation.ui.common.SearchableTextWithBg
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.lib.ValRef
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.discount_value
import retailtouch.composeapp.generated.resources.held_tickets
import retailtouch.composeapp.generated.resources.hold_sale
import retailtouch.composeapp.generated.resources.img_empty_cart
import retailtouch.composeapp.generated.resources.items
import retailtouch.composeapp.generated.resources.no_products_added
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.price
import retailtouch.composeapp.generated.resources.qty
import retailtouch.composeapp.generated.resources.qty_value
import retailtouch.composeapp.generated.resources.review_cart
import retailtouch.composeapp.generated.resources.search_items
import retailtouch.composeapp.generated.resources.selected_member
import retailtouch.composeapp.generated.resources.sub_total
import retailtouch.composeapp.generated.resources.tax_value
import retailtouch.composeapp.generated.resources.total_value

@Composable
fun CartView(interactorRef: ValRef<SharedPosViewModel>) {
    val viewModel = interactorRef.value
    val state by viewModel.posUIState.collectAsStateWithLifecycle()
    val appState = LocalAppState.current
    val navigator = LocalNavigator.currentOrThrow

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
                    viewModel.updateDiscountDialog(true)
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

        if(appState.isPortrait){
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
        }
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



            if(state.globalDiscount>0){
                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BottomTex(
                        label = stringResource(Res.string.discount_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.appRed)

                    BottomTex(
                        label = viewModel.getDiscountValue(), //state.cartItemsDiscount+state.cartPromotionDiscount
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
                    label = viewModel.formatPriceForUI(state.globalTax),
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
                    label = viewModel.formatPriceForUI(state.grandTotal),
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