package com.lfssolutions.retialtouch.presentation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lfssolutions.retialtouch.domain.model.productWithTax.HeldCollection
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.navigation.NavigatorActions
import com.lfssolutions.retialtouch.presentation.ui.common.ActionDialog
import com.lfssolutions.retialtouch.presentation.ui.common.AppCircleProgressIndicator
import com.lfssolutions.retialtouch.presentation.ui.common.AppDialog
import com.lfssolutions.retialtouch.presentation.ui.common.AppHorizontalDivider
import com.lfssolutions.retialtouch.presentation.ui.common.AppLeftSideMenu
import com.lfssolutions.retialtouch.presentation.ui.common.AppOutlinedSearch
import com.lfssolutions.retialtouch.presentation.ui.common.BottomTex
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonCard
import com.lfssolutions.retialtouch.presentation.ui.common.ButtonRowCard
import com.lfssolutions.retialtouch.presentation.ui.common.CreateMemberDialog
import com.lfssolutions.retialtouch.presentation.ui.common.CreateMemberForm
import com.lfssolutions.retialtouch.presentation.ui.common.DiscountDialog
import com.lfssolutions.retialtouch.presentation.ui.common.ListItemText
import com.lfssolutions.retialtouch.presentation.ui.common.MemberList
import com.lfssolutions.retialtouch.presentation.ui.common.MemberListDialog
import com.lfssolutions.retialtouch.presentation.ui.common.NumberPad
import com.lfssolutions.retialtouch.presentation.ui.common.QtyItemText
import com.lfssolutions.retialtouch.presentation.ui.common.SearchableTextFieldWithDialog
import com.lfssolutions.retialtouch.presentation.ui.common.TexWithClickableBg
import com.lfssolutions.retialtouch.presentation.ui.common.VectorIcons
import com.lfssolutions.retialtouch.presentation.viewModels.PosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DiscountType
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import org.jetbrains.compose.resources.DrawableResource
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
    posViewModel: PosViewModel = koinInject()
){
    val navigator = LocalNavigator.currentOrThrow
    val posUIState by posViewModel.posUIState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        posViewModel.loadDataFromDatabases()
    }

    LaunchedEffect(posUIState.isInsertion) {
        posViewModel.fetchUIProductList()
    }

    LaunchedEffect(posUIState.uiPosList) {
        // This will trigger whenever uiPosList changes
        println("UI Pos List Updated: ${posUIState.uiPosList}")
        posViewModel.calculateBottomValues()
    }



    SearchableTextFieldWithDialog(
        isVisible = posUIState.showDialog,
        query = posUIState.searchQuery,
        onQueryChange = { newQuery ->
            println("search value: $newQuery")
            posViewModel.updateSearchQuery(newQuery)
        },
        onDismiss = {
            posViewModel.updateDialogState(false)
        },
        content = {
            DialogList(posUIState.dialogPosList,posUIState.searchQuery,posUIState.currencySymbol, onClick = {selectedItem->
                posViewModel.updateDialogState(false)
                println("selectedItem : $selectedItem")
                posViewModel.insertPosListItem(selectedItem)
            })
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


    AppLeftSideMenu(
        modifier = Modifier.fillMaxSize(),
        onMenuItemClick = {

            //navigator.handleNavigation(it)
        },
        content = {
            //for POS Screen
            PosTopContent(posUIState,posViewModel,onNavigatePayment={
               // navigator.navigateToPaymentType(posUIState.selectedMemberId,posUIState.grandTotal)
                NavigatorActions.navigateToPaymentScreen(navigator,posUIState.selectedMemberId,posUIState.grandTotal)
            })


        },
        holdSaleContent = {
            HoldSaleContent(posUIState,posViewModel)
        }
    )
}

@Composable
fun PosTopContent(posUIState: PosUIState, posViewModel: PosViewModel, onNavigatePayment:  (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier=Modifier
            .fillMaxHeight()
            .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //Top Search Bar
            AppOutlinedSearch(
                value = posUIState.searchQuery,
                onValueChange = { posViewModel.updateSearchQuery(it) },
                placeholder = stringResource(Res.string.search),
                leadingIcon = AppIcons.searchIcon,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                onSearchClick = { resultVal->
                    posViewModel.updateDialogState(resultVal)
                })

            //Select Member
            showMemberCard(posUIState,posViewModel)

            // Wrapping both header and LazyColumn inside a Row with horizontal scroll
            POSTaxListView(posUIState,posViewModel)


        }

        AppCircleProgressIndicator(
            isVisible=posUIState.isLoading
        )

        //Bottom Row Calculation
        BottomContent(modifier  = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .horizontalScroll(rememberScrollState())
            .align(Alignment.BottomStart),
            posUIState=posUIState,
            posViewModel=posViewModel,
            onPaymentClick = {
                onNavigatePayment(posUIState.selectedMemberId)
            }
        )
    }
}

