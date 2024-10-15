package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.productWithTax.HeldCollection
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DiscountApplied
import com.lfssolutions.retialtouch.utils.DiscountType
import com.lfssolutions.retialtouch.utils.DoubleExtension.calculatePercentage
import com.lfssolutions.retialtouch.utils.DoubleExtension.calculatePercentageByValue
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.koin.core.component.KoinComponent
import kotlin.math.absoluteValue

class SharedPosViewModel : BaseViewModel(), KoinComponent {

    private val _posUIState = MutableStateFlow(PosUIState())
    val posUIState: StateFlow<PosUIState> = _posUIState.asStateFlow()

    fun fetchUIProductList(){
        viewModelScope.launch(Dispatchers.IO){
            dataBaseRepository.getScannedProduct().collectLatest { list->
                withContext(Dispatchers.Main) {
                    _posUIState.update {
                        it.copy(shoppingCart = list)
                    }
                }
            }
        }
    }

    fun getAuthDetails(){
        viewModelScope.launch {
            authUser.collectLatest { authDetails->
                if(authDetails!=null){
                    _posUIState.update {
                        it.copy(
                            isSalesTaxInclusive = authDetails.loginDao.salesTaxInclusive?:false,
                            posInvoiceRounded=authDetails.loginDao.posInvoiceRounded
                        )
                    }
                }
            }
        }
    }

    // Optionally fetch and print products in the ViewModel
    fun loadDialogProduct() {
        viewModelScope.launch {
            productsList.collectLatest { productDaoList ->
                if(productDaoList.isNotEmpty()){
                   val productList= productDaoList.map { item->
                      item.rowItem
                    }
                    _posUIState.update { it.copy(dialogPosList=productList)}
                     println("ProductList: $productList")
                }
            }
        }
    }

    fun loadMemberList(){
        viewModelScope.launch {
            // Collect member list
            memberList.collectLatest { members ->
                if(members.isNotEmpty()){
                    println("memberList: $members")
                    _posUIState.update { state ->
                        state.copy(memberList = members.map { it.rowItem})
                    }
                }
            }
        }
    }

