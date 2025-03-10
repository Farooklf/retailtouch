package com.hashmato.retailtouch.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.domain.model.products.CRSaleOnHold
import com.hashmato.retailtouch.domain.model.products.CartItem
import com.hashmato.retailtouch.domain.model.products.POSProduct
import com.hashmato.retailtouch.domain.model.products.PosUIState
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.common.dialogs.ActionDialog
import com.hashmato.retailtouch.presentation.common.AppCircleProgressIndicator
import com.hashmato.retailtouch.presentation.common.AppHorizontalDivider
import com.hashmato.retailtouch.presentation.common.AppPrimaryButton
import com.hashmato.retailtouch.presentation.common.BasicScreen
import com.hashmato.retailtouch.presentation.common.BottomTex
import com.hashmato.retailtouch.presentation.common.ButtonCard
import com.hashmato.retailtouch.presentation.common.ButtonRowCard
import com.hashmato.retailtouch.presentation.common.CommonListHeader
import com.hashmato.retailtouch.presentation.common.CommonListRow
import com.hashmato.retailtouch.presentation.common.dialogs.CreateMemberDialog
import com.hashmato.retailtouch.presentation.common.CreateMemberForm
import com.hashmato.retailtouch.presentation.common.GreyButtonWithElevation
import com.hashmato.retailtouch.presentation.common.dialogs.HoldSaleDialog
import com.hashmato.retailtouch.presentation.common.ListItemText
import com.hashmato.retailtouch.presentation.common.ListText
import com.hashmato.retailtouch.presentation.common.dialogs.MemberListDialog
import com.hashmato.retailtouch.presentation.common.NumberPad
import com.hashmato.retailtouch.presentation.common.QtyItemText
import com.hashmato.retailtouch.presentation.common.SearchableTextWithBg
import com.hashmato.retailtouch.presentation.common.SelectableRow
import com.hashmato.retailtouch.presentation.common.StocksListItem
import com.hashmato.retailtouch.presentation.common.dialogs.StockDialog
import com.hashmato.retailtouch.presentation.common.TexWithClickableBg
import com.hashmato.retailtouch.presentation.common.VectorIcons
import com.hashmato.retailtouch.presentation.common.dialogs.PromotionAndDiscountDialog
import com.hashmato.retailtouch.presentation.common.dialogs.TicketDiscountDialog
import com.hashmato.retailtouch.presentation.viewModels.SharedPosViewModel
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.utils.DiscountType
import com.hashmato.retailtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.lib.rememberValRef
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.cashier
import retailtouch.composeapp.generated.resources.clear_scanned_message
import retailtouch.composeapp.generated.resources.discount
import retailtouch.composeapp.generated.resources.discount_str
import retailtouch.composeapp.generated.resources.discount_value
import retailtouch.composeapp.generated.resources.hash
import retailtouch.composeapp.generated.resources.held_tickets
import retailtouch.composeapp.generated.resources.hold_sale
import retailtouch.composeapp.generated.resources.hold_transaction
import retailtouch.composeapp.generated.resources.ic_star
import retailtouch.composeapp.generated.resources.items
import retailtouch.composeapp.generated.resources.items_discount
import retailtouch.composeapp.generated.resources.items_value
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.price
import retailtouch.composeapp.generated.resources.promo_discount
import retailtouch.composeapp.generated.resources.qty
import retailtouch.composeapp.generated.resources.qty_value
import retailtouch.composeapp.generated.resources.retail_pos
import retailtouch.composeapp.generated.resources.search_items
import retailtouch.composeapp.generated.resources.sub_total
import retailtouch.composeapp.generated.resources.tax_value
import retailtouch.composeapp.generated.resources.total_value

