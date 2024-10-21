package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.inventory.CRShoppingCartItem
import com.lfssolutions.retialtouch.domain.model.inventory.Product
import com.lfssolutions.retialtouch.domain.model.inventory.Stock
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.productWithTax.HeldCollection
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.DrawableResource
import org.koin.core.component.KoinComponent
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round

class SharedPosViewModel : BaseViewModel(), KoinComponent {

    private val _posUIState = MutableStateFlow(PosUIState())
    val posUIState: StateFlow<PosUIState> = _posUIState.asStateFlow()

    private val currency = MutableStateFlow<String>("")


    data class LoadData(
        val members: List<MemberDao>,
        val promotionsDetails: List<PromotionDetails>,
        val promotions: List<Promotion>,
        /*val additionalData: List<Additional>,
        val extraData: List<Extra> // Adjust type accordingly*/
    )



    // Function to load both details and promotions
    fun loadDbData() {
        viewModelScope.launch {
            // Combine both flows to load them in parallel
            combine(
                dataBaseRepository.getMember(),
                dataBaseRepository.getPromotionDetails(),
                dataBaseRepository.getPromotions()
            ) { members,details, promotion ->
                LoadData(members,details,promotion)
            }
                .onStart { _posUIState.update { it.copy(isLoading = true) }}  // Show loader when starting
                .catch { _posUIState.update { it.copy(isLoading = false) } }   // Handle any errors and hide loader
                .collect { (members,details, promotion) ->
                    println("promotion details : $details $promotion")
                    _posUIState.update {  it.copy(
                        memberList = members.map {member-> member.rowItem},
                        promotionDetails = details.toMutableList(),
                        promotions = promotion.toMutableList(),
                        isLoading = false
                    )
                    }
                }
        }
    }


    fun getAuthDetails(){
        viewModelScope.launch {
            authUser.collectLatest { authDetails->
                if(authDetails!=null){
                    val login=authDetails.loginDao
                    currency.update { login.currencySymbol?:"$" }
                    _posUIState.update {
                        it.copy(
                            currencySymbol = login.currencySymbol?:"$",
                            isSalesTaxInclusive = login.salesTaxInclusive?:false,
                            posInvoiceRounded=login.posInvoiceRounded,
                            isDiscountGranted= isDiscountEnabledTaxInclusiveName()
                        )
                    }
                }
            }
        }
    }

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

    fun loadAllProducts() {
        viewModelScope.launch {
            updateLoader(true)
            dataBaseRepository.getProduct().collectLatest { productList ->
                if(productList.isNotEmpty()){
                    _posUIState.update { it.copy(stockList=productList, isLoading = false)}
                     println("ProductList: $productList")
                }
            }
        }

    }

    //scan search code
    fun scanBarcode(){
        viewModelScope.launch {
            scanStock(_posUIState.value.searchQuery)
        }
    }

    private fun scanStock(barcode: String) {
        viewModelScope.launch {
            if (isBarcodeValid(barcode)) {
                handleDiscountBarcode(barcode)
            } else if (barcode.contains('*')) {
                val (qty, code) = parseQuantityAndCode(barcode)
                handleProductLookup(code, qty)
            } else {
                handleProductLookup(barcode, 1.0)
            }
        }
    }

