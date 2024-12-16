package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.products.AnimatedProductCard
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.AppCartButton
import com.lfssolutions.retialtouch.presentation.ui.common.AppPrimaryButton
import com.lfssolutions.retialtouch.presentation.ui.common.BottomTex
import com.lfssolutions.retialtouch.presentation.ui.common.CartListItem
import com.lfssolutions.retialtouch.presentation.ui.common.CashierBasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.CategoryListItem
import com.lfssolutions.retialtouch.presentation.ui.common.CustomSwitch
import com.lfssolutions.retialtouch.presentation.ui.common.ListCenterText
import com.lfssolutions.retialtouch.presentation.ui.common.ListText
import com.lfssolutions.retialtouch.presentation.ui.common.ProductItemAnimation
import com.lfssolutions.retialtouch.presentation.ui.common.ProductListItem
import com.lfssolutions.retialtouch.presentation.ui.common.SearchableTextWithBg
import com.lfssolutions.retialtouch.presentation.ui.common.VectorIcons
import com.lfssolutions.retialtouch.presentation.ui.common.fillScreenHeight
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import com.outsidesource.oskitcompose.lib.ValRef
import com.outsidesource.oskitcompose.lib.rememberValRef
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.discount_value
import retailtouch.composeapp.generated.resources.hash
import retailtouch.composeapp.generated.resources.held_tickets
import retailtouch.composeapp.generated.resources.hold_sale
import retailtouch.composeapp.generated.resources.ic_add_member
import retailtouch.composeapp.generated.resources.ic_grid
import retailtouch.composeapp.generated.resources.ic_list
import retailtouch.composeapp.generated.resources.ic_promotion
import retailtouch.composeapp.generated.resources.ic_resetcart
import retailtouch.composeapp.generated.resources.img_empty_cart
import retailtouch.composeapp.generated.resources.items
import retailtouch.composeapp.generated.resources.no_products_added
import retailtouch.composeapp.generated.resources.payment
import retailtouch.composeapp.generated.resources.price
import retailtouch.composeapp.generated.resources.qty
import retailtouch.composeapp.generated.resources.qty_value
import retailtouch.composeapp.generated.resources.review_cart
import retailtouch.composeapp.generated.resources.rtl
import retailtouch.composeapp.generated.resources.search_items
import retailtouch.composeapp.generated.resources.sub_total
import retailtouch.composeapp.generated.resources.tax_value
import retailtouch.composeapp.generated.resources.total_value


object CashierScreen: Screen {
    @Composable
    override fun Content() {
        CashierUI()
    }
}

@Composable
fun CashierUI(
    posViewModel: SharedPosViewModel = koinInject()
){
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val state by posViewModel.posUIState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        posViewModel.loadCategoryAndMenuItems()
    }

    CashierBasicScreen(
        modifier = Modifier.systemBarsPadding(),
        isScrollable = false,
        contentMaxWidth = Int.MAX_VALUE.dp
    ){
        if(appState.isTablet || appState.isLandScape){
            CompositionLocalProvider(LocalLayoutDirection provides if (state.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                LandscapeCashierScreen(
                    interactorRef = rememberValRef(posViewModel),
                )
            }
        }else{
            PortraitCashierScreen(
                interactorRef = rememberValRef(posViewModel),
            )
         }

        state.animatedProductCard?.let {
            ProductItemAnimation(card = it, viewModel = posViewModel)
        }

        SnackbarHost(
            hostState = snackbarHostState.value,
            modifier = Modifier
                .align(Alignment.TopCenter)

        )
    }
}