object PosScreen:Screen{
    @Composable
    override fun Content() {
        Pos()
    }
}
@Composable
fun Pos(
    posViewModel: SharedPosViewModel = koinInject()
){
    val navigator = LocalNavigator.currentOrThrow
    val posUIState by posViewModel.posUIState.collectAsStateWithLifecycle()
    val authUser by posViewModel.authUser.collectAsStateWithLifecycle()
    val appState = LocalAppState.current

    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }


    LaunchedEffect(authUser){
        //posViewModel.getPrinterEnable()
        posViewModel.getAuthDetails()
        posViewModel.loadDbData()
    }

    LaunchedEffect(posUIState.isCallScannedItems) {
        posViewModel.fetchUIProductList()
    }

    LaunchedEffect(posUIState.cartList) {
        posViewModel.recomputeSale()
    }

    LaunchedEffect(posUIState.isError) {
        if (posUIState.isError) {
            //val success=getString(Res.string.success_title)
            snackbarHostState.value.showSnackbar(posUIState.errorMsg)
        }
    }

    BasicScreen(
        modifier = Modifier.systemBarsPadding(),
        title = stringResource(Res.string.cashier).uppercase(),
        isTablet = appState.isTablet,
        contentMaxWidth = Int.MAX_VALUE.dp,
        onBackClick = {
            navigator.pop()
        }
    ){
        val (vertPadding,horPadding)=if(appState.isPortrait)
            AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
        else
            AppTheme.dimensions.padding10 to AppTheme.dimensions.padding20


        val (textStyleHeader,btnStyle)=if(appState.isPortrait)
            AppTheme.typography.bodyBold() to AppTheme.typography.captionBold()
        else
            AppTheme.typography.titleBold() to AppTheme.typography.bodyBold()

        val space=if(appState.isPortrait)
            AppTheme.dimensions.padding3
        else
            AppTheme.dimensions.padding20

        val discountText=if(posUIState.globalDiscount>0){
            stringResource(Res.string.discount_str, posViewModel.getDiscountValue())
        }else{
            stringResource(Res.string.discount)
        }

        val (holdSaleText,icon) = if(posUIState.cartList.isEmpty() && posUIState.salesOnHold.isEmpty()){
            stringResource(Res.string.hold_sale) to AppIcons.pauseIcon
        }else if(posUIState.cartList.isEmpty() &&  posUIState.salesOnHold.isNotEmpty()){
            //val grandTotal = posUIState.salesOnHold.entries.sumOf { it.value.grandTotal }
            //val text = "#${posUIState.salesOnHold.size}."
            stringResource(Res.string.held_tickets) to null
        }else if(posUIState.cartList.isNotEmpty() &&  posUIState.salesOnHold.isNotEmpty()){
            stringResource(Res.string.hold_sale) to AppIcons.pauseIcon
        }else{
            stringResource(Res.string.hold_sale)  to AppIcons.pauseIcon
        }

        val holdSaleClickable= if(posUIState.cartList.isEmpty() && posUIState.salesOnHold.isEmpty()){
            false
        }else if(posUIState.cartList.isNotEmpty() && posUIState.salesOnHold.isEmpty()){
            true
        }else if(posUIState.cartList.isEmpty() && posUIState.salesOnHold.isNotEmpty()){
            true
        }else{
            true
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            //Top scrollable content
            LazyColumn(modifier = Modifier.weight(1f)){
                item{
                    //Top Search Bar
                    SearchableTextWithBg(
                        value = posUIState.searchQuery,
                        leadingIcon = AppIcons.searchIcon,
                        placeholder = stringResource(Res.string.search_items),
                        label = stringResource(Res.string.search_items),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                // When done is pressed, open the dialog
                                posViewModel.onSearchClicked(true)
                            }
                        ),
                        onValueChange = {
                            posViewModel.updateSearchQuery(it)
                        }
                    )

                    //Select Member
                    showMemberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = vertPadding),
                        posUIState=posUIState,
                        posViewModel=posViewModel
                    )

                    //List UI Header
                    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = vertPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        ListText(label = stringResource(Res.string.hash), textStyle = textStyleHeader, modifier = Modifier.width(30.dp))
                        ListText(label = stringResource(Res.string.items), textStyle = textStyleHeader,modifier = Modifier.weight(1.2f))
                        ListText(label = stringResource(Res.string.price),textStyle = textStyleHeader, modifier = Modifier.weight(1.2f))
                        ListText(label = stringResource(Res.string.qty), textStyle = textStyleHeader,modifier = Modifier.weight(1.2f))
                        ListText(label = stringResource(Res.string.sub_total),textStyle = textStyleHeader, modifier = Modifier.weight(1.2f))
                        VectorIcons(icons = AppIcons.removeIcon, modifier = Modifier.weight(.5f), onClick = {
                            posViewModel.updateClearCartDialogVisibility(posUIState.cartList.isNotEmpty())
                        })
                    }
                }

                itemsIndexed(posUIState.cartList
                ){index, product ->
                    POSTaxItem(
                        index = index,
                        item = product,
                        isPortrait = appState.isPortrait,
                        horizontalPadding=horPadding,
                        verticalPadding=vertPadding,
                        posViewModel=posViewModel
                    )
                }
            }

            //Bottom fix content
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(5.dp)){

                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spaceBetweenPadded(5.dp)) {
                    BottomTex(
                        label = stringResource(Res.string.qty_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey,
                        isPortrait = appState.isPortrait,
                        modifier = Modifier.wrapContentWidth()
                    )

                    BottomTex(
                        label = "${posUIState.quantityTotal}",
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey,
                        modifier = Modifier.wrapContentWidth()
                    )
                }

                /*Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BottomTex(
                        label = stringResource(Res.string.items_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey,
                    )

                    BottomTex(
                        label = posViewModel.formatPriceForUI(posUIState.cartTotalWithoutDiscount),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey,
                    )
                }*/

                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BottomTex(
                        label = stringResource(Res.string.discount_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.appRed)

                    BottomTex(
                        label = posViewModel.formatPriceForUI(posUIState.cartItemTotalDiscounts+posUIState.cartPromotionDiscount),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.appRed)
                }

                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BottomTex(
                        label = stringResource(Res.string.tax_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey)

                    BottomTex(
                        label = posViewModel.formatPriceForUI(posUIState.globalTax),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.textDarkGrey)
                }

                //Total Value

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    BottomTex(
                        label = stringResource(Res.string.total_value,":"),
                        textStyle = AppTheme.typography.h1Bold().copy(fontSize = 24.sp),
                        color = AppTheme.colors.textPrimary
                    )


                    BottomTex(
                        label = posViewModel.formatPriceForUI(posUIState.grandTotal),
                        textStyle = AppTheme.typography.h1Bold().copy(fontSize = 24.sp),
                        color = AppTheme.colors.textPrimary)
                }

                Row(modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space)) {

                    //Hold Button
                    AppPrimaryButton(
                        enabled = holdSaleClickable,
                        label = holdSaleText,
                        leftIcon = icon,
                        backgroundColor = AppTheme.colors.textPrimary,
                        disabledBackgroundColor = AppTheme.colors.textPrimary,
                        style = btnStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                            if(posUIState.cartList.isNotEmpty())
                                  posViewModel.holdCurrentSale()
                            else if(posUIState.cartList.isEmpty() && posUIState.salesOnHold.isNotEmpty())
                                posViewModel.updateHoldSalePopupState(true)

                        }
                    )

                    //Discount Button
                    AppPrimaryButton(
                        enabled = posUIState.cartList.isNotEmpty(),
                        label = discountText,
                        leftIcon = AppIcons.discountIcon,
                        backgroundColor = AppTheme.colors.appRed,
                        disabledBackgroundColor = AppTheme.colors.appRed,
                        style = btnStyle,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(),
                        onClick = {
                           // posViewModel.onGlobalDiscountClick()
                            posViewModel.onGlobalDiscountClick()
                        }
                    )

                    //Payment Button
                    AppPrimaryButton(
                        enabled = posUIState.cartList.isNotEmpty(),
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
                .align(Alignment.TopCenter))

        AppCircleProgressIndicator(
            isVisible=posUIState.isLoading
        )
    }


    StockDialog(
        isVisible = posUIState.showDialog,
        interactorRef = rememberValRef(posViewModel),
        onDismiss = {
            posViewModel.updateDialogState(false)
        },
        onItemClick = {selectedItem->
            posViewModel.updateDialogState(false)
            posViewModel.clearSearch()
            posViewModel.addSearchProduct(selectedItem)
        }
    )

    //Discount Content
    PromotionAndDiscountDialog(
        isVisible = posUIState.showPromotionDiscountDialog,
        promotions=posUIState.promotions,
        isPortrait=appState.isPortrait,
        onDismiss = {
            posViewModel.updatePromotionDiscountDialog(false)
        },
        onItemClick = {promotion->
            posViewModel.updatePromotionDiscountDialog(false)
            posViewModel.applyPromotionDiscounts(promotion)
        },
        onClearPromotionClick = {

        }
    )

    //Cart Item Discount Content
    TicketDiscountDialog(
        isVisible = posUIState.showDiscountDialog,
        inputValue = posUIState.itemDiscount,
        inputError = posUIState.inputDiscountError,
        trailingIcon= posViewModel.getDiscountTypeIcon(),
        selectedDiscountType = posUIState.selectedDiscountType,
        onDismissRequest = {
            posViewModel.updateItemRemoveDialogState(false)
        },
        onTabClick = {discountType->
            posViewModel.updateDiscountType(discountType)
        },
        onDiscountChange = { discount->
            posViewModel.updateDiscountValue(discount)
        },
        onApply = {
            posViewModel.onApplyDiscountClick()
        },
        onCancel = {
            posViewModel.updateItemRemoveDialogState(false)
        },
        onNumberPadClick = {symbol->
            posViewModel.onNumberPadClick(symbol)
        }
    )


    MemberListDialog(
        isVisible = posUIState.isMemberDialog,
        interactorRef = rememberValRef(posViewModel),
        onDismissRequest = {
            posViewModel.updateMemberDialogState(false)
        }
    )

    HoldSaleDialog(
        posState=posUIState,
        isVisible = posUIState.showHoldSalePopup,
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
        onDismiss = {
            posViewModel.updateHoldSalePopupState(false)
        },
        onRemove = { id->
           posViewModel.removeHoldSale(id)
        },
        onItemClick = {collection->
            posViewModel.reCallHoldSale(collection)
            if(posUIState.salesOnHold.isEmpty()){
                posViewModel.updateHoldSalePopupState(false)
            }
        }
    )


    ActionDialog(
        isVisible = posUIState.isRemoveDialog,
        dialogTitle = stringResource(Res.string.retail_pos),
        dialogMessage = stringResource(Res.string.clear_scanned_message),
        onDismissRequest = {
            posViewModel.updateClearCartDialogVisibility(false)
        },
        onCancel = {
            posViewModel.updateClearCartDialogVisibility(false)
        },
        onConfirm = {
            posViewModel.removedScannedItem()
        }
    )

    //CreateMemberDialog
    CreateMemberDialog(
        isVisible = posUIState.isCreateMemberDialog,
        onDismissRequest = {
            posViewModel.updateMemberDialogState(false)
        },
        dialogBody={
            //
            CreateMemberForm(
                posUIState=posUIState,
                posViewModel=posViewModel
            )
        }
    )

}