    // Handles discount barcodes and updates the state accordingly
    private fun handleDiscountBarcode(barcode: String) {
        with(_posUIState.value){
            // Check if discount is allowed
            if (!isDiscountGranted) {
                return
            }
            val percentDiscountMode = barcode.endsWith("%")
            println("percentDiscountMode : $percentDiscountMode")
            val parts = barcode.split("-")
            println("parts : $parts")
            val discountValue = parseDiscount(parts)
            println("discountValue : $discountValue")
            val itemNo = parseItemNo(parts)
            println("itemNo : $itemNo")

            if (itemNo == null || itemNo == 0) {
                // Apply global discount
                if (discountValue > posUIState.value.globalDiscount &&
                    (!percentDiscountMode || discountValue > 100)) {
                    //updateDialogState(true)
                    return
                }
                updateGlobalDiscounts(percentDiscountMode,discountValue)

            } else if (itemNo > 0 && itemNo <= cartList.size) {
                // Apply discount to specific item in the cart
                val currentItem = cartList[itemNo - 1]
                if (discountValue > currentItem.price && (!percentDiscountMode || discountValue > 100)){
                    //updateDialogState(true)
                    return
                }
                // Create a new item with the updated discount
                val updatedItem = currentItem.copy(discount = discountValue, discountIsInPercent = percentDiscountMode)
                // Update the shopping cart list with the updated item
                val updatedCart = cartList.toMutableList()
                updatedCart[itemNo - 1] = updatedItem
                updateSaleItem(updatedCart)
                println("parts : $parts")
                /*if (itemNo > 0 && itemNo <= shoppingCart.size) {
                    val item = shoppingCartNew[itemNo - 1]
                    if (discountValue > item.price && (!percentDiscountMode || discountValue > 100)) {
                        updateDialogState(true)
                        return
                    }
                    item.discount = discountValue
                    item.discountIsInPercent = percentDiscountMode
                }*/
            }
        }

    }

    // Parse quantity and code from barcode containing '*'
    private fun parseQuantityAndCode(barcode: String): Pair<Double, String> {
        return try {
            val parts = barcode.split('*')
            val qty = parts[0].toDoubleOrNull() ?: 1.0
            val code = parts[1]
            qty to code
        } catch (e: Exception) {
            1.0 to barcode  // Default to qty 1.0 if parsing fails
        }
    }

    // Handle product lookup by barcode or product code
    private suspend fun handleProductLookup(code: String, qty: Double) {
        val barcode = dataBaseRepository.getBarcode(code).firstOrNull()
        // If a barcode is found, try to get the product by barcode
        if (barcode != null) {
            val productB = barcode.productId.let { dataBaseRepository.getProductById(it).firstOrNull() }
            if (productB != null) {
                processFoundProduct(productB,qty)
                return  // Exit function since product was found
            }
        }
        // If no product found by barcode or if barcode is null, try to get product by product code
        val productPC = dataBaseRepository.getProductByCode(code).firstOrNull()
        if (productPC != null) {
            processFoundProduct(productPC,qty)
        } else {
            // Open dialog if no product found
            updateDialogState(true)
            return
        }
    }

    private fun processFoundProduct(product: Product, qty: Double) {
        // Your logic to process the found product
        val stock=Stock(
            id = product.id ?: 0,
            name = product.name ?: "",
            categoryId = 0,
            productId = product.id,
            sortOrder = 0,
            icon = product.image ?: "",
            stockPrice = product.price ?: 0.0,
            tax = product.tax ?: 0.0,
            barcode = product.barcode ?: "",
            inventoryCode = product.productCode ?: ""
        )

        addSaleItem(stock=stock, qty = qty)
    }

    private fun isBarcodeValid(barcode: String): Boolean {
        return barcode.length > 1 && (barcode.endsWith("%") || barcode.endsWith("$"))
    }

    private fun parseDiscount(parts: List<String>): Double {
        return parts.getOrNull(0)?.let { part ->
            part.substring(0, part.length - 1).toDoubleOrNull() ?: 0.0
        } ?: 0.0
    }

    private fun parseItemNo(parts: List<String>): Int? {
        return if (parts.size > 1) parts[0].toIntOrNull() else null
    }

    private fun updateGlobalDiscounts(isInPercent: Boolean,discount: Double) {
        _posUIState.update { currentState->
            currentState.copy(globalDiscountIsInPercent = isInPercent,globalDiscount=discount)
        }
    }

    private fun updateGlobalDiscount(discount: Double) {
        _posUIState.update { currentState->
            currentState.copy(globalDiscount = discount)
        }
    }

    private suspend fun getProductByBarcode(code: String) {
        dataBaseRepository.getBarcode(code).collectLatest { barcode->
            barcode?.productId?.let { dataBaseRepository.getProductById(it) }?.collectLatest {
                //productById.value=it
            }
        }
    }

    private fun updateSaleItem(item: MutableList<CRShoppingCartItem>) {
        viewModelScope.launch {
            _posUIState.update { it.copy(cartList = item,searchQuery = "") }
        }
    }

