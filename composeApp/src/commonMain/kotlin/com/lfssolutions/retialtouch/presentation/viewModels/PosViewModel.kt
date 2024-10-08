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
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.koin.core.component.KoinComponent

class PosViewModel : BaseViewModel(), KoinComponent {

    private val _posUIState = MutableStateFlow(PosUIState())
    val posUIState: StateFlow<PosUIState> = _posUIState.asStateFlow()

    fun loadDataFromDatabases() {
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading to true at the start
            updateLoader(true)
            try {
                // Load data from multiple databases concurrently
                val data1Deferred = async { getCurrencySymbol() }
                val data2Deferred = async { databaseRepository.getAllProductWithTax()}
                val data3Deferred = async { databaseRepository.getAllMembers()}
                val data4Deferred = async { databaseRepository.getAllMemberGroup()}

                // Await all results
                val currencySymbol = data1Deferred.await()
                val productTaxDao = data2Deferred.await()
                val membersDao = data3Deferred.await()
                val memberGroupDao = data4Deferred.await()

                // Update your state directly with responses
                _posUIState.update { currentState->
                    currentState.copy(
                        currencySymbol = currencySymbol
                    )
                }

                productTaxDao.collectLatest { productList ->
                    val transformedProductList = productList.map { productDao ->
                        // Access the rowItem and map it to the desired format
                        productDao.rowItem // Assuming this is the desired format
                    }
                    _posUIState.update {
                        it.copy(dialogPosList = transformedProductList)
                    }
                }


                membersDao.collectLatest { memberList ->
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
                }

                updateLoader(false)

            } catch (e: Exception) {
                // Handle errors, ensuring loading state is false
                updateLoader(false)
            }
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


    fun updateSearchQuery(value:String){
        viewModelScope.launch {
            _posUIState.update { it.copy(searchQuery = value) }
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
                    items = currentState.uiPosList,
                    grandTotal = currentState.grandTotal
                )

                // Create a new map by copying the old map and adding the new collection
                val updatedMap = currentState.holdSaleCollections.toMutableMap().apply {
                    put(currentState.currentCollectionId, newCollection)
                }


                // Check if the current items are the same as the last held collection
                //val isSameAsLastHeld = updatedMap.values.lastOrNull()?.items == currentState.uiPosList

                val newCollectionId = if(updatedMap.isNotEmpty()) currentState.currentCollectionId+1 else currentState.currentCollectionId


                // Reset uiPosList for new entries
                currentState.copy(
                    holdSaleCollections = updatedMap,
                    currentCollectionId = newCollectionId,
                    uiPosList = listOf(), // Reset after holding
                    isHoldSaleDialog = true
                )
            }
        }
    }

    fun getListFromHoldSale(collection: HeldCollection) {
        viewModelScope.launch {
          _posUIState.update { currentState->

              // Update uiPosList with the selected collection's items
              val updatedCollections = currentState.holdSaleCollections.toMutableMap()

              updatedCollections.remove(collection.collectionId) // Remove the selected collection

              // Merge the existing items in uiPosList with the items from the selected collection
              val updatedUiPosList = currentState.uiPosList + collection.items


              currentState.copy(
                  uiPosList = updatedUiPosList,
                  holdSaleCollections = updatedCollections,
                  currentCollectionId = if (updatedCollections.isEmpty()) 1 else currentState.currentCollectionId-1
              )

          }
        }
    }


    fun insertPosListItem(item: ProductTaxItem) {
        viewModelScope.launch(Dispatchers.IO) {
            // Ensure the quantity is at least 1
            val qty = if (item.qtyOnHand < 1.0) 1.0 else item.qtyOnHand
            val updatedItem = item.copy(qtyOnHand = qty)

            // Insert or update the product in the local database
            val updatedProductList = _posUIState.value.uiPosList + updatedItem
            insertOrUpdateScannedProduct(updatedProductList)
            _posUIState.value.isInsertion=true
           // Automatically handled by observing the flow in the UI, so no need to manually update the list
        }
    }

    fun fetchUIProductList(){
        viewModelScope.launch(Dispatchers.IO){
            val scannedProductJob = async { databaseRepository.fetchAllScannedProduct()}.await()
            scannedProductJob.collectLatest { scannedProductList->
                val transformedProductList=scannedProductList.map {
                    ProductTaxItem(
                        id = it.productId.toInt(),
                        name = it.name,
                        inventoryCode = it.inventoryCode,
                        barCode = it.barCode,
                        qtyOnHand = it.qty,
                        price = it.price,
                        subtotal=it.subtotal,
                        originalSubTotal = it.subtotal,
                        taxPercentage = it.taxPercentage,
                        taxValue = it.taxValue,
                        discount = it.discount
                    )
                }
                _posUIState.update {
                    it.copy(uiPosList = transformedProductList)
                }
            }
        }

    }

    fun calculateBottomValues(){
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val totalQty = currentState.uiPosList.sumOf { it.qtyOnHand }
                val totalTax = currentState.uiPosList.sumOf { it.taxValue ?: 0.0 }
                val itemSubTotal = currentState.uiPosList.sumOf { it.subtotal?:0.0 }
                val itemDiscount = currentState.uiPosList.sumOf { it.discount }
                val grandTotal = currentState.uiPosList.sumOf {
                    val subtotal = it.subtotal ?: 0.0
                    val taxAmount = (it.taxPercentage?.div(100.0)?.times(subtotal)) ?: 0.0
                    subtotal + taxAmount
                }
                currentState.copy(totalQty =totalQty, totalTax = totalTax, subTotal = itemSubTotal, grandTotal = grandTotal , originalTotal = grandTotal,itemsDiscount=itemDiscount)
            }
        }
    }

    fun updateQty(selectedItem: ProductTaxItem,isQtyIncrease:Boolean) {
        println("selectedItem : $selectedItem")
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val updatedProductList = currentState.uiPosList.map { product ->
                    if (product.id == selectedItem.id){
                        val newQty = if (isQtyIncrease) product.qtyOnHand+1 else{ product.qtyOnHand.takeIf { it > 1 }?.minus(1) ?: 1.0}
                        val newSubTotal= product.price?.times(newQty)
                        val finalTotal = newSubTotal?.minus(product.discount)
                        val updatedProduct =  product.copy(qtyOnHand = newQty, subtotal = finalTotal)
                        updateScannedProduct(updatedProduct)
                        updatedProduct
                    }
                    else product
                }
                currentState.copy(uiPosList = updatedProductList)
            }

        }
    }

    fun removedListItem(updatedProduct: ProductTaxItem) {
        println("selectedItem : $updatedProduct")
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val updatedList = currentState.uiPosList.toMutableList()
                updatedList.remove(updatedProduct)
                currentState.copy(uiPosList = updatedList)
            }
        }
    }

    fun removedScannedItem() {

        viewModelScope.launch {
            _posUIState.update { currentState ->
                currentState.copy(uiPosList = listOf(), selectedMember = "Select Member", holdSaleCollections = hashMapOf(), isRemoveDialog = false)
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


                    val discount = when (selectedDiscountType) {
                        DiscountType.PERCENTAGE -> {
                            if(selectedDiscountApplied==DiscountApplied.TOTAL){
                                inputDiscount.toDouble().calculatePercentage(grandTotal)

                            }else{
                                inputDiscount.toDouble().calculatePercentage(itemPriceClickItem.originalSubTotal)
                            }
                        }
                        DiscountType.FIXED_AMOUNT -> {
                            //calculate fixed amount
                            inputDiscount.toDouble()
                        }
                    }

                    updateFinalDiscount(discount)
                }
            }
        }
    }

    private fun updateFinalDiscount(finalDiscount: Double) {
        viewModelScope.launch {
            _posUIState.update { currentState->
                if(currentState.selectedDiscountApplied==DiscountApplied.TOTAL){
                     val finalTotal= currentState.grandTotal-finalDiscount
                     val adjustedTotal = finalTotal.coerceAtLeast(0.0)
                     currentState.copy(isDiscountDialog = false, grandTotal = adjustedTotal, discounts = finalDiscount)
                }else{
                    val id=currentState.itemPriceClickItem.id
                    val finalTotal = currentState.itemPriceClickItem.originalSubTotal-finalDiscount
                    val adjustedTotal = finalTotal.coerceAtLeast(0.0)
                    val updatedList=currentState.uiPosList.map { element->
                        if(element.id==id){
                            val updatedItem= element.copy(discount = finalDiscount, subtotal = adjustedTotal)
                            updateScannedProduct(updatedItem)
                            updatedItem
                        }else{
                            element
                        }
                    }
                    currentState.copy(isDiscountDialog = false, uiPosList = updatedList)
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
                    AppIcons.dollarIcon }
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