@Composable
fun POSTaxItem(
    index:Int,
    item: CartItem,
    isPortrait:Boolean,
    horizontalPadding: Dp,
    verticalPadding:Dp,
    posViewModel: SharedPosViewModel)
{
    val (primaryText, textTotalLabel) = when {
        item.discount > 0 || item.currentDiscount > 0 -> {
            AppTheme.colors.textError to  "  ${posViewModel.formatPriceForUI(item.getFinalPrice())} \n (${posViewModel.calculateDiscount(item)})"
        }
        else -> {
            AppTheme.colors.primaryText.copy(alpha = 0.8f) to posViewModel.formatPriceForUI(item.getFinalPrice())
        }
    }

    val (borderColor,rowBgColor)=when(index%2 == 0){
        true->  AppTheme.colors.borderColor to AppTheme.colors.listRowBgColor
        false ->AppTheme.colors.appWhite to AppTheme.colors.appWhite
    }

    val (buttonBgColor,textColor)=when(index%2 == 0){
        true->   AppTheme.colors.primaryColor to AppTheme.colors.appWhite
        false -> AppTheme.colors.listRowBgColor to AppTheme.colors.textPrimary
    }

    if(isPortrait){
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite)){
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor),
                verticalArrangement = Arrangement.spaceBetweenPadded(5.dp)) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ListText(
                        label = "${index+1}",
                        textStyle = AppTheme.typography.bodyMedium(),
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.width(30.dp)
                    )
                    Row(modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if(item.promotion!=null){
                            Image(
                                painter = painterResource(Res.drawable.ic_star),
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.dimensions.smallXIcon),
                                contentScale = ContentScale.Crop
                            )
                        }
                        //Items name
                        ListText(
                            label = "${item.stock.name} [${item.stock.inventoryCode?:""}]",
                            textStyle = AppTheme.typography.bodyMedium(),
                            color = AppTheme.colors.textBlack,
                            modifier = Modifier.wrapContentWidth()
                        )
                    }

                    //modifier icons
                    VectorIcons(icons = AppIcons.cancelIcon,
                        modifier = Modifier.weight(.5f),
                        onClick = {
                            posViewModel.removedListItem()
                        }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Spacer(modifier = Modifier.weight(1.3f))

                    //Price
                    Row(modifier = Modifier.weight(1.2f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        GreyButtonWithElevation(
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            label = posViewModel.formatPriceForUI(item.price),
                            contentColor = textColor,
                            buttonBgdColor = buttonBgColor,
                            textStyle = AppTheme.typography.bodyNormal(),
                            onClick = {
                                posViewModel.onItemDiscountClick(item,index)
                            }
                        )
                    }

                    //Qty
                    Row(modifier = Modifier.weight(1.2f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.dp)){
                        QtyItemText(
                            label = "${item.qty}",
                            textStyle = AppTheme.typography.bodyMedium(),
                            color = AppTheme.colors.textPrimary,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            isEven=index%2 == 0,
                            isPortrait=isPortrait,
                            onIncreaseClick = {
                                posViewModel.increaseQty(item)
                            },
                            onDecreaseClick = {
                                posViewModel.decreaseQty(item)
                            },
                            onClick = {
                                posViewModel.applyCustomQty(item)
                            }
                        )
                    }
                    Column(modifier = Modifier.weight(1.2f).wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ListText(
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            label = posViewModel.formatPriceForUI(item.getFinalPrice()),
                            textStyle = AppTheme.typography.bodyMedium(),
                            color = AppTheme.colors.textPrimary
                        )

                        if(item.discount > 0 || item.currentDiscount > 0){
                           ListText(
                               modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                               label = "(${posViewModel.calculateDiscount(item)})",
                               textStyle = AppTheme.typography.bodyMedium(),
                               color = AppTheme.colors.textError
                           )
                        }
                    }

                }
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    }
    else{
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite)){
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor),verticalArrangement = Arrangement.spaceBetweenPadded(5.dp)) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    //Hash index
                    ListItemText(
                        label = "${index+1}",
                        textStyle = AppTheme.typography.bodyMedium(),
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.width(30.dp)
                    )

                    if(item.exchange){
                        VectorIcons(icons = AppIcons.e_exchangeIcon,
                            modifier = Modifier.width(AppTheme.dimensions.smallXIcon),
                            iconColor= if(item.exchange) AppTheme.colors.primaryText else AppTheme.colors.secondaryText,
                            onClick = {

                            }
                        )
                    }

                    Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if(item.promotion!=null){
                            Image(
                                painter = painterResource(Res.drawable.ic_star),
                                contentDescription = null,
                                modifier = Modifier.size(AppTheme.dimensions.smallXIcon),
                                contentScale = ContentScale.Crop
                            )
                        }
                        //Items name
                        ListItemText(
                            label = "${item.stock.name} \n[${item.stock.inventoryCode?:""}]",
                            textStyle = AppTheme.typography.bodyMedium(),
                            color = AppTheme.colors.textBlack,
                            modifier = Modifier.wrapContentWidth(),
                            singleLine = false
                        )
                    }


                    //Price
                    Row(modifier = Modifier.weight(1.2f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        GreyButtonWithElevation(
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            label = posViewModel.formatPriceForUI(item.price),
                            contentColor = textColor,
                            buttonBgdColor = buttonBgColor,
                            textStyle = AppTheme.typography.bodyNormal(),
                            onClick = {posViewModel.onItemDiscountClick(item,index)}
                        )
                    }


                    //Qty
                    Row(modifier = Modifier.weight(1.2f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.dp)){
                        QtyItemText(
                            label = "${item.qty}",
                            textStyle = AppTheme.typography.bodyMedium(),
                            color = AppTheme.colors.primaryText,
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            isPortrait=isPortrait,
                            isEven=index%2 == 0,
                            onIncreaseClick = {
                                posViewModel.increaseQty(item)
                            },
                            onDecreaseClick = {
                                posViewModel.decreaseQty(item)
                            },
                            onClick = {
                                posViewModel.applyCustomQty(item)
                            }
                        )
                    }


                    //Subtotal
                    Column(modifier = Modifier.weight(1.2f).wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ListText(
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            label = posViewModel.formatPriceForUI(item.getFinalPrice()),
                            textStyle = AppTheme.typography.bodyMedium(),
                            color = primaryText
                        )
                        if(item.discount > 0 || item.currentDiscount > 0){
                            ListText(
                                modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                                label = "(${posViewModel.calculateDiscount(item)})",
                                textStyle = AppTheme.typography.bodyMedium(),
                                color = AppTheme.colors.textError
                            )
                        }

                    }

                    //modifier icons
                    VectorIcons(icons = AppIcons.cancelIcon,
                        modifier = Modifier.weight(.5f),
                        onClick = {
                            posViewModel.removedListItem()
                        }
                    )

                }
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    }
}