    fun loadDataFromDatabases() {
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading to true at the start
            updateLoader(true)
            try {
                // Load data from multiple databases concurrently
                //val data1Deferred = async { getCurrencySymbol() }
                //val data2Deferred = async { dataBaseRepository.getProducts()}
               // val data3Deferred = async { dataBaseRepository.getAllMembers()}
                //val data4Deferred = async { dataBaseRepository.getAllMemberGroup()}

                // Await all results
                //val currencySymbol = data1Deferred.await()
                //val productTaxDao = data2Deferred.await()
                //val membersDao = data3Deferred.await()
                //val memberGroupDao = data4Deferred.await()

                // Update your state directly with responses
                currencySymbol.collectLatest {
                    _posUIState.update { currentState->
                        currentState.copy(currencySymbol = it)
                    }
                }

                productsList.collectLatest {itemDao->
                    println("productDao: $itemDao")
                    val transformedList = itemDao?.map { item ->
                        println("rowItem: ${item.rowItem}")
                        item.rowItem
                    }?: emptyList()
                    _posUIState.update {
                        it.copy(dialogPosList = transformedList)
                    }
                }


                /*membersDao.collectLatest { memberList ->
                    val transformedList = memberList.map { memberDao ->
                        // Access the rowItem and map it to the desired format
                        memberDao.rowItem
                    }
                    _posUIState.update {
                        it.copy(memberList = transformedList)
                    }
                }

                memberGroupDao.collectLatest { memberList ->
                    val transformedList = memberList.map { memberDao ->
                        memberDao.rowItem
                    }

                    if(transformedList.isNotEmpty()){
                        _posUIState.update {
                            it.copy( selectedMemberGroup= transformedList[0].name?:"", selectedMemberGroupId = transformedList[0].id)
                        }
                    }
                    _posUIState.update {
                        it.copy(memberGroupList = transformedList)
                    }
                }*/

                updateLoader(false)

            } catch (e: Exception) {
                // Handle errors, ensuring loading state is false
                updateLoader(false)
            }
        }
    }

    fun insertPosListItem(item: ProductTaxItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val qty = if (item.qtyOnHand < 1.0) 1.0 else item.qtyOnHand
            val updatedItem = item.copy(qtyOnHand = qty)
            dataBaseRepository.insertOrUpdateScannedProduct(updatedItem)
            _posUIState.update {it.copy(isCallScannedItems= !it.isCallScannedItems)}
        }
    }

    private fun updateLoader(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(isLoading = value) }
        }
    }

    fun updateDialogState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(showDialog = value) }
        }
    }


    fun updateSearchQuery(query:String){
        viewModelScope.launch {
            _posUIState.update {
                if (isCode(query)) {
                    filterListByCode(query) // Filter by barcode or inventory code
                }
                it.copy(searchQuery = query)
            }
        }
    }

    // Detects if a query is likely a code (numeric and shorter than a typical name)
    private fun isCode(query: String): Boolean {
        return query.all { it.isDigit() } /*&& query.length <= 10 // Adjust length if needed*/
    }

    private fun filterListByCode(query: String) {
        viewModelScope.launch {
            val filteredList = _posUIState.value.dialogPosList.filter {
                it.barCode?.contains(query)==true  || it.inventoryCode?.contains(query) == true
            }
            if (filteredList.isNotEmpty()) {
                insertPosListItem(filteredList[0]) // Add only the first matched item
            }
        }
    }

    fun updateDiscountType(discountType: DiscountType) {
        viewModelScope.launch {
            _posUIState.update { it.copy(selectedDiscountType = discountType) }
        }
    }

    fun updateDiscountValue(discount: String) {
        viewModelScope.launch {
            _posUIState.update { it.copy(inputDiscount = discount) }
        }
    }

    fun updateMemberDialogState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(isMemberDialog = value) }
        }
    }


    fun updateCreateMemberDialogState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(isCreateMemberDialog = value) }
        }
    }

    fun updateRemoveDialogState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(isRemoveDialog = value) }
        }
    }

    fun onSearchMember(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(searchMember = value) }
        }
    }
    fun onSelectedMember(mMemberItem: MemberItem){
        viewModelScope.launch {
            _posUIState.update { it.copy(selectedMember = mMemberItem.name,selectedMemberId=mMemberItem.id, isMemberDialog = false)}
        }
    }

    fun onSelectedMemberGroup(groupItem: MemberGroupItem){
        viewModelScope.launch {
            _posUIState.update { it.copy(selectedMemberGroup = groupItem.name?:"", selectedMemberGroupId=groupItem.id)}
        }
    }

    fun updateMemberCode(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(memberCode = value) }
        }
    }

    fun updateMemberName(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(memberName = value) }
        }
    }

    fun updateEmail(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(email = value) }
        }
    }

    fun updateMobileNo(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(mobileNo = value) }
        }
    }

    fun updateAddress(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(address = value) }
        }
    }

    fun updateZipCode(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(zipCode = value) }
        }
    }


    fun onHoldClicked(){
        viewModelScope.launch(Dispatchers.Default) {

            _posUIState.update { currentState->

                // Create a new HeldCollection
                val newCollection = HeldCollection(
                    collectionId = currentState.currentCollectionId,
                    items = currentState.shoppingCart,
                    grandTotal = currentState.grandTotal
                )

                // Create a new map by copying the old map and adding the new collection
                val updatedMap = currentState.holdSaleCollections.toMutableMap().apply {
                    put(currentState.currentCollectionId, newCollection)
                }


                // Check if the current items are the same as the last held collection
                //val isSameAsLastHeld = updatedMap.values.lastOrNull()?.items == currentState.shoppingCart

                val newCollectionId = if(updatedMap.isNotEmpty()) currentState.currentCollectionId+1 else currentState.currentCollectionId


                // Reset shoppingCart for new entries
                currentState.copy(
                    holdSaleCollections = updatedMap,
                    currentCollectionId = newCollectionId,
                    shoppingCart = listOf(), // Reset after holding
                    isHoldSaleDialog = true
                )
            }
        }
    }

    fun getListFromHoldSale(collection: HeldCollection) {
        viewModelScope.launch {
          _posUIState.update { currentState->

              // Update shoppingCart with the selected collection's items
              val updatedCollections = currentState.holdSaleCollections.toMutableMap()

              updatedCollections.remove(collection.collectionId) // Remove the selected collection

              // Merge the existing items in shoppingCart with the items from the selected collection
              val updatedUiPosList = currentState.shoppingCart + collection.items


              currentState.copy(
                  shoppingCart = updatedUiPosList,
                  holdSaleCollections = updatedCollections,
                  currentCollectionId = if (updatedCollections.isEmpty()) 1 else currentState.currentCollectionId-1
              )

          }
        }
    }

    fun calculateBottomValues(){
        viewModelScope.launch {
            _posUIState.update { currentState ->
               /* var total = 0.0
                var taxTotal = 0.0
                var apiTax = 0.0
                var cartWithoutDiscount = 0.0
                var promoDiscount = 0.0
                var cartItemDiscount = 0.0
                var cartItemPromotionDiscount = 0.0
                var qty:Double=0.0
                currentState.shoppingCart.forEach { item->
                    qty += item.qtyOnHand.absoluteValue
                }*/
                val totalQty = currentState.shoppingCart.sumOf { it.qtyOnHand }
                val totalTax = currentState.shoppingCart.sumOf { it.taxValue ?: 0.0 }
                val itemSubTotal = currentState.shoppingCart.sumOf { it.cartTotal?:0.0 }
                val itemDiscount = currentState.shoppingCart.sumOf { it.itemDiscount }
                val grandTotal = currentState.shoppingCart.sumOf {
                    val subtotal = it.cartTotal ?: 0.0
                    val taxAmount = (it.taxPercentage?.div(100.0)?.times(subtotal)) ?: 0.0
                    subtotal + taxAmount
                }
                currentState.copy(quantityTotal =totalQty, invoiceTax = totalTax, invoiceSubTotal = itemSubTotal, grandTotal = grandTotal , originalTotal = grandTotal,itemsDiscount=itemDiscount)
            }
        }
    }

    fun getFinalPrice(): Double {
        with(_posUIState.value) {
            var amt = if (promotionByQuantity) {
                (amount ?: 0.0) * qty
            } else {
                cprice?.times(qty)
            }

            amt?.let {
                if (amt > 0 || exchange) {
                    val discount= if(inputDiscount.isNotEmpty()) inputDiscount.toDouble() else 0.0
                    if (discount > 0) {
                        if (discountIsInPercent) {
                            if (discount < 100.0) {
                                amt -= (amt * discount) / 100.0
                            }
                        } else if (discount <= amt) {
                            val des = discount * qty
                            amt -= des
                        }
                    }
                    return amt // Apply rounding if needed: posRounding(amt, podRounding)
                }
                else{
                  return 0.0
                }
            }
            return 0.0
        }
    }

    fun updateQty(selectedItem: ProductTaxItem,isQtyIncrease:Boolean) {
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val updatedProductList = currentState.shoppingCart.map { product ->
                    if (product.id == selectedItem.id){
                        val newQty = if (isQtyIncrease) product.qtyOnHand+1 else{ product.qtyOnHand.takeIf { it > 1 }?.minus(1) ?: 1.0}
                        val newSubTotal= product.price?.times(newQty)
                        val taxValue=newSubTotal?.calculatePercentage(product.taxPercentage?:0.0)
                        val finalTotal = newSubTotal?.minus(product.itemDiscount)
                        val updatedProduct = product.copy(qtyOnHand = newQty, cartTotal = finalTotal, originalSubTotal = newSubTotal?:0.0, taxValue = taxValue)
                        dataBaseRepository.updateScannedProduct(updatedProduct)
                        updatedProduct
                    }
                    else product
                }
                currentState.copy(shoppingCart = updatedProductList)
            }

        }
    }

    fun removedListItem(selectedItem: ProductTaxItem) {
        viewModelScope.launch {
            _posUIState.update { currentState ->
                dataBaseRepository.removeScannedItemById(selectedItem.id.toLong())
                currentState.copy(isCallScannedItems = !currentState.isCallScannedItems)
            }
        }
    }

    fun removedScannedItem() {

        viewModelScope.launch {
            _posUIState.update { currentState ->
                currentState.copy(shoppingCart = listOf(), selectedMember = "Select Member", holdSaleCollections = hashMapOf(), isRemoveDialog = false)
            }
        }
    }

    fun onNumberPadClick(symbol: String) {
        with(_posUIState.value){
            when (symbol) {
                "." -> if (!inputDiscount.contains('.')) {
                    updateDiscountValue(("$inputDiscount."))
                }
                "x" -> if (inputDiscount.isNotEmpty()) {
                    val newDiscount = inputDiscount.dropLast(1)
                    updateDiscountValue(newDiscount)
                }
                else -> {
                    val updatedDiscount = (inputDiscount + symbol)
                    updateDiscountValue(updatedDiscount)
                }
            }
        }
    }

    fun onApplyDiscountClick(){
        viewModelScope.launch {
            with(_posUIState.value){
                if(inputDiscount.isEmpty()){
                    inputDiscountError="Enter input amount"
                }else{
                    inputDiscountError=null

                    val totalAmount = when(selectedDiscountApplied){
                        DiscountApplied.TOTAL -> {
                            grandTotal
                        }
                        DiscountApplied.ITEMS -> {
                            itemPriceClickItem.originalSubTotal
                        }
                    }

                    val (discount,discountPer) = when (selectedDiscountType) {
                        DiscountType.PERCENTAGE -> {
                            totalAmount.calculatePercentage(inputDiscount.toDouble()) to inputDiscount.toDouble()
                        }
                        DiscountType.FIXED_AMOUNT -> {
                            //calculate fixed amount
                            inputDiscount.toDouble() to totalAmount.calculatePercentageByValue(inputDiscount.toDouble())
                        }
                    }
                    val finalTotal= totalAmount-discount
                    val adjustedTotal = finalTotal.coerceAtLeast(0.0)
                    println("Item : discount -$discount $discountPer $adjustedTotal")
                    updateFinalDiscount(discount,discountPer,adjustedTotal)
                }
            }
        }
    }

    private fun updateFinalDiscount(discount: Double,discountPercentage:Double,finalValue:Double) {
        viewModelScope.launch {
            _posUIState.update { currentState->
                if(currentState.selectedDiscountApplied==DiscountApplied.TOTAL){
                    if (currentState.isSalesTaxInclusive) {
                        //grandTotalWithoutDiscount.value = total
                    } else {
                       // grandTotalWithoutDiscount.value = total + taxTotal;
                    }

                    currentState.copy(isDiscountDialog = false, grandTotal = finalValue, invoiceNetDiscount = discount, invoiceNetDiscountPerc = discountPercentage)
                }else{
                    val updatedList=currentState.shoppingCart.map { element->
                        if(element.id==currentState.itemPriceClickItem.id){
                            val updatedItem= element.copy(itemDiscount = discount, itemDiscountPerc = discountPercentage, cartTotal = finalValue)
                            dataBaseRepository.updateScannedProduct(updatedItem)
                            updatedItem
                        }else{
                            element
                        }
                    }
                    currentState.copy(isDiscountDialog = false, shoppingCart = updatedList)
                }
            }
        }
    }

    fun getDiscountValue():String{
        with(_posUIState.value){
            return  "${discounts.roundTo()} ${getDiscountTypeSymbol()}"
        }
    }

    fun getDiscountTypeIcon(): DrawableResource {
        with(_posUIState.value){
            return when (selectedDiscountType) {
                DiscountType.PERCENTAGE -> {
                    AppIcons.percentageIcon}
                DiscountType.FIXED_AMOUNT -> {
                    AppIcons.dollarIcon
                }
            }
        }
    }

    private fun getDiscountTypeSymbol(): String {
        with(_posUIState.value){
            return when (selectedDiscountType) {
                DiscountType.PERCENTAGE -> {
                    "%"}
                DiscountType.FIXED_AMOUNT -> {
                    currencySymbol }
            }
        }
    }

    fun onToggleChange() {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isDropdownExpanded = !it.isDropdownExpanded)
            }
        }
    }

    fun onTotalDiscountItemClick(){
        viewModelScope.launch {
            _posUIState.update { it.copy(isDiscountDialog = true,selectedDiscountApplied = DiscountApplied.TOTAL) }
        }
    }

    fun onPriceItemClick(selectedItem: ProductTaxItem) {
        viewModelScope.launch {
            _posUIState.update { it.copy(isDiscountDialog = true, selectedDiscountApplied = DiscountApplied.ITEMS, itemPriceClickItem=selectedItem) }
        }

    }

    fun dismissDiscountDialog() {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isDiscountDialog = false)
            }
        }
    }




    //Payment
    fun onPaymentClick() {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isPaymentScreen = !it.isPaymentScreen)
            }
        }
    }

}