@Composable
fun LandscapeCashierScreen(interactorRef: ValRef<SharedPosViewModel>) {

    val state by interactorRef.value.posUIState.collectAsStateWithLifecycle()
    val viewModel =interactorRef.value
    var selectedCatId by remember { mutableStateOf(state.selectedCategoryId) }
    val categoryListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    println("categories -${state.categories} | menuItems - ${state.menuProducts}")

    Row(modifier = Modifier
        .fillMaxWidth()
        .fillScreenHeight()
    ) {

        //Category and Items Part
        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(horizontal = AppTheme.dimensions.padding10, vertical = AppTheme.dimensions.padding10),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ){
                //Top Search Bar
                SearchableTextWithBg(
                    value = state.searchQuery,
                    leadingIcon = AppIcons.searchIcon,
                    placeholder = stringResource(Res.string.search_items),
                    label = stringResource(Res.string.search_items),
                    modifier = Modifier.weight(0.6f).wrapContentHeight(),
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

                RtlView(
                    isRtl = state.isRtl,
                    onChange = { viewModel.updateScreenDirection(isRtl = it) }
                )

                ListGridSwitch(
                    isList = state.isList,
                    onClick = { viewModel.updateMenuDisplayFormat(isList = it) }
                )

            }


            LazyRow(
                state=categoryListState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                itemsIndexed(state.categories, key = { index,item -> item.id }) {index, category ->
                    CategoryListItem(
                        name = category.name,
                        isSelected = if (selectedCatId ==-1) {index==0} else {
                            category.id == selectedCatId
                        },
                        onClick = {
                            selectedCatId = category.id
                            viewModel.selectCategory(category.id)
                            coroutineScope.launch {
                                categoryListState.animateScrollToItem(index)
                            }
                        }
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(if (state.isList) 1 else state.gridColumnCount),
                modifier = Modifier.padding(vertical = AppTheme.dimensions.padding5),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = 60.dp
                )
            ) {
                val updatedMenus=state.menuProducts.filter {it.categoryId==state.selectedCategoryId }
                items(updatedMenus,   key = { item -> item.id?:0 }) { product ->
                    ProductListItem(
                        product = product,
                        isList = state.isList,
                        viewModel=viewModel,
                        onClick = {
                            viewModel.onProductItemClick(
                                AnimatedProductCard(
                                    product = product
                                )
                            )
                        }
                    )
                }
            }
        }

        //Cart Value Part
        Column(
            modifier = Modifier
                .weight(0.3f)
                .fillScreenHeight()
                .background(AppTheme.colors.secondaryBg)
        ){
            CartView(interactorRef)
         }
    }

}

@Composable
fun CartView(interactorRef: ValRef<SharedPosViewModel>) {
    val state by interactorRef.value.posUIState.collectAsStateWithLifecycle()
    val viewModel =interactorRef.value
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current

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
                    .weight(1f)
                    .padding(start = AppTheme.dimensions.padding15),
                style = AppTheme.typography.header().copy(fontSize = 20.sp),
                color = AppTheme.colors.primaryText
            )

            CartHeaderImageButton(
                icon = Res.drawable.ic_resetcart,
                onClick = {
                    viewModel.updateClearCartDialogVisibility(state.cartList.isNotEmpty())
                }
            )

            CartHeaderImageButton(
                icon = Res.drawable.ic_promotion,
                isPrimary = true,
                onClick = {

                }
            )

            CartHeaderImageButton(
                icon = Res.drawable.ic_add_member,
                onClick = {

                }
            )

        }

        if (state.cartList.isEmpty()) {
            EmptyCartForm(modifier = Modifier.weight(1f))
        }else{
           val textStyleHeader=AppTheme.typography.bodyBold()
           val btnStyle=AppTheme.typography.captionBold()

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

            LazyColumn(modifier = Modifier.weight(1f)){
                itemsIndexed(state.cartList
                ){index, product ->
                    CartListItem(
                        index = index,
                        item = product,
                        isPortrait = true,
                        horizontalPadding=AppTheme.dimensions.padding10,
                        verticalPadding=AppTheme.dimensions.padding10,
                        posViewModel=viewModel
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
                        label = stringResource(Res.string.discount_value,":"),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.appRed)

                    BottomTex(
                        label = viewModel.formatPriceForUI(state.cartItemsDiscount+state.cartPromotionDiscount),
                        textStyle = textStyleHeader,
                        color = AppTheme.colors.appRed)
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
                        isVisible = state.cartList.isNotEmpty(),
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
                        isVisible = state.cartList.isNotEmpty(),
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
}


@Composable
private fun RtlView(
    isRtl: Boolean,
    onChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .height(45.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = AppTheme.colors.cardBorder,
                shape = RoundedCornerShape(4.dp)
            )
            .background(AppTheme.colors.secondaryBg)
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(Res.string.rtl),
            style = AppTheme.typography.bodyMedium(),
            color = AppTheme.colors.textDarkGrey
        )

        CustomSwitch(
            checked = isRtl,
            onCheckedChange = onChange,
            width = 30.dp,
            height = 15.dp
        )
    }
}