@Composable
fun showMemberCard(modifier : Modifier,posUIState: PosUIState, posViewModel: SharedPosViewModel) {
    Card(
        modifier = modifier
            .clickable{
                posViewModel.updateMemberDialogState(true)
            },
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.brand),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(5.dp)
    ){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                text = posUIState.selectedMember,
                style = AppTheme.typography.bodyNormal(),
                color = AppTheme.colors.appWhite
            )

            Icon(
                modifier = Modifier.size(AppTheme.dimensions.smallXIcon),
                imageVector = vectorResource(AppIcons.downArrowIcon),
                contentDescription = "",
                tint = AppTheme.colors.appWhite
            )

        }
    }
}

@Composable
fun HoldSaleCard(
    posUIState:PosUIState,
    posViewModel: SharedPosViewModel,
    modifier: Modifier = Modifier.fillMaxWidth(),
    elevation: CardElevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
    onHoldClick : () -> Unit
){
    val grandTotal=posUIState.salesOnHold.entries.sumOf { it.value.grandTotal }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.appWhite),
        border = BorderStroke(width = 1.dp, color = AppTheme.colors.listBorderColor),
        elevation = elevation,
        shape = AppTheme.appShape.card
    ){

        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().clickable{
            onHoldClick.invoke()
        }){
            // Row 1: Rounded corners on the left
            SelectableRow(
                modifier = Modifier.weight(1.5f).height(AppTheme.dimensions.defaultButtonSize),
                text = stringResource(Res.string.hold_transaction),
                isSelected = false,
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp) // Rounded left corners
            )
            // Row 2: Rounded corners on the right
            SelectableRow(
                modifier = Modifier.weight(.5f).height(AppTheme.dimensions.defaultButtonSize),
                text = "#${posUIState.salesOnHold.size} [${posViewModel.formatPriceForUI(grandTotal)}]",
                isSelected = true,
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp) // Rounded right corners
            )
        }
    }
}