@Composable
fun HoldSaleContent(posUIState: PosUIState,posViewModel: PosViewModel){
    if(posUIState.isHoldSaleDialog){
        Column(modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            val collectionList = posUIState.holdSaleCollections.entries.toList()

            if(collectionList.size>1){
                //Header
                ButtonRowCard(
                    label = "${posUIState.holdSaleCollections.size}",
                    icons = AppIcons.downArrowIcon,
                    iconSize = 28.dp,
                    innerPaddingValues = PaddingValues(horizontal = 40.dp, vertical = 20.dp),

                    isDropdownExpanded = posUIState.isDropdownExpanded,
                    onClick = {
                        posViewModel.onToggleChange()
                    }
                )
            }
            //List
            if(posUIState.isDropdownExpanded){
                showHoldSaleList(posUIState,collectionList,posViewModel)
            }else if(collectionList.size==1){
                showHoldSaleList(posUIState,collectionList,posViewModel)
            }
        }
    }
}

@Composable
fun showMemberCard(posUIState: PosUIState, posViewModel: PosViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable{
                posViewModel.updateMemberDialogState(true)
            },
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.backgroundDialog),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(5.dp)
    ){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Text(
                modifier = Modifier.wrapContentHeight().weight(1f).padding(10.dp),
                text = posUIState.selectedMember,
                style = AppTheme.typography.bodyMedium(),
                color = AppTheme.colors.textColor
            )

            Icon(
                modifier = Modifier.size(AppTheme.dimensions.smallIcon),
                imageVector = vectorResource(AppIcons.downArrowIcon),
                contentDescription = "",
                tint = AppTheme.colors.textColor
            )

        }
    }
}