    private fun updateGlobalExchangeActivator(value:Boolean) {
        viewModelScope.launch {
            _posUIState.update { it.copy(globalExchangeActivator = value) }
        }
    }

    fun readDialogProduct(){
        viewModelScope.launch {
            val state=_posUIState.value
            if(state.stockList.isEmpty())
                loadAllProducts()

            val dataset = state.stockList.filter { it.matches(state.searchQuery) }.toMutableList()

            if (state.searchQuery.isNotEmpty()) {
                val barcode = dataBaseRepository.getBarcode(state.searchQuery).firstOrNull()
                if (barcode != null) {
                    val productB = barcode.productId.let { dataBaseRepository.getProductById(it).firstOrNull() }
                    if (productB != null) {
                        dataset.add(0, productB)
                    }
                }
            }


        }
    }

    fun addSearchProduct(product: Product){
        val qty = 1.0
        val stock=Stock(
            id = product.id ?: 0,
            name = product.name ?: "",
            categoryId = 0,
            productId = product.id,
            sortOrder = 0,
            icon = product.image ?: "",
            stockPrice = product.price ?: 0.0,
            tax = product.tax ?: 0.0,
            barcode = product.barcode ?: "",
            inventoryCode = product.productCode ?: ""
        )

        addSaleItem(stock=stock, qty = qty)
    }

    private fun addSaleItem(stock: Stock, qty: Double = 1.0)
    {
        viewModelScope.launch {
            with(_posUIState.value){
                var adjustedQty = qty
                // Handle exchange (return case) by negating the quantity
                if (globalExchangeActivator) {
                    adjustedQty *= -1
                }
                val existingItem=findCartItem(stock,cartList)
                val updatedList = if (existingItem != null) {
                    // Update the quantity of the existing item
                    cartList.map { element ->
                        if (element.id == stock.id) {
                            element.copy(qty = element.qty + qty)
                        } else {
                            element
                        }
                    }.toMutableList()
                } else {
                    // Add a new item to the beginning of the cart list
                    val newItem = CRShoppingCartItem(
                        stock = stock,
                        exchange = globalExchangeActivator,
                        qty = qty,
                        salesTaxInclusive = isSalesTaxInclusive
                    )
                    mutableListOf(newItem).apply { addAll(cartList) }
                }

                updateSaleItem(updatedList)
                updateGlobalExchangeActivator(false)
            }
        }
    }

    private fun findCartItem(stock: Stock, shoppingCart: List<CRShoppingCartItem>): CRShoppingCartItem? {
        return shoppingCart.firstOrNull { itm ->
            val sameBarcode = stock.barcode != null && stock.barcode.length > 3 &&
                    (itm.stock.barcode == stock.barcode || itm.stock.inventoryCode == stock.barcode)
            itm.stock.id == stock.id || sameBarcode
        }
    }

    fun recomputeSale(){
        viewModelScope.launch(Dispatchers.Default) {
            println("Recompute List :${posUIState.value.cartList}")
            applyDiscountsIfEligible()
            calculateCartTotals()
        }
    }