@Composable
fun BottomContent(modifier: Modifier, posUIState: PosUIState, posViewModel: SharedPosViewModel, onPaymentClick:  () -> Unit){

    Row(modifier = modifier) {

        Column(modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Column(modifier=Modifier.wrapContentHeight().weight(1f)) {
                    BottomTex(
                        label = stringResource(Res.string.items_discount),
                        textStyle = AppTheme.typography.titleMedium()
                    )

                    BottomTex(
                        label = posViewModel.formatPriceForUI(posUIState.cartItemTotalDiscounts),
                        textStyle = AppTheme.typography.titleMedium().copy(fontSize = 20.sp)
                    )
                }

                Column(modifier=Modifier.wrapContentHeight().weight(1f), horizontalAlignment = Alignment.End) {
                    BottomTex(
                        label = stringResource(Res.string.promo_discount),
                        textStyle = AppTheme.typography.titleMedium(),
                        singleLine = true
                    )

                    BottomTex(
                        label = posViewModel.formatPriceForUI(posUIState.cartPromotionDiscount),
                        textStyle = AppTheme.typography.titleMedium().copy(fontSize = 20.sp)
                    )
                }

            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
               Column(modifier = Modifier
                   .wrapContentHeight()
                   .wrapContentWidth(),
                   verticalArrangement = Arrangement.spacedBy(10.dp),
                   horizontalAlignment = Alignment.Start) {
                   Row(modifier = Modifier.wrapContentWidth()) {
                       BottomTex(
                           label = stringResource(Res.string.qty_value,":"),
                           textStyle = AppTheme.typography.titleMedium())

                       BottomTex(
                           label = "${posUIState.quantityTotal}",
                           textStyle = AppTheme.typography.titleMedium().copy(fontSize = 20.sp))
                   }

                   Row(modifier = Modifier.wrapContentWidth()) {
                       BottomTex(
                           label = stringResource(Res.string.items_value,":"),
                           textStyle = AppTheme.typography.titleMedium())

                       BottomTex(
                           label = posViewModel.formatPriceForUI(posUIState.cartTotalWithoutDiscount),
                           textStyle = AppTheme.typography.titleMedium().copy(fontSize = 20.sp))
                   }

                   Row(modifier = Modifier.wrapContentWidth()) {
                       BottomTex(
                           label = stringResource(Res.string.tax_value,":"),
                           textStyle = AppTheme.typography.titleMedium())

                       BottomTex(
                           label = posViewModel.formatPriceForUI(posUIState.globalTax),
                           textStyle = AppTheme.typography.titleMedium().copy(fontSize = 20.sp))
                   }

                   Row(modifier = Modifier.wrapContentWidth()) {
                       BottomTex(
                           label = stringResource(Res.string.total_value,":"),
                           textStyle = AppTheme.typography.titleMedium(),
                           color = AppTheme.colors.textError
                       )


                       BottomTex(
                           label = posViewModel.formatPriceForUI(posUIState.grandTotal),
                           textStyle = AppTheme.typography.titleMedium().copy(fontSize = 20.sp),
                           color = AppTheme.colors.textError)
                   }
               }

                TexWithClickableBg(onClick = {
                    //open discount pad
                    posViewModel.onGlobalDiscountClick()

                }){
                    BottomTex(label = stringResource(Res.string.discount_value, posViewModel.getDiscountValue()), color = AppTheme.colors.appWhite)
                }
            }

            Row(modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                //Hold Button
                ButtonRowCard(
                    modifier = Modifier.height(AppTheme.dimensions.defaultButtonSize).weight(1f),
                    label = stringResource(Res.string.hold_sale),
                    icons = AppIcons.pauseIcon,
                    backgroundColor = AppTheme.colors.buttonPrimaryBgColor,
                    disableBgColor= AppTheme.colors.buttonPrimaryDisableBgColor,
                    isEnabled = posUIState.cartList.isNotEmpty(),
                    isColorChange = true,
                    onClick = {
                        //on Hold Button click
                        posViewModel.holdCurrentSale()
                    }
                )

                //Payment Button
                ButtonRowCard(
                    modifier = Modifier.height(AppTheme.dimensions.defaultButtonSize).weight(1f),
                    label = stringResource(Res.string.payment),
                    icons = AppIcons.paymentIcon,
                    backgroundColor = AppTheme.colors.buttonGreenBgColor,
                    disableBgColor= AppTheme.colors.buttonGreenDisableBgColor,
                    isEnabled = posUIState.cartList.isNotEmpty(),
                    isColorChange = true,
                    onClick = {
                        onPaymentClick.invoke()
                    }
                )
            }
        }

    }
}

