package com.hashmato.retailtouch.presentation.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.domain.model.products.AnimatedProductCard
import com.hashmato.retailtouch.navigation.NavigatorActions
import com.hashmato.retailtouch.presentation.common.AppCartButton
import com.hashmato.retailtouch.presentation.common.dialogs.CartLoader
import com.hashmato.retailtouch.presentation.common.CashierBasicScreen
import com.hashmato.retailtouch.presentation.common.CategoryListItem
import com.hashmato.retailtouch.presentation.common.CustomSwitch
import com.hashmato.retailtouch.presentation.common.ProductItemAnimation
import com.hashmato.retailtouch.presentation.common.ProductListItem
import com.hashmato.retailtouch.presentation.common.SearchableTextWithBg
import com.hashmato.retailtouch.presentation.common.dialogs.StockDialog
import com.hashmato.retailtouch.presentation.common.fillScreenHeight
import com.hashmato.retailtouch.presentation.viewModels.SharedPosViewModel
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.lib.ValRef
import com.outsidesource.oskitcompose.lib.rememberValRef
import com.outsidesource.oskitcompose.router.KMPBackHandler
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.ic_grid
import retailtouch.composeapp.generated.resources.ic_list
import retailtouch.composeapp.generated.resources.rtl
import retailtouch.composeapp.generated.resources.search_items


object CashierScreen: Screen {
    @Composable
    override fun Content() {
        CashierUI()
    }
}

@Composable
fun CashierUI(
    viewModel: SharedPosViewModel = koinInject()
){
    val navigator = LocalNavigator.currentOrThrow
    val appState = LocalAppState.current
    val snackbarHostState = remember { mutableStateOf(SnackbarHostState()) }
    val state by viewModel.posUIState.collectAsStateWithLifecycle()

    KMPBackHandler(true, onBack = {
        NavigatorActions.navigateBackToHomeScreen(navigator,false)
    })

    LaunchedEffect(Unit){
        viewModel.loadCategoryAndMenuItems()
        viewModel.loadDbData()
    }


    CartLoader(
        isVisible = state.showCartLoader
    )

    CashierBasicScreen(
        modifier = Modifier.systemBarsPadding(),
        isScrollable = false,
        contentMaxWidth = Int.MAX_VALUE.dp
    ){
        if(appState.isTablet || appState.isLandScape){
            CompositionLocalProvider(LocalLayoutDirection provides if (state.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
                LandscapeCashierScreen(
                    interactorRef = rememberValRef(viewModel),
                )
            }
        }else{
            PortraitCashierScreen(
                interactorRef = rememberValRef(viewModel),
            )
         }

        state.animatedProductCard?.let {
            ProductItemAnimation(card = it, viewModel = viewModel)
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
}

@Composable
fun LandscapeCashierScreen(interactorRef: ValRef<SharedPosViewModel>) {

    val state by interactorRef.value.posUIState.collectAsStateWithLifecycle()
    val viewModel =interactorRef.value
    var selectedCatId by remember { mutableStateOf(state.selectedCategoryId) }
    val categoryListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    //println("categories -${state.categories} | menuItems - ${state.menuProducts}")

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
private fun PortraitCashierScreen(
    interactorRef: ValRef<SharedPosViewModel>,
) {
    val viewModel =interactorRef.value
    val state by viewModel.posUIState.collectAsStateWithLifecycle()
    var selectedCatId by remember { mutableStateOf(state.selectedCategoryId) }
    val categoryListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow

    println("categories -${state.categories} | menuItems - ${state.menuProducts}")

    LaunchedEffect(Unit) {
        viewModel.loadTotal()
    }


    Box(
        modifier = Modifier.fillMaxWidth()
            .fillScreenHeight()
            .padding(
                start = 10.dp,
                top = 10.dp,
                end = 10.dp
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = AppTheme.dimensions.padding10),
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
                items(updatedMenus,key = { item -> item.id }) { product ->
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
                NavigatorActions.navigateToCartScreen(navigator)
            } ,
            enabled = state.cartList.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(10.dp),
            text = "VIEW CART: ${viewModel.formatPriceForUI(state.cartValue)}",
        )
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