    private fun calculateCartTotals(){
        with(_posUIState.value){
            var qty = 0.0
            var total = 0.0
            var taxTotal = 0.0
            var cartWithoutDiscount = 0.0
            var promoDiscount = 0.0
            var cartItemDiscount = 0.0
            var cartItemPromotionDiscount = 0.0
            var itemTotal = 0.0
            var itemDiscount = 0.0
            var apiTax = 0.0

            for (item in cartList) {
                qty += if (item.qty < 0) item.qty * -1 else item.qty

                if (isSalesTaxInclusive) {
                    total += item.getFinalPrice()
                    taxTotal += item.calculateTax()
                    apiTax = item.tax
                    cartWithoutDiscount += item.getFinalPriceWithoutTax()
                    promoDiscount += item.calculateDiscount()
                    cartItemDiscount += item.getItemDiscount()
                    cartItemPromotionDiscount += item.getPromotionDiscount()
                } else {
                    total += item.getFinalPriceWithoutTax()
                    cartWithoutDiscount += item.getFinalPriceWithoutTax()
                    taxTotal += item.calculateTax()
                    cartItemDiscount += item.getItemDiscount()
                    cartItemPromotionDiscount += item.getPromotionDiscount()
                }

                itemTotal += (item.price * item.qty)
                itemDiscount += item.calculateDiscount()
            }



            // After this loop, you can now work with your totals
            println("Total Quantity: $qty")
            println("Total Cart Value: $total")
            println("Total Tax: $taxTotal")
            println("Cart Without Discount: $cartWithoutDiscount")
            println("Total Promo Discount: $promoDiscount")
            println("Cart Item Discount: $cartItemDiscount")
            println("Cart Item Promotion Discount: $cartItemPromotionDiscount")
            println("Total Item Price: $itemTotal")


            _posUIState.update { currentState->
                currentState.copy(
                    quantityTotal = qty,
                    cartTotal = total,
                    cartTotalWithoutDiscount = cartWithoutDiscount,
                    cartItemsDiscount = cartItemDiscount,
                    cartPromotionDiscount = cartItemPromotionDiscount,
                    grandTotalWithoutDiscount = if(isSalesTaxInclusive) total else (total + taxTotal),
                    promotionDiscount=promoDiscount,
                    itemTotal=itemTotal,
                    itemDiscount=itemDiscount
                )
            }

            // Apply global discount logic
            total = applyGlobalDiscount(total,globalDiscount, globalDiscountIsInPercent)
            println("total :$total | posInvoiceRounded: $posInvoiceRounded")
            val roundingTotal = posRounding(total, posInvoiceRounded)
            val (invoiceRounding, grandTotal) = calculateGrandTotal(
                total, roundingTotal, taxTotal, isSalesTaxInclusive
            )
            val globalTax = calculateGlobalTax(isSalesTaxInclusive, grandTotal, apiTax)
            println("total: $total | grandTotal: $grandTotal | globalTax : $globalTax")
            _posUIState.update { currentState->
                currentState.copy(
                    invoiceRounding= invoiceRounding,
                    grandTotal = grandTotal,
                    globalTax = globalTax
                )
            }

        }
    }

    private fun applyDiscountsIfEligible() {
        with(_posUIState.value){
            if (isDiscountGranted) {
                promoByPriceBreak.clear()
                promoByQuantity.clear()

                // Apply promotions to items in the cart
                cartList.forEach { item ->
                    val promo = promotionDetails.firstOrNull { it.inventoryCode == item.stock.inventoryCode }
                        ?: PromotionDetails(id = 0)

                    if (promo.id != 0) {
                        val promoForName = promotions.firstOrNull { it.id.toInt() == promo.promotionId }
                            ?: Promotion(id = 0)
                        item.promotionName = promoForName.name
                        //_tryApplyPromo(promo, item)
                    }
                }

                // Process price-break and quantity-based promotions
                val updatedList=cartList.map { item ->
                    item.promotion?.let { promotion ->
                        when (promotion.promotionTypeName) {
                            "PromotionByPriceBreak" -> applyPriceBreakPromotion(item, promotion)
                            "PromotionByQty" -> applyQuantityPromotion(item, promotion)
                        }
                    }
                }
            }
        }
    }