@Composable
fun HoldSaleContent(modifier : Modifier,posUIState: PosUIState,posViewModel: SharedPosViewModel) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //val collectionList = posUIState.salesOnHold.entries.toList()
        //Header
        if (posUIState.salesOnHold.size == 1) {
            val collection=posUIState.salesOnHold.entries.first().value
            ButtonCard(
                modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                label = posViewModel.formatPriceForUI(collection.grandTotal),
                discountType = "# ${posUIState.salesOnHold.size}",
                backgroundColor = AppTheme.colors.appGreen,
                innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonSquarePadding, vertical = AppTheme.dimensions.buttonSquarePadding),
                onClick = {
                    posViewModel.reCallHoldSale(collection)
                }
            )
        }else {
            ButtonRowCard(
                modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                label = "${posUIState.salesOnHold.size}",
                icons = AppIcons.downArrowIcon,
                iconSize = AppTheme.dimensions.smallIcon,
                isDropdownExpanded = posUIState.isDropdownExpanded,
                onClick = {
                    posViewModel.onToggleChange()
                }
            )
        }

        /*if(posUIState.isDropdownExpanded){
            showHoldSaleList(posUIState,posViewModel)
        }else if(posUIState.salesOnHold.size==1){
            showHoldSaleList(posUIState,posViewModel)
        }*/
    }
}
@Composable
fun showHoldSaleList(posUIState: PosUIState, posViewModel: SharedPosViewModel) {
    LazyColumn(modifier = Modifier.wrapContentWidth().weight(1f)) {
        itemsIndexed(posUIState.salesOnHold.toList()
        ){  index,(key, value) ->
            // entry.key is the ID, and entry.value is the HeldCollection
            HoldSaleCollectionItem(index=index,collection = value,posViewModel=posViewModel)
        }
    }
}

