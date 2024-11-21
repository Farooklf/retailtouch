package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.App
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.products.CRShoppingCartItem
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.products.HeldCollection
import com.lfssolutions.retialtouch.domain.model.products.PosUIState
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.ActionDialog
import com.lfssolutions.retialtouch.presentation.ui.common.AppCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.AppHorizontalDivider
import com.lfssolutions.retialtouch.presentation.ui.common.AppLeftSideMenu
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedSearch
import com.lfssolutions.retialtouch.presentation.ui.common.AppScreenPadding
import com.lfssolutions.retialtouch.presentation.ui.common.BottomTex
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonCard
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonRowCard
import com.lfssolutions.retialtouch.presentation.ui.common.CreateMemberDialog
import com.lfssolutions.retialtouch.presentation.ui.common.CreateMemberForm
import com.lfssolutions.retialtouch.presentation.ui.common.DiscountDialog
import com.lfssolutions.retialtouch.presentation.ui.common.HoldSaleDialog
import com.lfssolutions.retialtouch.presentation.ui.common.ListItemText
import com.lfssolutions.retialtouch.presentation.ui.common.ListText
import com.lfssolutions.retialtouch.presentation.ui.common.MemberList
import com.lfssolutions.retialtouch.presentation.ui.common.MemberListDialog
import com.lfssolutions.retialtouch.presentation.ui.common.NumberPad
import com.lfssolutions.retialtouch.presentation.ui.common.QtyItemText
import com.lfssolutions.retialtouch.presentation.ui.common.SearchableTextFieldWithDialog
import com.lfssolutions.retialtouch.presentation.ui.common.SearchableTextWithBg
import com.lfssolutions.retialtouch.presentation.ui.common.TexWithClickableBg
import com.lfssolutions.retialtouch.presentation.ui.common.VectorIcons
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DiscountType
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.modifier.defaultMaxSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.barcode
import retailtouch.composeapp.generated.resources.clear_scanned_message
import retailtouch.composeapp.generated.resources.description
import retailtouch.composeapp.generated.resources.discount
import retailtouch.composeapp.generated.resources.discount_value
import retailtouch.composeapp.generated.resources.hash
import retailtouch.composeapp.generated.resources.hold_sale
import retailtouch.composeapp.generated.resources.ic_star
import retailtouch.composeapp.generated.resources.in_stock
import retailtouch.composeapp.generated.resources.items
import retailtouch.composeapp.generated.resources.items_discount
import retailtouch.composeapp.generated.resources.items_value
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.price
import retailtouch.composeapp.generated.resources.promo_discount
import retailtouch.composeapp.generated.resources.qty
import retailtouch.composeapp.generated.resources.qty_value
import retailtouch.composeapp.generated.resources.retail_pos
import retailtouch.composeapp.generated.resources.search
import retailtouch.composeapp.generated.resources.search_items
import retailtouch.composeapp.generated.resources.sku
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

    LaunchedEffect(Unit) {
        posViewModel.isLoggedIn()
    }

    LaunchedEffect(Unit){
       posViewModel.initialState()
    }

    LaunchedEffect(authUser){
        posViewModel.getPrinterEnable()
        posViewModel.getAuthDetails()
        posViewModel.loadDbData()
    }

    LaunchedEffect(posUIState.isCallScannedItems) {
        posViewModel.fetchUIProductList()
    }

    LaunchedEffect(posUIState.cartList) {
        posViewModel.recomputeSale()
    }

    AppLeftSideMenu(
        syncInProgress = posUIState.syncInProgress,
        exchangeActive = posUIState.globalExchangeActivator,
        printerEnabled = posUIState.isPrinterEnable,
        modifier = Modifier.fillMaxSize(),
        onActivateExchange = {
          posViewModel.updateGlobalExchangeActivator(!posUIState.globalExchangeActivator)
        },
        onActivatePrinter={
            posViewModel.updatePrinterValue(!posUIState.isPrinterEnable)
        },
        onCategoryClick = {
            NavigatorActions.navigateBackToHomeScreen(navigator,false)
        },
        onSyncClick = {
            posViewModel.syncPendingSales()
        },
        content = {
            //for POS Screen
            AppScreenPadding(
                content = { horizontalPadding, verticalPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = horizontalPadding, vertical = verticalPadding)){
                        Column {
                            PosTopContent(modifier = Modifier.weight(1f),posUIState,posViewModel)

                            //Bottom Row Calculation
                            BottomContent(modifier  = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                                posUIState=posUIState,
                                posViewModel=posViewModel,
                                onPaymentClick = {
                                    NavigatorActions.navigateToPaymentScreen(navigator)
                                }
                            )
                        }
                        AppCircleProgressIndicator(
                            isVisible=posUIState.isLoading
                        )
                    }
                }
            )
        }
    )


    SearchableTextFieldWithDialog(
        isVisible = posUIState.showDialog,
        onDismiss = {
            posViewModel.updateDialogState(false)
        },
        content = {
            if(posUIState.stockList.isEmpty())
                posViewModel.loadAllProducts()

            DialogStockScreen(
                state = posUIState,
                searchQuery = posUIState.searchQuery,
                currencySymbol = posUIState.currencySymbol,
                onClick = {selectedItem->
                    posViewModel.updateDialogState(false)
                    posViewModel.clearSearch()
                    posViewModel.addSearchProduct(selectedItem)
                },
                onQueryChange = { newQuery ->
                    posViewModel.updateSearchQuery(newQuery)
                },
            )

        }
    )

    //Discount Content
    DiscountDialog(
        isVisible = posUIState.isDiscountDialog,
        onDismissRequest = {
            posViewModel.dismissDiscountDialog()
        },
        dialogBody = {
            DiscountContent(
                inputValue = posUIState.inputDiscount,
                inputError = posUIState.inputDiscountError,
                trailingIcon = posViewModel.getDiscountTypeIcon(),
                selectedDiscountType = posUIState.selectedDiscountType,
                onDiscountChange = { discount->
                    posViewModel.updateDiscountValue(discount)
                },
                onNumberPadClick = {symbol->
                    posViewModel.onNumberPadClick(symbol)
                },
                onCancel = {
                    posViewModel.dismissDiscountDialog()
                },
                onApply = {
                    posViewModel.onApplyDiscountClick()
                },
                onClick = {discountType->
                    posViewModel.updateDiscountType(discountType)
                }
            )
        }
    )


    MemberListDialog(
        isVisible = posUIState.isMemberDialog,
        onDismissRequest = {
            posViewModel.updateMemberDialogState(false)
        },
        dialogBody={
            MemberList(
                posUIState=posUIState,
                posViewModel=posViewModel,
                onMemberCreate = {
                   //create Member dialog
                 posViewModel.updateCreateMemberDialogState(true)
                }
            )
        }
    )

    HoldSaleDialog(
        isVisible = posUIState.isDropdownExpanded,
        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
        contentMaxWidth = AppTheme.dimensions.holdSaleListDefaultWidth,
        onDismissRequest = {
            posViewModel.onToggleChange()
        },
        dialogBody={
            showHoldSaleList(posUIState,posViewModel)
        }
    )

    ActionDialog(
        isVisible = posUIState.isRemoveDialog,
        dialogTitle = stringResource(Res.string.retail_pos),
        dialogMessage = stringResource(Res.string.clear_scanned_message),
        onDismissRequest = {
            posViewModel.updateRemoveDialogState(false)
        },
        onCancel = {
            posViewModel.updateRemoveDialogState(false)
        },
        onYes = {
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
fun PosTopContent(modifier:Modifier, posUIState: PosUIState, posViewModel: SharedPosViewModel) {
    Column(modifier=modifier, verticalArrangement = Arrangement.SpaceBetween) {
        var width by remember { mutableStateOf(0) }
        val density = LocalDensity.current
        val widthDp = remember(width, density) { with(density) { width.toDp() } }
        val searchFieldFocus = remember { FocusRequester() }


        LazyColumn(modifier=Modifier.weight(1f).onSizeChanged {
            width = it.width
        }) {
            item{
                //Top Search Bar
                AppOutlinedSearch(
                    value = posUIState.searchQuery,
                    onValueChange = { posViewModel.updateSearchQuery(it) },
                    placeholder = stringResource(Res.string.search),
                    leadingIcon = AppIcons.searchIcon,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    onSubmittedClick = {
                        posViewModel.scanBarcode()
                        searchFieldFocus.requestFocus() // Keep focus on the search field
                    },
                    modifier = Modifier.fillMaxWidth().padding(2.dp).focusRequester(searchFieldFocus)
                )


                Row(modifier=Modifier.fillMaxWidth().padding(2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    //Select Member
                    showMemberCard(modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                        posUIState=posUIState,
                        posViewModel=posViewModel
                    )

                    //Hold Sale
                    if (posUIState.isHoldSaleDialog) {
                        HoldSaleContent(
                            modifier = Modifier
                                .wrapContentWidth()
                                .defaultMaxSize(AppTheme.dimensions.holdSaleListDefaultWidth)
                                .wrapContentHeight(),
                            posUIState=posUIState,
                            posViewModel=posViewModel
                        )
                    }


                }

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                    //List UI Header
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spaceBetweenPadded(5.dp)
                    ){
                        ListItemText(label = stringResource(Res.string.hash), modifier = Modifier.width(30.dp))
                        ListItemText(label = stringResource(Res.string.items), modifier = Modifier.width(150.dp))
                        ListItemText(label = stringResource(Res.string.price), modifier = Modifier.width(100.dp))
                        ListItemText(label = stringResource(Res.string.qty), modifier = Modifier.width(150.dp))
                        ListItemText(label = stringResource(Res.string.sub_total), modifier = Modifier.width(100.dp))
                        VectorIcons(icons = AppIcons.removeIcon, modifier = Modifier.width(AppTheme.dimensions.smallIcon), onClick = {
                            posViewModel.updateRemoveDialogState(posUIState.cartList.isNotEmpty())
                        })
                    }
                }

            }
            itemsIndexed(posUIState.cartList
            ){index, product ->
                AppHorizontalDivider(modifier=Modifier.width(widthDp))
                POSTaxItem(
                    index = index,
                    item = product,
                    posViewModel=posViewModel,
                    onPriceClick = { selectedItem->
                        posViewModel.onPriceItemClick(selectedItem,index)
                    },
                    increaseQty = { selectedItem->
                        posViewModel.increaseQty(selectedItem)
                    },
                    decreaseQty = {selectedItem->
                        posViewModel.decreaseQty(selectedItem)
                    },
                    onCustomQtyClick={selectedItem->
                        posViewModel.applyCustomQty(selectedItem)
                    },
                    onRemoveClick = { selectedItem->
                        posViewModel.removedListItem(selectedItem)
                    }
                )
            }

        }
    }
}

@Composable
fun POSTaxItem(
    index:Int,
    item: CRShoppingCartItem,
    posViewModel: SharedPosViewModel,
    onPriceClick: (CRShoppingCartItem) -> Unit,
    increaseQty: (CRShoppingCartItem) -> Unit,
    decreaseQty: (CRShoppingCartItem) -> Unit,
    onCustomQtyClick: (CRShoppingCartItem) -> Unit,
    onRemoveClick: (CRShoppingCartItem) -> Unit,
)
{
    val (primaryText, textTotalLabel) = when {
        item.discount > 0 || item.currentDiscount > 0 -> {
            AppTheme.colors.textError to  "  ${posViewModel.formatPriceForUI(item.getFinalPrice())} \n (${posViewModel.calculateDiscount(item)})"
        }
        else -> {
            AppTheme.colors.primaryText.copy(alpha = 0.8f) to posViewModel.formatPriceForUI(item.getFinalPrice())
        }
    }

    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 10.dp).horizontalScroll(
        rememberScrollState()
    )) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spaceBetweenPadded(5.dp)
        ){
            //Hash index
            ListItemText(
                label = "${index+1}",
                textStyle = AppTheme.typography.titleNormal(),
                color = AppTheme.colors.primaryText.copy(alpha = .8f),
                modifier = Modifier.width(30.dp).padding(vertical = 10.dp)
            )

            if(item.exchange){
                VectorIcons(icons = AppIcons.e_exchangeIcon,
                    modifier = Modifier.width(AppTheme.dimensions.smallXIcon),
                    iconColor= if(item.exchange) AppTheme.colors.primaryText else AppTheme.colors.secondaryText,
                    onClick = {

                    }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)
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
                    textStyle = AppTheme.typography.titleNormal(),
                    color = AppTheme.colors.primaryText.copy(alpha = .8f),
                    modifier = Modifier.width(150.dp),
                    singleLine = false
                )
            }

            //Price
            ListItemText(
                label = posViewModel.formatPriceForUI(item.getFinalPrice()),
                textStyle = AppTheme.typography.titleNormal(),
                color = AppTheme.colors.primaryText.copy(alpha = .8f),
                modifier = Modifier.width(100.dp),
                isButton = true,
                onButtonClick = {
                    onPriceClick.invoke(item)
                }
            )

            //Qty
            QtyItemText(
                label = "${item.qty}",
                textStyle = AppTheme.typography.titleNormal(),
                color = AppTheme.colors.primaryText.copy(alpha = .8f),
                modifier = Modifier.width(160.dp),
                onIncreaseClick = {
                    increaseQty.invoke(item)
                },
                onDecreaseClick = {
                    decreaseQty.invoke(item)
                },
                onClick = {
                    onCustomQtyClick(item)
                }
            )

            //Subtotal
            Column(modifier = Modifier.width(100.dp).wrapContentHeight(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                ListItemText(
                    modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                    label = textTotalLabel,
                    textStyle = AppTheme.typography.titleNormal(),
                    color = primaryText,
                    singleLine = false
                )
                /*if(item.discount>0 || item.currentDiscount>0){
                    ListItemText(
                        label = "(${posViewModel.calculateDiscount(item)})",
                        textStyle = AppTheme.typography.bodyNormal(),
                        color = primaryText,
                        modifier = Modifier.wrapContentWidth().wrapContentHeight()
                    )
                }*/
            }

            //modifier icons
            VectorIcons(icons = AppIcons.closeIcon,
                modifier = Modifier.width(AppTheme.dimensions.smallIcon),
                onClick = {
                    onRemoveClick(item)
                }
            )

        }

    }

}