@Composable
fun POSTaxListView(posUIState: PosUIState, posViewModel: PosViewModel) {
    var width by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val widthDp = remember(width, density) { with(density) { width.toDp() } }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 10.dp)
    ){
        Column(modifier = Modifier.fillMaxSize(),
        ) {

            //List UI Header
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ){
                ListItemText(label = stringResource(Res.string.hash), modifier = Modifier.width(30.dp))
                ListItemText(label = stringResource(Res.string.items), modifier = Modifier.width(150.dp))
                ListItemText(label = stringResource(Res.string.price), modifier = Modifier.width(100.dp))
                ListItemText(label = stringResource(Res.string.qty), modifier = Modifier.width(150.dp))
                ListItemText(label = stringResource(Res.string.sub_total), modifier = Modifier.width(100.dp))
                VectorIcons(icons = AppIcons.removeIcon, modifier = Modifier.width(AppTheme.dimensions.smallIcon), onClick = {
                    posViewModel.updateRemoveDialogState(posUIState.uiPosList.isNotEmpty())
                })
            }

            //List Content
            LazyColumn( modifier = Modifier.onSizeChanged {
                width = it.width
            }) {
                var index=0
                items(posUIState.uiPosList
                ){ product ->
                    AppHorizontalDivider(modifier=Modifier.width(widthDp))
                    index+=1
                    POSTaxItem(
                        index =index,product,posUIState.currencySymbol,
                        onPriceClick = { selectedItem->
                            posViewModel.onPriceItemClick(selectedItem)
                        },
                        onQtyChanged = { selectedItem, isIncrease->
                            posViewModel.updateQty(selectedItem,isIncrease)
                        },
                        onRemoveClick = { selectedItem->
                            posViewModel.removedListItem(selectedItem)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun POSTaxItem(
    index:Int, product: ProductTaxItem,
    currencySymbol: String,
    onPriceClick: (ProductTaxItem) -> Unit,
    onQtyChanged: (ProductTaxItem,Boolean) -> Unit,
    onRemoveClick: (ProductTaxItem) -> Unit,
)
{
    val (textColor, textTotalLabel) = when {
        product.discount > 0.0 -> {
            AppTheme.colors.textError to "$currencySymbol${product.subtotal?.roundTo()}\n (-${product.discount}$currencySymbol)"
        }
        else -> {
            AppTheme.colors.textColor.copy(alpha = 0.8f) to "$currencySymbol${product.subtotal?.roundTo()}"
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {


        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            //Hash
            ListItemText(
                label = "$index",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(30.dp).padding(vertical = 10.dp)
            )

            //Items
            ListItemText(
                label = "${product.name?:""} $\n [${product.inventoryCode?:""}]",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(150.dp),
                singleLine = false
            )


            //Price
            ListItemText(
                label = "$currencySymbol${product.price?.roundTo()}",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(100.dp),
                isButton = true,
                onButtonClick = {
                    onPriceClick.invoke(product)
                }
            )

            //Qty
            QtyItemText(
                label = "${product.qtyOnHand}",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(160.dp),
                onIncreaseClick = {
                    onQtyChanged.invoke(product,true)
                },
                onDecreaseClick = {
                    onQtyChanged.invoke(product,false)
                }
            )

            //Subtotal
            ListItemText(
                label = textTotalLabel,
                textStyle = AppTheme.typography.titleMedium(),
                color = textColor,
                modifier = Modifier.width(100.dp).wrapContentHeight()
            )
            /*Column(modifier = Modifier.width(100.dp).wrapContentHeight(), horizontalAlignment = Alignment.CenterHorizontally) {

                if(product.discount>0){
                    ListItemText(
                        label = "$currencySymbol${product.subtotal?.roundTo()}",
                        textStyle = AppTheme.typography.bodyMedium(),
                        color = AppTheme.colors.textColor.copy(alpha = .8f),
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }*/

            //modifier icons
            VectorIcons(icons = AppIcons.closeIcon,
                modifier = Modifier.width(AppTheme.dimensions.smallIcon),
                onClick = {
                    onRemoveClick(product)
                }
            )

        }

    }

}

@Composable
fun BottomContent(modifier: Modifier, posUIState: PosUIState, posViewModel: PosViewModel,onPaymentClick:  () -> Unit){
    Row(modifier = modifier) {

        //Item Discount
        Column(modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {

            BottomTex(
                label = stringResource(Res.string.items_discount,"${posUIState.currencySymbol} ${posUIState.itemsDiscount.roundTo()}")
            )

            //Hold Button
            if((posUIState.uiPosList.isNotEmpty() && posUIState.uiPosList.size>1) || posUIState.holdSaleCollections.isNotEmpty()){
                ButtonCard(
                    label = stringResource(Res.string.hold_sale),
                    icons = AppIcons.pauseIcon,
                    backgroundColor = AppTheme.colors.searchBoxColor,
                    onClick = {
                        //on Hold Button click
                        posViewModel.onHoldClicked()
                    }
                )
            }
        }


        //Right content
        Column(modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {

            BottomTex(
                label = stringResource(Res.string.promo_discount,"${posUIState.currencySymbol} ${posUIState.promoDiscount.roundTo()}")
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier
                    .wrapContentWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    BottomTex(label = stringResource(Res.string.qty_value,"${posUIState.totalQty}"))
                    BottomTex(label = stringResource(Res.string.items_value,"${posUIState.currencySymbol} ${posUIState.subTotal.roundTo()}"))

                    TexWithClickableBg(onClick = {
                        //open discount pad
                        posViewModel.onTotalDiscountItemClick()

                    }){
                        BottomTex(label = stringResource(Res.string.discount_value, posViewModel.getDiscountValue()), color = AppTheme.colors.textWhite)
                    }

                    BottomTex(label = stringResource(Res.string.tax_value,"${posUIState.currencySymbol} ${posUIState.totalTax.roundTo()}"))

                    BottomTex(label = stringResource(Res.string.total_value,"${posUIState.currencySymbol} ${posUIState.grandTotal.roundTo()}"))

                }

                Spacer(modifier = Modifier.width(10.dp))

                //Payment Button
                ButtonCard(
                    label = stringResource(Res.string.payment),
                    icons = AppIcons.paymentIcon,
                    backgroundColor = AppTheme.colors.appGreen,
                    isEnabled = posUIState.uiPosList.isNotEmpty(),
                    onClick = {
                        onPaymentClick.invoke()
                    }
                )
            }
        }
    }
}


@Composable
fun showHoldSaleList(posUIState: PosUIState, collectionList: List<MutableMap.MutableEntry<Int, HeldCollection>>, posViewModel: PosViewModel) {
    LazyColumn(modifier = Modifier.weight(1f)) {
        items(
            collectionList
        ){ entry ->
            // entry.key is the ID, and entry.value is the HeldCollection
            HoldSaleCollectionItem(currencySymbol=posUIState.currencySymbol,collectionId = entry.key, collection = entry.value, onClick = {collection->

                posViewModel.getListFromHoldSale(collection)
            })
        }
    }
}

@Composable
fun HoldSaleCollectionItem(
    currencySymbol: String,
    collectionId: Int,
    collection: HeldCollection,
    onClick: (HeldCollection) -> Unit
) {

    Column(modifier = Modifier.wrapContentHeight().wrapContentWidth().padding(vertical = 10.dp)) {
        ButtonCard(
            modifier = Modifier.wrapContentHeight().wrapContentWidth(),
            label ="$currencySymbol ${collection.grandTotal.roundTo()}",
            discountType = "# ${collection.collectionId}",
            backgroundColor = AppTheme.colors.appGreen,
            innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            onClick = {
                onClick(collection)
            }
        )
    }
}


@Composable
fun DialogList(productTaxList: List<ProductTaxItem>, searchQuery: String,currencySymbol: String,onClick: (ProductTaxItem) -> Unit) {
    var width by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val widthDp = remember(width, density) { with(density) { width.toDp() } }

     Surface(modifier = Modifier.fillMaxSize(),
         shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
         border = BorderStroke(width = 1.dp, color = AppTheme.colors.listBorderColor)
     ){
         Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
             Column(modifier = Modifier.fillMaxWidth()) {

                 Row(modifier = Modifier.fillMaxWidth(),
                     verticalAlignment = Alignment.CenterVertically,
                     horizontalArrangement = Arrangement.Center
                 ){

                     // Adjust the weight proportions
                     ListItemText(label = stringResource(Res.string.sku), modifier = Modifier.width(100.dp))
                     ListItemText(label = stringResource(Res.string.barcode), modifier = Modifier.width(100.dp))
                     ListItemText(label = stringResource(Res.string.price), modifier = Modifier.width(100.dp))
                     ListItemText(label = stringResource(Res.string.in_stock), modifier = Modifier.width(100.dp))
                     ListItemText(label = stringResource(Res.string.description), modifier = Modifier.wrapContentWidth())

                 }



                 // Display filtered products in a LazyColumn
                 LazyColumn(modifier = Modifier.onSizeChanged {
                     width=it.width
                 }){
                     // Filter the product tax list based on the search query
                     val filteredProducts = productTaxList.filter { product ->
                         (searchQuery.isEmpty() || product.name?.contains(searchQuery, ignoreCase = true) == true ||
                                 product.barCode?.contains(searchQuery, ignoreCase = true) == true ||
                         product.inventoryCode?.contains(searchQuery, ignoreCase = true) == true)
                     }
                     items(filteredProducts){ product ->
                         AppHorizontalDivider(modifier = Modifier.width(widthDp))
                         POSItem(product,currencySymbol, onClick = { selectedItem->
                             onClick(selectedItem)
                         })
                     }
                 }
             }
         }

     }

}

@Composable
fun POSItem(product: ProductTaxItem, currencySymbol: String, onClick: (ProductTaxItem) -> Unit) {

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth().clickable{onClick(product)},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            //SKU
            ListItemText(
                label = product.inventoryCode?:"",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(100.dp)
            )

            //barCode
            ListItemText(
                label = product.barCode?:"",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(100.dp)
            )

            ListItemText(
                label = "$currencySymbol${product.price}",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(100.dp)
            )


            ListItemText(
                label = "${product.qtyOnHand}",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.width(100.dp)
            )

            ListItemText(
                label = product.name?:"",
                textStyle = AppTheme.typography.titleMedium(),
                color = AppTheme.colors.textColor.copy(alpha = .8f),
                modifier = Modifier.wrapContentWidth()
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



