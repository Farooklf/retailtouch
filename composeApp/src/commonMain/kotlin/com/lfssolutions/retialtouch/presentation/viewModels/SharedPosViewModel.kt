package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.model.LoadData
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.menu.StockCategory
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSaleDao
import com.lfssolutions.retialtouch.domain.model.products.AnimatedProductCard
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.products.CRShoppingCartItem
import com.lfssolutions.retialtouch.domain.model.products.CreatePOSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.domain.model.products.PosInvoiceDetail
import com.lfssolutions.retialtouch.domain.model.products.PosPayment
import com.lfssolutions.retialtouch.domain.model.products.PosUIState
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.domain.model.promotions.CRPromotionByPriceBreak
import com.lfssolutions.retialtouch.domain.model.promotions.CRPromotionByQuantity
import com.lfssolutions.retialtouch.domain.model.promotions.CRPromotionByQuantityItem
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.utils.AppBasicsDetails
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getCurrentDate
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getCurrentDateAndTimeInEpochMilliSeconds
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getCurrentDateTime
import com.lfssolutions.retialtouch.utils.DiscountApplied
import com.lfssolutions.retialtouch.utils.DiscountType
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import com.lfssolutions.retialtouch.utils.NumberFormatting
import com.lfssolutions.retialtouch.utils.PrinterType
import com.lfssolutions.retialtouch.utils.TemplateType
import com.lfssolutions.retialtouch.utils.defaultTemplate
import com.lfssolutions.retialtouch.utils.defaultTemplate2
import com.lfssolutions.retialtouch.utils.formatAmountForPrint
import com.lfssolutions.retialtouch.utils.printer.ItemData
import com.lfssolutions.retialtouch.utils.printer.PrinterServiceProvider
import com.lfssolutions.retialtouch.utils.printer.TemplateRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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

    private val employeeId = MutableStateFlow(0)

    private val _posInvoice = MutableStateFlow(PosInvoice())
    val posInvoice : StateFlow<PosInvoice> = _posInvoice.asStateFlow()



    fun initialState(){
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isPaymentClose=false, cartTotal = 0.0)
            }
        }
    }

    //Load data from DataBase
    fun loadCategoryAndMenuItems() {
        viewModelScope.launch(Dispatchers.IO) {
            updateCartLoader(true)
            val authDetails = async { dataBaseRepository.getAuthUser() }.await()
            val category = async { dataBaseRepository.getStockCategories() }.await()
            val menuProducts= async { dataBaseRepository.getStocks() }.await()

            authDetails.collectLatest { authDetails->
                val login = authDetails.loginDao
                _posUIState.update {
                    it.copy(
                        loginUser = login,
                        currencySymbol = login.currencySymbol?:"$",
                        isSalesTaxInclusive = login.salesTaxInclusive?:false,
                        posInvoiceRounded=login.posInvoiceRounded,
                        isDiscountGranted= isDiscountEnabledTaxInclusiveName()
                    )
                }
            }

            category.collectLatest { category ->
                if(category.isNotEmpty()){
                    updateCategories(category.sortedBy { it.name }.sortedBy { it.sortOrder } )
                }
            }
            menuProducts.collectLatest { menu->
                updateMenuProducts(menu)
            }

           // updateLoader(false)
        }
    }

    fun loadTotal() {
        viewModelScope.launch {
         _posUIState.update{state->
             val totalPrice= state.cartList.sumOf{ it.price*it.qty }
             state.copy(cartValue = totalPrice, cartSize =state.cartList.size)
          }
        }
    }

    // Function to load both details and promotions
    fun loadDbData() {
        viewModelScope.launch {
            // Combine both flows to load them in parallel
            combine(
                dataBaseRepository.getMember(),
                dataBaseRepository.getPromotionDetails(),
                dataBaseRepository.getPromotions(),
                dataBaseRepository.getSelectedLocation(),
                dataBaseRepository.getHoldSales()
            ) { members,details, promotion,location,holdSale->
                LoadData(members,details,promotion,location,holdSale)
            }
                .onStart {
                    _posUIState.update { it.copy(isLoading = true)}
                }  // Show loader when starting
                .catch { th ->
                    println("exception : ${th.message}")
                    _posUIState.update { it.copy(isLoading = false) }
                }   // Handle any errors and hide loader
                .collect { (members,details, promotion,location,holdSale) ->
                    println("promotionDetails : $details | promotion : $promotion | location : $location |holdSale : $holdSale")
                    val holdMap = holdSale.associateBy { item -> item.collectionId }.toMutableMap()
                    _posUIState.update {  it.copy(
                        location=location,
                        memberList = members.map {member-> member.rowItem},
                        promotionDetails = details.toMutableList(),
                        promotions = promotion.toMutableList(),
                        salesOnHold = holdMap,
                        isLoading = false
                    )
                    }
                }
        }
    }


    fun getPrinterEnable(){
        viewModelScope.launch {
            isPrinterEnable.collectLatest { isPrinter->
               if(isPrinter!=null){
                   _posUIState.update {
                       it.copy(isPrinterEnable= isPrinter)
                   }
               }
            }
        }
    }

    fun getAuthDetails(){
        viewModelScope.launch {
            authUser.collect { authDetails->
                if(authDetails!=null){
                    val login = authDetails.loginDao
                    _posUIState.update {
                        it.copy(
                            loginUser = login,
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

    fun getEmployee(){
        viewModelScope.launch {
            employee.collectLatest { empDao->
                if(empDao!=null){
                    employeeId.update {
                        empDao.employeeId
                    }
                }
            }
        }
    }

    fun fetchPaymentList(){
        viewModelScope.launch(Dispatchers.IO){
            dataBaseRepository.getPaymentType().collectLatest { list->
                withContext(Dispatchers.Main) {
                    _posUIState.update { it.copy(availablePayments = list)
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
            dataBaseRepository.getProduct().collect { productList ->
                if(productList.isNotEmpty()){
                    _posUIState.update { it.copy(stockList=productList, isLoading = false)}
                }
            }
        }
    }

    fun scanBarcode(){
        viewModelScope.launch {
            scanStock(posUIState.value.searchQuery)
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
        val stock= Stock(
            id = product.id ?: 0,
            name = product.name ?: "",
            categoryId = 0,
            productId = product.id?:0,
            sortOrder = 0,
            imagePath = product.image ?: "",
            price = product.price ?: 0.0,
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

    private fun updateSaleItem(item: MutableList<CRShoppingCartItem>) {
        viewModelScope.launch {
            item.isEmpty()
            _posUIState.update { it.copy(cartList = item,searchQuery = "") }
        }
    }

    fun updateGlobalExchangeActivator(value:Boolean) {
        viewModelScope.launch {
            _posUIState.update { it.copy(globalExchangeActivator = value) }
        }
    }

    private fun updateSyncInProgress(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(syncInProgress = value) }
        }
    }


    fun addSearchProduct(product: Product){
        val qty = 1.0
        val stock= Stock(
            id = product.id,
            name = product.name,
            categoryId = 0,
            productId = product.id,
            sortOrder = 0,
            imagePath = product.image,
            price = product.price,
            tax = product.tax,
            barcode = product.barcode,
            inventoryCode = product.productCode
        )

        addSaleItem(stock=stock, qty = qty)
    }

    private fun addToCartItem(stock: Stock) {
        val state=posUIState.value
        val adjustedQty=1.0

        val cartItem = state.cartList.find { (it.stock.inventoryCode==stock.inventoryCode) || (it.stock.barcode==stock.barcode) || (it.stock.productId==stock.productId)}

        val updatedCartList = if(cartItem!=null){
            state.cartList.map { element ->
                if (element.id == stock.id) {
                    element.copy(qty = element.qty + adjustedQty)
                } else {
                    element
                }
            }.toMutableList()
        }else{
            val newCartItem=CRShoppingCartItem(
                id = stock.id,
                stock = stock,
                exchange = state.globalExchangeActivator,
                qty = adjustedQty,
                salesTaxInclusive = state.isSalesTaxInclusive
            )
            mutableListOf(newCartItem).apply { addAll(state.cartList) }
        }
        println("updatedCartList : $updatedCartList")
        updateSaleItem(updatedCartList)
        loadTotal()
        applyDiscountsIfEligible(updatedCartList)
    }

    private fun addSaleItem(stock: Stock, qty: Double = 1.0)
    {
        viewModelScope.launch {
            with(posUIState.value){
                var adjustedQty = qty
                if (globalExchangeActivator) {
                    adjustedQty *= (-1)
                }
                val existingItem=findCartItem(stock,cartList)
                val updatedList = if (existingItem != null) {
                    cartList.map { element ->
                        if (element.id == stock.id) {
                            element.copy(qty = element.qty + adjustedQty)
                        } else {
                            element
                        }
                    }.toMutableList()
                } else {
                    val newItem = CRShoppingCartItem(
                        stock = stock,
                        exchange = globalExchangeActivator,
                        qty = adjustedQty,
                        salesTaxInclusive = isSalesTaxInclusive
                    )
                    mutableListOf(newItem).apply { addAll(cartList) }
                }
                applyDiscountsIfEligible(updatedList)
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
        viewModelScope.launch {
            //calculateCartTotals()
            loadCartTotals()
        }
    }

     private fun loadCartTotals(){
        viewModelScope.launch{
            posUIState.collect{ state->
                val itemTotalQty=state.cartList.sumOf { if (it.qty < 0) (it.qty * -1) else it.qty }
                val itemTotalTax = state.cartList.sumOf {  it.calculateTax() }
                val cartWithoutDiscount = state.cartList.sumOf {  it.getFinalPriceWithoutTax() }
                //val itemTotal = state.cartList.sumOf { (it.currentPrice * it.qty) }
                //val itemDiscount = state.cartList.sumOf { it.calculateDiscount() }
                val cartItemDiscount = state.cartList.sumOf { it.calculateDiscount() }
                val cartItemPromotionDiscount = state.cartList.sumOf { it.getPromotionDiscount() }

               var cartTotal= if (state.isSalesTaxInclusive){
                     state.cartList.sumOf {  it.getFinalPrice() }
                }else{
                    state.cartList.sumOf {  it.getFinalPriceWithoutTax() }
                }
                val apiTax = if(state.cartList.isNotEmpty())
                     if (state.isSalesTaxInclusive) state.cartList.first().tax else 0.0
                else 0.0

                // Apply global discount logic
                cartTotal = applyGlobalDiscount(cartTotal,state.globalDiscount, state.globalDiscountIsInPercent)
                val roundingTotal = posRounding(cartTotal, state.posInvoiceRounded)
                val (invoiceRounding, grandTotal) = calculateGrandTotal(
                    cartTotal, roundingTotal, itemTotalTax, state.isSalesTaxInclusive
                )
                val globalTax = calculateGlobalTax(state.isSalesTaxInclusive, grandTotal, apiTax)
                // After this loop, you can now work with your totals
                println("TotalQuantity: $itemTotalQty | TotalTax: $itemTotalTax | SubTotal: $cartTotal | SubTotalWithoutDiscount:  $cartWithoutDiscount | PromoDiscount : $cartItemPromotionDiscount | ItemDiscount : $cartItemDiscount | SubTotalAfterDiscount: $cartTotal | RoundingTotal: $roundingTotal | GrandTotal: $grandTotal | GlobalTax : $globalTax | InvoiceRounding: $invoiceRounding")

                _posUIState.update { currentState->
                    currentState.copy(
                        quantityTotal = itemTotalQty,
                        cartTotal = cartTotal,
                        cartTotalWithoutDiscount = cartWithoutDiscount,
                        cartItemsDiscount = cartItemDiscount,
                        cartPromotionDiscount = cartItemPromotionDiscount,
                        grandTotalWithoutDiscount = if(state.isSalesTaxInclusive) cartTotal else (cartTotal + itemTotalTax),
                        invoiceRounding= invoiceRounding,
                        grandTotal = grandTotal,
                        globalTax = globalTax,
                        remainingBalance = grandTotal
                    )
                }

            }
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

                itemTotal += (item.currentPrice * item.qty)
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
            _posUIState.update { currentState->
                println("total: $total | grandTotal: $grandTotal | globalTax : $globalTax |remainingBalance : $remainingBalance")
                currentState.copy(
                    invoiceRounding= invoiceRounding,
                    grandTotal = grandTotal,
                    globalTax = globalTax,
                    remainingBalance = grandTotal
                )
            }

        }
    }

    private fun applyDiscountsIfEligible(cartList: MutableList<CRShoppingCartItem>) {
        val currentState=posUIState.value
        if (currentState.isDiscountGranted) {
            currentState.promoByPriceBreak.clear()
            currentState.promoByQuantity.clear()

            // Apply promotions to items in the cart
            cartList.forEach { item ->
                val promo = currentState.promotionDetails.firstOrNull { it.inventoryCode == item.stock.inventoryCode }
                    ?: PromotionDetails(id = 0)

                if (promo.promotionId != 0) {
                    item.promotionName = promo.promotionTypeName
                    /*val promoForName = currentState.promotions.firstOrNull { it.id.toInt() == promo.promotionId }
                        ?: Promotion(id = 0)
                    item.promotionName = promoForName.name*/
                    tryApplyPromo(promo, item)
                }
            }

            // Process price-break and quantity-based promotions
            val updatedCartList =  cartList.map { item ->
                when (item.promotion?.promotionTypeName) {
                    "PromotionByPriceBreak" -> {
                        applyPriceBreakPromotion(item, item.promotion)
                        item
                    }
                    "PromotionByQty" -> {
                        applyQuantityPromotion(item, item.promotion)
                        item
                    }
                    else -> item
                }
            }.toMutableList()
            updateSaleItem(updatedCartList)
        }
    }

    private fun applyQuantityPromotion(item: CRShoppingCartItem, promotion: PromotionDetails?) {
        with(_posUIState.value){
            val promoId = promotion?.promotionId
            // Check if promoBy quantity contains the promoId

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

                        // Create a new list to trigger recomposition
                        /*val updatedCartList = cartList.map { cartItem ->
                            if (cartItem.stock.id == item.id)
                            {
                                println("promotionItem :$cartItem")
                                item
                            }
                            else cartItem
                        }.toMutableList()

                        // Update the UI state with the new list
                        _posUIState.update { currentState ->
                            currentState.copy(cartList = updatedCartList)
                        }*/

                    }else {
                        // Quantity does not meet the promotion requirement
                        item.promotionActive = false
                        item.promotionByQuantity = false
                    }
                }

            }
        }
    }

    private fun applyPriceBreakPromotion(item: CRShoppingCartItem, promotion: PromotionDetails?) {
        with(_posUIState.value){
            val promoId = promotion?.promotionId
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

    private fun tryApplyPromo(promo: PromotionDetails, item: CRShoppingCartItem) {
        //println("Matched promo")
        when (promo.promotionTypeName) {
            "PromotionByQty" -> tryApplyPromoByQty(promo, item)
            "PromotionByPrice" -> tryApplyPromoByPrice(promo, item)
            "PromotionByPriceBreak" -> tryApplyPromoByPriceBreak(promo, item)
            else -> println("Unknown promotion type: ${promo.promotionTypeName}")
        }
    }

    private fun tryApplyPromoByQty(promo: PromotionDetails, item: CRShoppingCartItem){
        item.promotion=promo
        println("PromoTypeDetails: ${item.promotion}")
        val id = item.promotion?.promotionId ?: 0

        posUIState.value.run {
            if (!promoByQuantity.containsKey(id)) {
                promoByQuantity[id] = CRPromotionByQuantity()
                promoByQuantity[id]?.qty= 0.0
                promoByQuantity[id]?.items= mutableListOf()
            }

            promoByQuantity[id]?.let { promoData ->
                promoData.qty += item.qty
                promoData.promoQty = promo.qty
                promoData.amount = promo.amount
                val count = (promoData.qty) / (promoData.promoQty)
                val qty = promoData.promoQty
                promoData.remainingPromotionQty = count * qty
                val percentage: Double? = item.promotion?.promotionPerc?.let {
                    100 / it
                }
                promoData.percentage = percentage
                promoData.items.add(CRPromotionByQuantityItem(
                code= item.code, price=item.price, qty=item.qty, checked = false))
            }

        }
    }

    private fun tryApplyPromoByPrice(promo: PromotionDetails, item: CRShoppingCartItem) {
        item.promotion = promo
        println("PromoTypeDetails: ${item.promotion}")
        if (promo.qty <= item.qty) {
            item.promotionActive = true
            if (promo.promotionPerc!=null  && promo.promotionPerc > 0.0) {
                //item.promotion?.promotionPrice=promo.promotionPrice
                item.promotion?.promotionPrice = promo.price.let { price ->
                    price - (price * (promo.promotionPerc)) / 100
                }
            }
        } else {
            item.promotionActive = false
        }
    }

    private fun tryApplyPromoByPriceBreak(promo: PromotionDetails, item: CRShoppingCartItem){
        item.promotion = promo
        var quantity = 0.0
        var priceB = 0.0
        val id = item.promotion?.promotionId ?: 0
        posUIState.value.run{
            if (!promoByPriceBreak.containsKey(id)) {
                promoByPriceBreak[id] = CRPromotionByPriceBreak()
                promoByPriceBreak[id]?.qty= 0.0
            }

            promo.priceBreakPromotionAttribute?.forEach { element->
                val qty= promoByPriceBreak[id]?.qty?.plus(item.qty)
                if ((element.qty ?: 0.0) <= qty!!) {
                    quantity = element.qty ?: 0.0
                    priceB = element.price ?: 0.0
                }
            }

            promoByPriceBreak[id]?.apply {
                qty += item.qty
                promoQty = quantity
                price = priceB
            }
        }


    }

    private fun applyGlobalDiscount(subTotal: Double, globalDiscount: Double, isPercent: Boolean): Double {
        return if (globalDiscount > 0) {
            if (isPercent) {
                if (globalDiscount < 100.0) {
                    subTotal - ((subTotal * globalDiscount) / 100.0)
                } else subTotal
            } else {
                max(0.0, subTotal - globalDiscount) // Ensure total doesn't go negative
            }
        } else subTotal
    }

    private fun posRounding(n: Double, rounding: Double?): Double {
        if (rounding == 0.0) return n

        val toFix = if (n == n) {
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

    fun calculateDiscount(item: CRShoppingCartItem): String {
        return when {
            item.discount > 0 -> {
                if (item.currentDiscount > 0) {
                    "-${item.calculateDiscount()}${if (item.discountIsInPercent) "%" else _posUIState.value.currencySymbol}"
                } else {
                    "-${item.calculateDiscount()}${if (item.discountIsInPercent) "%" else _posUIState.value.currencySymbol}"
                }
            }
            item.currentDiscount > 0 -> {
                "-${item.currentDiscount.roundTo(2)}${if (item.discountIsInPercent) "%" else _posUIState.value.currencySymbol}"
            }
            else -> ""
        }
    }

    fun onNumberPadClick(symbol: String) {
        with(_posUIState.value){
            when (symbol) {
                "." -> if (!inputDiscount.contains('.')) {
                    updateEnterAmountValue(("$inputDiscount."))
                }
                "x" -> if (inputDiscount.isNotEmpty()) {
                    val newDiscount = inputDiscount.dropLast(1)
                    updateEnterAmountValue(newDiscount)
                }
                else -> {
                    val updatedDiscount = (inputDiscount + symbol)
                    updateEnterAmountValue(updatedDiscount)
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
                            if (discountValue < selectedProduct.price){
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
                }
            }
        }
    }

    private fun insertPosListItem(item: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val qty = if (item.qtyOnHand ==0.0) 1.0 else item.qtyOnHand
            val updatedItem = item.copy(qtyOnHand = qty)
             dataBaseRepository.insertOrUpdateScannedProduct(updatedItem)
            _posUIState.update {it.copy(isCallScannedItems= !it.isCallScannedItems)}
        }
    }

    fun holdCurrentSale(){
        viewModelScope.launch {
            _posUIState.update{state->
                val salesOnHold : MutableMap<Long, CRSaleOnHold> = hashMapOf()
                val saleHold= CRSaleOnHold(
                    ts= getCurrentDateAndTimeInEpochMilliSeconds(),
                    collectionId = getCurrentDateAndTimeInEpochMilliSeconds(),
                    grandTotal = state.grandTotal,
                    member = state.memberItem,
                    items = state.cartList
                )
                salesOnHold[saleHold.collectionId] = saleHold
                val updatedMap = (state.salesOnHold+salesOnHold).toMutableMap()
                dataBaseRepository.insertOrUpdateHoldSaleRecord(updatedMap)
                state.copy(
                    salesOnHold=updatedMap,
                    cartList = mutableListOf(),
                    memberItem = MemberItem(),
                    selectedMember = "Search Member"
                )

            }
        }
    }

    fun reCallHoldSale(cRSaleOnHold:CRSaleOnHold){
        viewModelScope.launch {
            _posUIState.update { currentState->
                dataBaseRepository.removeHoldSaleItemById(cRSaleOnHold.collectionId)
                val updatedMap= currentState.salesOnHold.toMutableMap()
                updatedMap.remove(cRSaleOnHold.collectionId)
                val updatedCartList = (currentState.cartList + cRSaleOnHold.items).toMutableList()

                currentState.copy(
                    cartList = updatedCartList,
                    salesOnHold = updatedMap,
                    memberItem = cRSaleOnHold.member?:MemberItem()
                )
            }
        }
    }

    fun removeHoldSale(collectionId:Long){
        viewModelScope.launch {
            _posUIState.update { currentState->
                dataBaseRepository.removeHoldSaleItemById(collectionId)
                val updatedMap= currentState.salesOnHold.toMutableMap()
                updatedMap.remove(collectionId)
                currentState.copy(
                    salesOnHold = updatedMap
                )
            }
        }
    }

    fun updateHoldSalePopupState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(showHoldSalePopup = value) }
        }
    }

    private fun updateLoader(value:Boolean){
        _posUIState.update { it.copy(isLoading = value) }
    }

    private fun updateCartLoader(value:Boolean){
        _posUIState.update { it.copy(showCartLoader = value) }
    }

    fun dismissErrorDialog(){
        viewModelScope.launch {
            _posUIState.update { it.copy(isError = false, errorMsg = "") }
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

    fun updateScreenDirection(isRtl: Boolean) {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isRtl = isRtl)
            }
        }
    }

    fun updateMenuDisplayFormat(isList: Boolean) {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(isList = isList)
            }
        }
    }

    fun selectCategory(categoryId: Int) {
        println("clicked_category:$categoryId")
        _posUIState.update {state->
            state.copy(
                selectedCategoryId = categoryId
            )
        }
        //updateSelectedCategoryProducts(categoryId)
    }

    private fun updateSelectedCategoryProducts(categoryId: Int) {
        viewModelScope.launch {
            val selectedCatProducts = mutableListOf<Stock>()
            _posUIState.update { state ->
                selectedCatProducts.addAll(state.menuProducts.filter { it.categoryId == categoryId })
                println("selectedCatProducts:$selectedCatProducts")
                updateMenuProducts(selectedCatProducts.sortedBy { it.sortOrder })
                state.copy(
                    selectedCategoryId = categoryId
                )
            }
        }
    }

    private fun updateCategories(categories: List<StockCategory>) {
        viewModelScope.launch{
            _posUIState.update {
                it.copy(
                    categories = categories.distinct(),
                    selectedCategoryId = categories.first().id
                )
            }
        }
    }

    private fun updateMenuProducts(products: List<Stock>) {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(
                    menuProducts = products.distinct(),
                    showCartLoader = false
                )
            }
        } }

    fun onProductItemClick(animatedProductCard: AnimatedProductCard) {
        viewModelScope.launch {
            val stock = animatedProductCard.product
            println("clicked_menu:$stock")
            addToCartItem(stock=stock)
        }
    }

     fun showAnimatedProductCard(card: AnimatedProductCard?) {
        viewModelScope.launch {
            _posUIState.update { state -> state.copy(animatedProductCard = card) }
            delay(150)
            _posUIState.update { state -> state.copy(animatedProductCard = null) }
        }
    }


    fun updateMemberDialogState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(isMemberDialog = value) }
        }
    }


    fun formatPriceForUI(amount: Double) :String{
      return  "${_posUIState.value.currencySymbol}${NumberFormatting().format(amount)}"
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
    
    //Promotion Dialog Code

    fun updateDiscountDialog(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(showDiscountDialog = value) }
        }
    }

    fun updateDiscount(promotion:Promotion){
        viewModelScope.launch {
            //updateGlobalDiscounts(isInPercent = promotion.promotionValueType==1, discount = promotion.amount)
            _posUIState.update { state->

                val finalTotal=if (promotion.promotionValueType==1) {
                    if (promotion.amount < 100.0) {
                        state.grandTotal - ((state.grandTotal * promotion.amount) / 100.0)
                    } else state.grandTotal
                } else {
                    max(0.0, state.grandTotal - promotion.amount) // Ensure total doesn't go negative
                }

                state.copy(grandTotal = finalTotal, globalDiscountIsInPercent = promotion.promotionValueType==1, globalDiscount = promotion.amount)

            }


        }
    }

    fun getDiscountValue():String{
        val state=posUIState.value
        return when(state.globalDiscountIsInPercent){
            true->{
                "${state.globalDiscount}%"
            }
            else ->{
                formatPriceForUI(state.globalDiscount /*+ (if(state.cartPromotionDiscount>0) state.cartPromotionDiscount else 0.0)*/)
            }
        }
    }

    private fun getDiscountInPercent() : Boolean{
        return when(_posUIState.value.selectedDiscountType){
            DiscountType.PERCENTAGE ->{
                true
            }
            DiscountType.FIXED_AMOUNT->{
                false
            }
        }
    }

    private fun getDiscountSymbol() : String{
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
                    showItemDiscountDialog = true,
                    selectedDiscountApplied = DiscountApplied.GLOBAL,
                    inputDiscount = ""
                )
            }
        }
    }

    fun onPriceItemClick(selectedItem: CRShoppingCartItem, index: Int) {
        viewModelScope.launch {
            _posUIState.update { it.copy(showItemDiscountDialog = true, selectedDiscountApplied = DiscountApplied.SUB_ITEMS,itemPosition=index, selectedProduct = selectedItem, inputDiscount = "") }
        }

    }

    fun dismissDiscountDialog() {
        viewModelScope.launch {
            _posUIState.update {
                it.copy(showItemDiscountDialog = false)
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

    fun updateCreateMemberDialogState(value:Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(isCreateMemberDialog = value) }
        }
    }


    fun updateClearCartDialogVisibility(value:Boolean){
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


    fun removedListItem(selectedItem: CRShoppingCartItem) {
        viewModelScope.launch {
            _posUIState.update { currentState ->
                val updatedCartList = currentState.cartList.filter { it.stock.id != selectedItem.stock.id }.toMutableList()
                currentState.copy(cartList = updatedCartList)
            }
            /*_posUIState.update { currentState ->
                //dataBaseRepository.removeScannedItemById(selectedItem.id.toLong())
                //currentState.copy(isCallScannedItems = !currentState.isCallScannedItems)
                currentState.cartList.map { item->
                    if(item.id==selectedItem.id){
                        currentState.cartList.remove(selectedItem)
                    }else
                        item
                }
                currentState.copy(cartList = )
            }*/
        }
    }

    fun removedScannedItem() {

        viewModelScope.launch {
            _posUIState.update { currentState ->
                currentState.copy(cartList = mutableListOf(), selectedMember = "Select Member", holdSaleCollections = hashMapOf(), isRemoveDialog = false)
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

    fun updatePaymentStatus(transactionAmount: Double) {
        //  println("transactionAmount $transactionAmount")
        _posUIState.update {
            it.copy(
                paymentFromLib = true,
                paymentFromLibAmount = transactionAmount
            )
        }
    }

    fun updatePaymentCollectorDialogVisibility(value: Boolean){
        viewModelScope.launch {
            _posUIState.update { it.copy(showPaymentCollectorDialog = value) }
        }
    }

    fun onPaymentClicked(item: PaymentMethod ) {
        viewModelScope.launch {
            _posUIState.update { currentState ->
                currentState.copy(
                    selectedPaymentTypesId=item.id,
                    selectedPayment = item,
                    startPaymentLib = true
                    //showPaymentCollectorDialog = true
                )
            }
        }
    }

    fun resetStartPaymentLibState() {
        viewModelScope.launch{
            _posUIState.update { state -> state.copy(startPaymentLib = false) }
        }
    }

    fun updateDeletePaymentModeDialog(value: Boolean) {
        _posUIState.update { state -> state.copy(showDeletePaymentModeDialog = value) }
    }
    fun updateSelectedPaymentToDeleteId(selectedPaymentToDelete: Int) {
        _posUIState.update { state -> state.copy(selectedPaymentToDelete = selectedPaymentToDelete) }
    }

    fun deletePayment() {
        val state=_posUIState.value
        val deletedPayment = state.createdPayments.find { it.id == state.selectedPaymentToDelete }
        if (deletedPayment != null) {
            _posUIState.update {
                it.copy(
                    createdPayments = it.createdPayments.toMutableList().apply { remove(deletedPayment) },
                    paymentTotal = ((it.paymentTotal - deletedPayment.amount)).roundTo(state.roundToDecimal),
                    remainingBalance = ((it.grandTotal - (it.paymentTotal - deletedPayment.amount))).roundTo(state.roundToDecimal)
                )
            }
        }
    }

    fun updateEnterAmountValue(value:String){
        viewModelScope.launch {
            _posUIState.update {
                // Convert input value to a number (e.g., Double for decimal numbers)
                val numericValue = value.toDoubleOrNull()
                var errorMessage: String? = null
                if (numericValue == null) {
                    // Handle invalid input (non-numeric values)
                    errorMessage = "Invalid input. Please enter a valid number."
                }else{
                    when {
                        numericValue < it.minValue -> {
                            errorMessage = "Value must be at least ${it.minValue}"
                        }
                        numericValue > it.grandTotal -> {
                            errorMessage = "Value must not exceed ${it.grandTotal}"
                        }
                    }
                }

                it.copy(inputDiscount = value, inputDiscountError = errorMessage )
            }
        }
    }

    fun applyPaymentValue(tenderAmount: Double) {
        println("callApplyPayment: $tenderAmount")
        val state=_posUIState.value
        if(tenderAmount!=0.0){
            //val paymentAmount=state.inputDiscount.toDouble()

            val existingPayment = state.createdPayments.find { it.id==state.selectedPayment.id }

            // Update the amount of the existing payment
            val updatedPayments = if (existingPayment != null) {
                state.createdPayments.map { payment ->
                    if (payment.id == state.selectedPayment.id) {
                        payment.copy(amount = (payment.amount + tenderAmount).roundTo())
                    } else {
                        payment
                    }
                }
            } else {
                // Add a new payment entry if it doesn't exist
                state.createdPayments + PaymentMethod(
                    id = state.selectedPayment.id,
                    name = state.availablePayments.find { it.id == state.selectedPaymentTypesId }?.name ?: "",
                    amount = tenderAmount
                )
            }.toMutableList()

            _posUIState.update {
                val updatedPayment = it.availablePayments.map { payment ->
                    if (payment.id == state.selectedPaymentTypesId){
                        payment.copy(isSelected=true)
                    }
                    else payment
                }

                val remaining = ((it.grandTotal - (it.paymentTotal + tenderAmount))).roundTo(2)
                it.copy(
                    paymentTotal = ((it.paymentTotal + tenderAmount)).roundTo(2),
                    remainingBalance = remaining,
                    createdPayments = updatedPayments,
                    availablePayments = updatedPayment,
                    /*showPaymentCollectorDialog = false,*/
                    isExecutePosSaving = remaining<=0.0
                )
            }
        }
    }

    fun resetPaymentLibValues(){
        viewModelScope.launch {
            _posUIState.update { state -> state.copy(paymentFromLib = false, paymentFromLibAmount = 0.0) }
        }
    }

    fun callTender(value: Boolean) {
        if (value) {
            createTicketRequest()
        }
    }

    private fun updateCashExchange(value: Boolean) {
        _posUIState.update { state -> state.copy(isCash = value) }
    }

    private fun updatePOSError(value: String) {
        _posUIState.update { state -> state.copy(errorMsg = value, isError = true) }
    }

    private fun updateUnSyncedInvoices(value: Long) {
        _posUIState.update { state -> state.copy(unSyncInvoices = value) }
    }



    private fun createTicketRequest() {
        if (employeeId.value== 0 || posUIState.value.location==null) {
            updatePOSError("POS Locked Exception")
            return
        }

        if (posUIState.value.createdPayments.isEmpty()) {
            updatePOSError("choose payment first")
            return
        }

        tender()
    }

    private fun tender() {
        viewModelScope.launch(Dispatchers.IO) {
            val posState=_posUIState.value
            if(posState.createdPayments.isNotEmpty()){
                posState.createdPayments.forEach {element->
                    if(element.acceptChange==true || element.name?.lowercase() == "cash"){
                        updateCashExchange(true)
                    }
                }
            }
            val posInvoice=tenderPosInvoice(posState)
            //constructReceiptAndPrint(posInvoice)
            executePosPayment(posInvoice)
        }
    }

    private suspend fun executePosPayment(posInvoice: PosInvoice) {
        try {
            networkRepository.createUpdatePosInvoice(CreatePOSInvoiceRequest(posInvoice = posInvoice)).collectLatest { apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateLoader(true)
                        updateSyncInProgress(true)
                    },
                    onSuccess = { apiData ->
                        if(apiData.result?.posInvoice != null)
                             holdCurrentSync(posInvoice,true)
                        else
                            holdCurrentSync(posInvoice, false)
                    },
                    onError = { errorMsg ->
                        holdCurrentSync(posInvoice, false)
                        println(errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            holdCurrentSync(posInvoice, false)
            val errorMsg="${e.message}"
            println(errorMsg)
        }
    }

    private fun tenderPosInvoice(posState: PosUIState) : PosInvoice{
        posState.run {
            val netCost=grandTotal.roundTo(4)
            val netTotal= grandTotalWithoutDiscount.roundTo(4)
            val subTotal= grandTotal.roundTo(4)
            val invoiceTotalValue= (subTotal + globalTax).roundTo(4)
            val invoiceNetDiscount = if (globalDiscountIsInPercent) {
                (netTotal.times(globalDiscount)).div(100.0)
            } else {
                globalDiscount
            }
            val itemDiscountPercentage= ((globalDiscount / netTotal) * 100).roundTo(4)
            val posInvoice=PosInvoice(
                tenantId = loginUser.tenantId?:0,
                employeeId = employeeId.value,
                locationId=location?.locationId?:0,
                locationCode = location?.code?:"",
                terminalId = location?.locationId?:0,
                terminalName = AppBasicsDetails().getAppName(),
                invoiceNo = "${location?.code}-C${getCurrentDateTime()}",
                isRetailWebRequest=true,
                invoiceDate= getCurrentDate(),
                invoiceTotal = cartTotal, //before Tax
                invoiceItemDiscount = itemDiscount,
                invoiceTotalValue= netCost,
                invoiceNetDiscountPerc= if(globalDiscountIsInPercent)globalDiscount else 0.0,
                invoiceNetDiscount= invoiceNetDiscount.roundTo(4),
                invoiceTotalAmount=netCost,
                invoiceSubTotal= (netCost - globalTax).roundTo(4),
                invoiceTax= globalTax.roundTo(4),
                invoiceRoundingAmount=0.0,
                invoiceNetTotal= netCost,
                invoiceNetCost= netTotal,
                paid= paymentTotal, //netCost
                isCancelled= false,
                memberId = selectedMemberId,
                posInvoiceDetails = cartList.map {cart->
                    var disc = 0.0
                    val itemPrice = cart.getFinalPrice().roundTo(4)
                    if (/*itemTotal > 0 && */cart.qty > 0) {
                    }
                    // Determine discount percentage based on global or item-level logic
                    val discountPercentage = if (globalDiscountIsInPercent) {
                        globalDiscount
                    } else {
                        itemDiscountPercentage
                    }
                    disc = (itemPrice * discountPercentage) / 100.0
                    val total = cart.qty * cart.price
                    val subTotal = total - disc - cart.calculateDiscount()
                    val itemTax = calculateGlobalTax(cart.salesTaxInclusive, subTotal, cart.tax)
                    PosInvoiceDetail(
                        productId = cart.stock.productId,
                        posInvoiceId = 0,
                        inventoryCode = cart.stock.inventoryCode,
                        inventoryName = cart.stock.name,
                        qty = cart.qty.toInt(),
                        price = cart.price,
                        total = total,
                        totalAmount = itemPrice,
                        totalValue = itemPrice,
                        netTotal = (itemPrice - disc).roundTo(4),
                        netCost = itemPrice,
                        netDiscount = disc,
                        subTotal = (subTotal - itemTax).roundTo(4),
                        itemDiscountPerc = if (cart.discountIsInPercent) cart.discount else disc,
                        itemDiscount = cart.calculateDiscount(),
                        averageCost = itemPrice,
                        roundingAmount = 0.0,
                        tax = itemTax.roundTo(4),
                        taxPercentage = cart.tax
                    )
                }.toList(),
                posPayments = createdPayments.map { payment->
                    PosPayment(
                        posInvoiceId= 0,
                        paymentTypeId = payment.id,
                        amount = payment.amount,
                        name = payment.name?:""
                    )
                }.toList(),
                qty = quantityTotal.toInt() ,
                customerName = if(selectedMemberId==0) "N/A" else selectedMember,
                address1 = location?.address1?:"" ,
                address2 = location?.address2?:"" ,
            )

            return posInvoice
        }
    }

    private fun holdCurrentSync(posInvoice: PosInvoice, isSync: Boolean) {
        viewModelScope.launch {
            try {
                dataBaseRepository.addUpdatePendingSales(PendingSaleDao(
                    posInvoice = posInvoice,
                    isDbUpdate = posInvoice.pendingInvoices>0,
                    isSynced = isSync
                ))
                //update qty
                //dataBaseRepository.updateProductStockQuantity(posInvoice)
                /*dataBaseRepository.getAllPendingSaleRecordsCount().collectLatest { pendingCount->
                    updateUnSyncedInvoices(pendingCount)
                }*/
                //constructReceiptAndPrint(posInvoice)
                constructReceiptAndPrintTemplate(posInvoice)
                //constructReceiptAndPrint(posInvoice)
                updateSales()
                //syncStockQuantity()
                //syncInventory()
                clearSale()

            }catch (ex:Exception){
                val errorMsg="Error Saving Data \n${ex.message}"
                updatePOSError(errorMsg)
                updateLoader(false)
                updateSyncInProgress(false)
            }
        }

    }

    private fun constructReceiptAndPrint(ticket: PosInvoice){
        updatePaymentInvoiceState(ticket)

        val state = posUIState.value
        viewModelScope.launch {
            var textToPrint:String=defaultTemplate
            syncPrintTemplate(TemplateType.POSInvoice)
            printerTemplates.collectLatest {templateList->
                templateList?.map { template->
                    textToPrint=template.template?: defaultTemplate
                }
                val templateRenderer = TemplateRenderer()
                val renderedTemplate = templateRenderer.renderInvoiceTemplate(textToPrint, prepareData(ticket,state))
                 println("Receipt_Template : $renderedTemplate")
                connectAndPrint(renderedTemplate)
            }
        }
    }

    private fun constructReceiptAndPrintTemplate(ticket: PosInvoice){
        updatePaymentInvoiceState(ticket)
        viewModelScope.launch {
           connectAndPrintTemplate(ticket)
        }
    }


    private fun prepareData(ticket: PosInvoice, state: PosUIState): Map<String, Any?> {
        val currencySymbol=state.currencySymbol

        val row = ticket.posInvoiceDetails?.mapIndexed { index, items ->
            ItemData(index+1,
                items.inventoryName,
                items.qty.toDouble(),
                formatAmountForPrint(items.price, currencySymbol),
                formatAmountForPrint(items.qty.times(items.price),currencySymbol))
        }?: emptyList()


        val data = mapOf(
            "invoice.invoiceNo" to ticket.invoiceNo,
            "invoice.invoiceDate" to ticket.invoiceDate,
            "invoice.terms" to  ticket.posPayments?.get(0)?.name,
            "invoice.customerName" to if(state.selectedMember=="Select Member") "" else state.selectedMember,
            "customer.address1" to state.location?.address1,
            "customer.address2" to state.location?.address2,
            "items" to row,
            "invoice.qty" to row.size,
            "invoice.invoiceSubTotal" to formatAmountForPrint(ticket.invoiceSubTotal,currencySymbol),
            "invoice.tax" to formatAmountForPrint(ticket.invoiceTax,currencySymbol),
            "invoice.netTotal" to formatAmountForPrint(ticket.invoiceNetTotal,currencySymbol),
            "customer.balanceAmount" to  formatAmountForPrint(ticket.invoiceNetTotal, currencySymbol),
        )

        return data
    }

    private fun connectAndPrint(textToPrint: String) {
        viewModelScope.launch {
            dataBaseRepository.getPrinter().collect { printer ->
                if(printer!=null){
                    PrinterServiceProvider().connectPrinterAndPrint(
                        printers = printer,
                        printerType = when (printer.printerType) {
                            1L -> {
                                PrinterType.Ethernet
                            }

                            2L -> {
                                PrinterType.USB
                            }

                            3L -> {
                                PrinterType.Bluetooth
                            }

                            else -> {
                                PrinterType.Bluetooth
                            }
                        },
                        textToPrint = textToPrint
                    )
                }else{
                   //Show Message that your device is not connected
                    _posUIState.update { it.copy(isError = true,errorMsg = "add printer setting") }
                }
            }
        }
    }

    private fun connectAndPrintTemplate(posInvoice: PosInvoice) {
        val finalTextToPrint = PrinterServiceProvider().getPrintTextForReceiptTemplate(posInvoice, defaultTemplate2)
        println("finalText $finalTextToPrint")
        viewModelScope.launch {
            dataBaseRepository.getPrinter().collect { printer ->
                if(printer!=null){
                    val finalTextToPrint = PrinterServiceProvider().getPrintTextForReceiptTemplate(posInvoice, defaultTemplate2)
                    println("finalTextToPrint :$finalTextToPrint")

                    PrinterServiceProvider().connectPrinterAndPrint(
                        printers = printer,
                        printerType = when (printer.printerType) {
                            1L -> {
                                PrinterType.Ethernet
                            }

                            2L -> {
                                PrinterType.USB
                            }

                            3L -> {
                                PrinterType.Bluetooth
                            }
                            else -> {
                                PrinterType.Bluetooth
                            }
                        },
                        textToPrint = finalTextToPrint
                    )
                }else{
                   //Show Message that your device is not connected
                    _posUIState.update { it.copy(isError = true,errorMsg = "add printer setting") }
                }
            }
        }
    }

    private fun updatePaymentInvoiceState(transaction: PosInvoice) {
        _posInvoice.update { transaction }
    }

    fun updatePaymentSuccessDialog(result: Boolean) {
        _posUIState.update { state -> state.copy(showPaymentSuccessDialog = result) }
    }

    private fun onPaymentClose() {
        _posUIState.update { state -> state.copy(isPaymentClose = true) }
    }

     fun clearSale(){
         _posInvoice.update { PosInvoice() }
         _posUIState.update { PosUIState() }
         onPaymentClose()
    }


    // Detects if a query is likely a code (numeric and shorter than a typical name)
    private fun isCode(query: String): Boolean {
        return query.all { it.isDigit() } /*&& query.length <= 10 // Adjust length if needed*/
    }

    private fun filterListByCode(query: String) {
        viewModelScope.launch {
            val filteredList = _posUIState.value.dialogStockList.filter {
                it.barcode.contains(query) || it.productCode.contains(query)
            }
            if (filteredList.isNotEmpty()) {
                insertPosListItem(filteredList[0]) // Add only the first matched item
            }
        }
    }

    fun resetScreenState(){
        viewModelScope.launch {
            _posUIState.update { PosUIState() }
        }
    }

}