@Composable
fun showMemberCard(modifier : Modifier,posUIState: PosUIState, posViewModel: SharedPosViewModel) {
    Card(
        modifier =modifier
            .clickable{
                posViewModel.updateMemberDialogState(true)
            },
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundDialog),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(5.dp)
    ){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {

            Text(
                modifier = Modifier.wrapContentHeight().wrapContentWidth(),
                text = posUIState.selectedMember,
                style = AppTheme.typography.bodyNormal(),
                color = AppTheme.colors.primaryText
            )

            Icon(
                modifier = Modifier.size(AppTheme.dimensions.smallIcon),
                imageVector = vectorResource(AppIcons.downArrowIcon),
                contentDescription = "",
                tint = AppTheme.colors.primaryText
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
                        label = posViewModel.formatPriceForUI(posUIState.cartItemsDiscount),
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
                    posViewModel.onTotalDiscountItemClick()

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
                innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
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
    onClick: (Product) -> Unit,
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
                DialogListItem(position=index,product=product,currencySymbol=currencySymbol, onClick = { selectedItem->
                    onClick(selectedItem)
                })
            }
        }
    }
}

@Composable
fun CommonListHeader(){
    val appState = LocalAppState.current
    val textStyle=if(appState.isPortrait)
        AppTheme.typography.bodyBold()
    else
        AppTheme.typography.titleBold()

    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding20

    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)

    ){
        //SKU
        // Adjust the weight proportions
        ListText(
            label = stringResource(Res.string.sku).uppercase(),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1.2f)
        )
        ListText(
            label = stringResource(Res.string.barcode),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1.2f))
        ListText(
            label = stringResource(Res.string.price),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1f))
        ListText(
            label = stringResource(Res.string.in_stock),
            color = AppTheme.colors.textBlack,
            textStyle = textStyle,
            modifier = Modifier.weight(1f))

        if(!appState.isPortrait)
        {
            ListText(
                label = stringResource(Res.string.description),
                color = AppTheme.colors.textBlack,
                textStyle = textStyle,
                modifier = Modifier.weight(1.5f))
        }
    }
}