@Composable
fun HoldSaleCollectionItem(
    posViewModel: SharedPosViewModel,
    collection: CRSaleOnHold,
    index: Int,
) {

    Column(modifier = Modifier.wrapContentHeight().wrapContentWidth().padding(vertical = 10.dp)) {
        ButtonCard(
            modifier = Modifier.wrapContentHeight().wrapContentWidth(),
            label = posViewModel.formatPriceForUI(collection.grandTotal),
            discountType = "# ${index+1}",
            backgroundColor = AppTheme.colors.appGreen,
            innerPaddingValues = PaddingValues(horizontal = AppTheme.dimensions.buttonSquarePadding, vertical = AppTheme.dimensions.buttonSquarePadding),
            onClick = {
                posViewModel.reCallHoldSale(collection)
            }
        )
    }
}


@Composable
fun DialogStockScreen(
    state:PosUIState, searchQuery: String, currencySymbol: String,
    onClick: (POSProduct) -> Unit,
    onQueryChange: (String) -> Unit,

    ){
    val appState = LocalAppState.current
    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding20

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().background(AppTheme.colors.appWhite)
    ){
        SearchableTextWithBg(
            value = state.searchQuery,
            leadingIcon=AppIcons.searchIcon,
            placeholder = stringResource(Res.string.search_items),
            label = stringResource(Res.string.search_items),
            modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10),
            onValueChange = {
                onQueryChange(it)
            }
        )

        //List Content
        CommonListHeader()
        // Display filtered products in a LazyColumn
        LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()){
            // Filter the product tax list based on the search query
            val filteredProducts = state.stockList.filter { it.matches(searchQuery) }.toMutableList()
            itemsIndexed(filteredProducts){ index, product ->
                StocksListItem(position=index,product=product,currencySymbol=currencySymbol, onClick = { selectedItem->
                    onClick(selectedItem)
                })
            }
        }
    }
}