    private fun applyQuantityPromotion(item: CRShoppingCartItem, promotion: PromotionDetails) {
        with(_posUIState.value){
            val promoId = promotion.promotionId
            // Check if promoByquantity contains the promoId

            promoByQuantity[promoId]?.let{promoData ->
                if (promoData.promoQty == 0.0) {
                    // No quantity for the promotion, deactivate it
                    item.promotionActive = false
                    item.promotionByQuantity = false
                }else {
                    // Check if the quantity condition meets the promotion requirement
                    if (promoData.qty >= promoData.promoQty){
                        // Find the specific item in the promotion list
                        val thisItem = promoData.items.firstOrNull { it.code == item.code }
                        val thisItemQty = thisItem?.qty ?: 0.0
                        // Calculate the remaining promotion quantity
                        // Determine how many items can be included in the promotion
                        val itemWithPromotion : Double = if ((promoData.remainingPromotionQty - thisItemQty) > 0) {
                            thisItemQty
                        } else {
                            promoData.remainingPromotionQty
                        }

                        // Update remaining promotion quantity
                        promoData.remainingPromotionQty -= itemWithPromotion

                        val itemWithoutPromotion: Double = if ((thisItemQty-itemWithPromotion) > 0) {
                            (thisItemQty - itemWithPromotion)
                        } else {
                            0.0 // Ensure you're returning a Double
                        }

                        var promoAmountSingle = promoData.amount / promoData.promoQty

                        promoData.percentage?.let { percentage ->
                            if (percentage != 0.0) {
                                val offValue = (item.price * percentage) / 100
                                promoAmountSingle = item.price - offValue
                            }
                        }

                        val promoPrice = promoAmountSingle * itemWithPromotion
                        val notPromoPrice = item.price * itemWithoutPromotion
                        item.amount = (promoPrice + notPromoPrice) / item.qty
                        item.promotionActive = true
                        item.promotionByQuantity = true

                    }else {
                        // Quantity does not meet the promotion requirement
                        item.promotionActive = false
                        item.promotionByQuantity = false
                    }
                }

            }
        }
    }

    private fun applyPriceBreakPromotion(item: CRShoppingCartItem, promotion: PromotionDetails) {
        with(_posUIState.value){
            val promoId = promotion.promotionId
            if (promoByPriceBreak.containsKey(promoId)){
                val promoData = promoByPriceBreak[promoId]
                if(promoData!=null){
                    println("Promo Quantity: ${promoData.promoQty}")
                    if (promoData.promoQty == 0.0) {
                        item.promotionActive = false
                    }else{
                        if (promoData.qty >= promoData.promoQty) {
                            item.promotion?.promotionPrice = promoData.price
                            item.promotionActive = true
                        } else {
                            item.promotionActive = false
                        }
                    }
                }
            }
        }
    }

    private fun applyGlobalDiscount(total: Double, globalDiscount: Double, isPercent: Boolean): Double {
        return if (globalDiscount > 0) {
            if (isPercent) {
                if (globalDiscount < 100.0) {
                    total - ((total * globalDiscount) / 100.0)
                } else total
            } else {
                max(0.0, total - globalDiscount) // Ensure total doesn't go negative
            }
        } else total
    }

    private fun posRounding(n: Double, rounding: Double?): Double {
        if (rounding == 0.0) return n

        val toFix = if (n.toInt().toDouble() == n) {
            0 // If the number is an integer, no decimal places required
        } else {
            rounding.toString().substringAfter(".").length // Decimal places based on rounding
        }

        // Perform rounding to the specified precision
        val factor = 10.0.pow(toFix)
        var value = round(n * factor) / factor

        if (n.toInt().toDouble() != n) {
            val valueString = value.toString().toMutableList()
            val lastIndex = valueString.lastIndex

            val lastDigit = valueString[lastIndex].digitToIntOrNull() ?: 0
            valueString[lastIndex] = if (lastDigit >= 5) '5' else '0'

            value = valueString.joinToString("").toDoubleOrNull() ?: value
        }

        return value.roundTo(toFix)
    }

    private fun calculateGrandTotal(
        total: Double,
        roundingTotal: Double,
        taxTotal: Double,
        isSalesTaxInclusive: Boolean
    ): Pair<Double, Double> {
        var invoiceRounding=0.0
        val grandTotal: Double

        if (isSalesTaxInclusive) {
            if (roundingTotal != total) {
                invoiceRounding = roundingTotal - total
                grandTotal = roundingTotal
            } else {
                grandTotal = total
            }
        } else {
            if (roundingTotal != total) {
                invoiceRounding = roundingTotal - total
                grandTotal = roundingTotal + taxTotal
            } else {
                grandTotal = total + taxTotal
            }
        }

        return Pair(invoiceRounding, grandTotal)
    }

    private fun calculateGlobalTax(salesTaxInclusive: Boolean, amount: Double, tax: Double): Double {
        return if (salesTaxInclusive) {
            (amount * tax) / (tax + 100)
        } else {
            (amount * tax) / 100
        }
    }