@Composable
fun DialogListItem(position :Int,product: Product, currencySymbol: String, onClick: (Product) -> Unit) {
    val appState = LocalAppState.current
    val (borderColor,rowBgColor)=when(position%2 != 0){
        true->  AppTheme.colors.listRowBorderColor to AppTheme.colors.listRowBgColor
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
fun CommonListRow(product: Product,currencySymbol: String){
    val appState = LocalAppState.current
    val textStyle=if(appState.isPortrait)
        AppTheme.typography.bodyMedium()
      else
        AppTheme.typography.titleMedium()

    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding20

    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)

    ){
        //SKU
        ListText(
            label = product.productCode?.uppercase()?:"",
            textStyle = textStyle,
            color = AppTheme.colors.textDarkGrey,
            modifier = Modifier.weight(1.2f)
        )

        //barCode
        ListText(
            label = product.barcode?:"",
            textStyle = textStyle,
            color = AppTheme.colors.textDarkGrey.copy(alpha = .8f),
            modifier = Modifier.weight(1.2f)
        )

        ListText(
            label = "$currencySymbol${product.price}",
            textStyle = textStyle,
            color = AppTheme.colors.textDarkGrey,
            modifier = Modifier.weight(1f)
        )


        ListText(
            label = "${product.qtyOnHand}",
            textStyle = textStyle,
            color = AppTheme.colors.textDarkGrey,
            modifier = Modifier.weight(1f)
        )

        if(!appState.isPortrait){
            ListText(
                label = product.name?:"",
                textStyle =textStyle,
                color = AppTheme.colors.textBlack,
                modifier = Modifier.weight(1.5f)
            )
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
               innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
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
           }
       )
   }
}

//Right content
/*Column(modifier = Modifier
.wrapContentHeight()
.wrapContentWidth()
.padding(horizontal = if(appState.isPortrait) AppTheme.dimensions.phoneHorPadding else AppTheme.dimensions.tabHorPadding),
verticalArrangement = Arrangement.spacedBy(10.dp),
horizontalAlignment = Alignment.End
) {

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically) {

        Column(modifier = Modifier
            .wrapContentWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            BottomTex(label = stringResource(Res.string.qty_value,"${posUIState.quantityTotal}"))
            BottomTex(label = stringResource(Res.string.items_value,posViewModel.formatPriceForUI(posUIState.cartTotalWithoutDiscount)))

            TexWithClickableBg(onClick = {
                //open discount pad
                posViewModel.onTotalDiscountItemClick()

            }){
                BottomTex(label = stringResource(Res.string.discount_value, posViewModel.getDiscountValue()), color = AppTheme.colors.appWhite)
            }

            BottomTex(label = stringResource(Res.string.tax_value,posViewModel.formatPriceForUI(posUIState.globalTax?:0.0)))

            BottomTex(label = stringResource(Res.string.total_value,posViewModel.formatPriceForUI(posUIState.grandTotal)))

        }



    }
}*/