@Composable
fun DialogListItem(position :Int, product: POSProduct, currencySymbol: String, onClick: (POSProduct) -> Unit) {
    val appState = LocalAppState.current
    val (borderColor,rowBgColor)=when(position%2 != 0){
        true->  AppTheme.colors.borderColor to AppTheme.colors.listRowBgColor
        false ->AppTheme.colors.appWhite to AppTheme.colors.appWhite
    }

    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding20

    if(appState.isPortrait){
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite)){
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor).clickable{onClick(product)},
                verticalArrangement = Arrangement.spaceBetweenPadded(10.dp)) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                CommonListRow(product=product, currencySymbol=currencySymbol)
                ListText(
                    label = product.name?:"",
                    textStyle = AppTheme.typography.bodyMedium(),
                    color = AppTheme.colors.textBlack,
                    modifier = Modifier.wrapContentWidth().padding(start = horizontalPadding)
                )
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    }
    else{
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite)){
            Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(rowBgColor)
                .clickable{onClick(product)},
                verticalArrangement =Arrangement.spaceBetweenPadded(10.dp)
            ) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                CommonListRow(product=product,currencySymbol=currencySymbol)
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    }
}



@Composable
fun DiscountContent(
    inputValue:String,
    inputError:String?=null,
    trailingIcon:DrawableResource?=null,
    selectedDiscountType:DiscountType,
    onClick: (DiscountType) -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit,
    onDiscountChange: (discount: String) -> Unit,
    onNumberPadClick: (symbol: String) -> Unit,
){

    Column(
       modifier = Modifier
           .fillMaxWidth()
           .wrapContentHeight()
           .padding(10.dp)
           .verticalScroll(rememberScrollState()),
       horizontalAlignment = Alignment.CenterHorizontally,
       verticalArrangement = Arrangement.spacedBy(10.dp)
   ){

       //1st Row

       Row(modifier = Modifier
           .fillMaxWidth(),
           verticalAlignment = Alignment.CenterVertically
       ) {
           ButtonCard(
               modifier = Modifier.wrapContentHeight().weight(1f).padding(horizontal = 5.dp),
               label = stringResource(Res.string.discount),
               icons = AppIcons.dollarIcon,
               backgroundColor = AppTheme.colors.appGreen,
               innerPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
               isEnabled=selectedDiscountType!=DiscountType.FIXED_AMOUNT,
               isColorChange = true,
               onClick = {
                   onClick.invoke(DiscountType.FIXED_AMOUNT)
               }
           )

           ButtonCard(
               modifier = Modifier.wrapContentHeight().weight(1f).padding(horizontal = 5.dp),
               label = stringResource(Res.string.discount),
               icons = AppIcons.percentageIcon,
               backgroundColor = AppTheme.colors.appGreen,
               innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
               isEnabled=selectedDiscountType!=DiscountType.PERCENTAGE,
               isColorChange = true,
               onClick = {
                   onClick.invoke(DiscountType.PERCENTAGE)
               }
           )

        }

       NumberPad(
           textValue=inputValue,
           onValueChange = {discount->
               onDiscountChange.invoke(discount)
           },
           trailingIcon = trailingIcon,
           inputError=inputError,
           onNumberPadClick = {symbol->
               onNumberPadClick.invoke(symbol)
           }, onApplyClick = {
               onApply.invoke()
           }, onCancelClick = {
               onCancel.invoke()
           },
           onClearDiscountClick = {

           }
       )
   }
}


//Hold Transactions
/*if(posUIState.salesOnHold.isNotEmpty()){
    HoldSaleCard(
        posUIState=posUIState,
        posViewModel=posViewModel,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = vertPadding),
        onHoldClick = {

        })
}*/