@Composable
private fun ListGridSwitch(
    isList: Boolean = false,
    onClick: (Boolean) -> Unit = {},
) {
    val (listBg, listTxt)=when(isList){
        true->{ AppTheme.colors.textPrimary to AppTheme.colors.appWhite }
        false->{AppTheme.colors.appWhite to AppTheme.colors.textPrimary }
    }

    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(modifier = Modifier.size(45.dp).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { if (isList) onClick(false) }
           ),
            colors = CardDefaults.cardColors(containerColor = listTxt, contentColor = listBg),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
        ){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Image(
                    painter = painterResource(Res.drawable.ic_grid),
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimensions.standerIcon),
                    colorFilter = ColorFilter.tint(listBg),
                    contentScale = ContentScale.FillHeight
                )
            }

        }


        Card(modifier = Modifier.size(45.dp).padding(start = 1.dp).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { if (!isList) onClick(true) }
        ),
            colors = CardDefaults.cardColors(containerColor = listBg, contentColor = listTxt),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
        ){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Image(
                    painter = painterResource(Res.drawable.ic_list),
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.dimensions.standerIcon),
                    colorFilter = ColorFilter.tint(listTxt),
                    contentScale = ContentScale.FillHeight
                )
            }

        }
    }

}


@Composable
private fun PortraitCashierScreen(
    interactorRef: ValRef<SharedPosViewModel>,
) {
    val state by interactorRef.value.posUIState.collectAsStateWithLifecycle()
    val viewModel =interactorRef.value
    var selectedCatId by remember { mutableStateOf(state.selectedCategoryId) }
    val categoryListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadTotal()
    }

    Box(
        modifier = Modifier.fillMaxWidth()
            .fillScreenHeight()
            .padding(
                start = 5.dp,
                top = 8.dp,
                end = 5.dp
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    //Top Search Bar
                    SearchableTextWithBg(
                        value = state.searchQuery,
                        leadingIcon = AppIcons.searchIcon,
                        placeholder = stringResource(Res.string.search_items),
                        label = stringResource(Res.string.search_items),
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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

                LazyRow(
                    state=categoryListState,
                    modifier = Modifier.fillMaxWidth().padding(vertical = AppTheme.dimensions.padding10, horizontal = AppTheme.dimensions.padding5),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    itemsIndexed(state.categories, key = { index,item -> item.id }) {index, category ->
                        CategoryListItem(
                            name = category.name,
                            isSelected = if (selectedCatId ==-1) {index==0} else {
                                category.id == selectedCatId
                            },
                            onClick = {
                                selectedCatId = category.id
                                viewModel.selectCategory(category.id)
                                coroutineScope.launch {
                                    categoryListState.animateScrollToItem(index)
                                }
                            }
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = 60.dp
                )
            ) {
                val updatedMenus=state.menuProducts.filter {it.categoryId==state.selectedCategoryId }
                items(updatedMenus,key = { item -> item.id!! }) { product ->
                    ProductListItem(
                        product = product,
                        viewModel = viewModel,
                        onClick = { product: AnimatedProductCard ->
                            viewModel.showAnimatedProductCard(product)
                            viewModel.onProductItemClick(product)
                        }
                    )
                }
            }
        }

        AppCartButton(
            onClick = {
                //viewModel.showCartScreen()
            } ,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(10.dp),
            text = "VIEW CART: $${state.cartValue}",
        )
    }
}


@Composable
private fun CartHeaderImageButton(
    icon: DrawableResource,
    isPrimary:Boolean=false,
    onClick: () -> Unit = {}
) {
    Card(modifier = Modifier.size(40.dp).clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick),
        colors = CardDefaults.cardColors(if(isPrimary)AppTheme.colors.textPrimary else AppTheme.colors.textLightGrey),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = AppTheme.appShape.card

    )
    {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(AppTheme.dimensions.smallXIcon)
            )
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