    private fun processBarcode(barcode: String, onScan: (Double, String) -> Unit, onError: (String) -> Unit) {
        if (barcode.contains('*')) {
            try {
                val parts = barcode.split('*')
                if (parts.size == 2) {
                    val qty = parts[0].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid quantity format")
                    val code = parts[1]
                    onScan(qty, code) // Call the onScan callback with parsed values
                } else {
                    onError("Barcode must contain exactly two parts separated by '*'.")
                }
            } catch (e: Exception) {
                onError("Error processing barcode: ${e.message}")
            }
        } else {
            onError("Barcode must contain '*' to separate quantity and code.")
        }
    }


    fun insertPosListItem(item: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val qty = if (item.qtyOnHand ==0.0) 1.0 else item.qtyOnHand
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

    fun clearSearch() {
        viewModelScope.launch {
            _posUIState.update { it.copy(searchQuery = "") }
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

    fun updateMemberDialogState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(isMemberDialog = value) }
        }
    }

    fun formatPriceForUI(amount: Double?) :String{
      return  "${currency.value}${amount?.roundTo(2)}"
    }

    fun calculateDiscount(item: CRShoppingCartItem): String {
        return when {
            item.discount > 0 -> {
                if (item.currentDiscount > 0) {
                    "-${item.calculateDiscount()}${if (item.discountIsInPercent) "%" else currency.value}"
                } else {
                    "-${item.calculateDiscount()}${if (item.discountIsInPercent) "%" else currency.value}"
                }
            }
            item.currentDiscount > 0 -> {
                "-${item.currentDiscount.roundTo(2)}${if (item.discountIsInPercent) "%" else currency.value}"
            }
            else -> ""
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

                    val discountValue=inputDiscount.toDouble()

                    val finalAmountStr = when(selectedDiscountApplied){
                        DiscountApplied.GLOBAL -> {
                            updateGlobalDiscounts(getDiscountInPercent(),discountValue)
                            "$discountValue${getDiscountSymbol()}"
                        }
                        DiscountApplied.SUB_ITEMS -> {
                            if (discountValue < selectedProduct.price && (discountValue < 100)){
                                // Create a new item with the updated discount
                                //updateSubItemDiscount()
                                val updatedItem = selectedProduct.copy(discount = discountValue, discountIsInPercent = getDiscountInPercent())
                                // Update the shopping cart list with the updated item
                                 cartList[itemPosition] = updatedItem
                                 //updateSaleItem(cartList)
                                println("updatedItem : $updatedItem")
                            }
                            "${itemPosition}-$discountValue${getDiscountSymbol()}"
                        }
                    }
                    dismissDiscountDialog()
                    recomputeSale()

                   // scanStock(finalAmountStr)

                    /*val (discount,discountPer) = when (selectedDiscountType) {
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
                    updateFinalDiscount(discount,discountPer,adjustedTotal)*/
                }
            }
        }
    }

    private fun updateSubItemDiscount() {
        viewModelScope.launch {
            _posUIState.update { currentState->
                val discount: Double=currentState.inputDiscount.toDouble()
                val updatedList=currentState.cartList.map { element->
                    if(element.stock.id==currentState.selectedProduct.stock.id){
                        val updatedItem= element.copy(discount = discount,   discountIsInPercent = getDiscountInPercent())
                        //dataBaseRepository.updateScannedProduct(updatedItem)
                        updatedItem
                    }else{
                        element
                    }
                }.toMutableList()
                currentState.copy(isDiscountDialog = false, cartList = updatedList)
            }
        }
    }

    fun getDiscountValue():String{
        val state=_posUIState.value
        return when(state.globalDiscountIsInPercent){
            true->{
                "${state.globalDiscount}%"
            }
            else ->{
                formatPriceForUI(state.globalDiscount + (if(state.cartPromotionDiscount>0) state.cartPromotionDiscount else 0.0))
            }
        }
    }

    fun getDiscountInPercent() : Boolean{
        return when(_posUIState.value.selectedDiscountType){
            DiscountType.PERCENTAGE ->{
                true
            }
            DiscountType.FIXED_AMOUNT->{
                false
            }
        }
    }

    fun getDiscountSymbol() : String{
        return when(_posUIState.value.selectedDiscountType){
            DiscountType.PERCENTAGE ->{
                "%"
            }
            DiscountType.FIXED_AMOUNT->{
                "$"
            }
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

    fun onTotalDiscountItemClick(){
        viewModelScope.launch {
            _posUIState.update {
                it.copy(
                    isDiscountDialog = true,
                    selectedDiscountApplied = DiscountApplied.GLOBAL
                )
            }
        }
    }

    fun onPriceItemClick(selectedItem: CRShoppingCartItem, index: Int) {
        viewModelScope.launch {
            _posUIState.update { it.copy(isDiscountDialog = true, selectedDiscountApplied = DiscountApplied.SUB_ITEMS,itemPosition=index, selectedProduct = selectedItem) }
        }

    }

    fun dismissDiscountDialog() {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isDiscountDialog = false)
            }
        }
    }

    fun increaseQty(item: CRShoppingCartItem){
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val updatedProductList = currentState.cartList.map { element ->
                    if (element.stock.id == item.stock.id){
                        val newQty = if (element.qty + 1 > -1 && element.exchange) -1.0 else element.qty + 1
                        element.copy(qty = newQty)
                        //dataBaseRepository.updateScannedProduct(updatedProduct)
                    }
                    else element
                }.toMutableList()
                currentState.copy(cartList = updatedProductList)
            }
        }
    }

    fun decreaseQty(item: CRShoppingCartItem){
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val updatedProductList = currentState.cartList.map { element ->
                    if (element.stock.id == item.stock.id){
                        val newQty = (element.qty - 1).let { qty ->
                            if (qty < 1 && !element.exchange) 1.0 else qty
                        }
                        element.copy(qty = newQty)
                    }
                    else element
                }.toMutableList()
                currentState.copy(cartList = updatedProductList)
            }
        }
    }

    fun applyCustomQty(item: CRShoppingCartItem){
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val updatedProductList = currentState.cartList.map { element ->
                    if (element.stock.id == item.stock.id){
                        val newQty = if(currentState.inputDiscount.isNotEmpty())currentState.inputDiscount.toDouble() else element.qty
                        element.copy(qty = newQty)
                    }
                    else element
                }.toMutableList()
                currentState.copy(cartList = updatedProductList)
            }
        }
    }

    fun updateQty(selectedItem: CRShoppingCartItem,isQtyIncrease:Boolean) {
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


    fun removedListItem(selectedItem: CRShoppingCartItem) {
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

    fun onToggleChange() {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isDropdownExpanded = !it.isDropdownExpanded)
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

    // Detects if a query is likely a code (numeric and shorter than a typical name)
    private fun isCode(query: String): Boolean {
        return query.all { it.isDigit() } /*&& query.length <= 10 // Adjust length if needed*/
    }

    private fun filterListByCode(query: String) {
        viewModelScope.launch {
            val filteredList = _posUIState.value.dialogStockList.filter {
                it.barcode?.contains(query)==true  || it.productCode?.contains(query) == true
            }
            if (filteredList.isNotEmpty()) {
                insertPosListItem(filteredList[0]) // Add only the first matched item
            }
        }
    }

    fun calculateBottomValues(){
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val totalQty = currentState.shoppingCart.sumOf { it.qtyOnHand }
                val totalTax = currentState.shoppingCart.sumOf { it.taxValue ?: 0.0 }
                val itemSubTotal = currentState.shoppingCart.sumOf { it.cartTotal?:0.0 }
                val itemDiscount = currentState.shoppingCart.sumOf { it.itemDiscount }
                val grandTotal = currentState.shoppingCart.sumOf {
                    val subtotal = it.cartTotal ?: 0.0
                    val taxAmount = (it.taxPercentage?.div(100.0)?.times(subtotal)) ?: 0.0
                    subtotal + taxAmount
                }
                currentState.copy(quantityTotal =totalQty, invoiceTax = totalTax, invoiceSubTotal = itemSubTotal, grandTotal = grandTotal , originalTotal = grandTotal,itemDiscount=itemDiscount)
            }
        }
    }

}


