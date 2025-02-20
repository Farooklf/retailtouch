package com.hashmato.retailtouch.domain.repositories

import com.hashmato.retailtouch.domain.PreferencesRepository
import com.hashmato.retailtouch.domain.SqlRepository
import com.hashmato.retailtouch.domain.model.employee.POSEmployee
import com.hashmato.retailtouch.domain.model.employee.EmployeesResponse
import com.hashmato.retailtouch.domain.model.employee.EmployeesRights
import com.hashmato.retailtouch.domain.model.employee.POSEmployeeRight
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSaleDao
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSale
import com.hashmato.retailtouch.domain.model.products.POSProduct
import com.hashmato.retailtouch.domain.model.products.Stock
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.location.LocationResponse
import com.hashmato.retailtouch.domain.model.login.AuthenticateDao
import com.hashmato.retailtouch.domain.model.login.LoginResponse
import com.hashmato.retailtouch.domain.model.memberGroup.MemberGroupDao
import com.hashmato.retailtouch.domain.model.memberGroup.MemberGroupResponse
import com.hashmato.retailtouch.domain.model.members.Member
import com.hashmato.retailtouch.domain.model.members.MemberResponse
import com.hashmato.retailtouch.domain.model.menu.CategoryDao
import com.hashmato.retailtouch.domain.model.menu.CategoryResponse
import com.hashmato.retailtouch.domain.model.menu.MenuDao
import com.hashmato.retailtouch.domain.model.paymentType.PaymentTypeDao
import com.hashmato.retailtouch.domain.model.paymentType.PaymentMethod
import com.hashmato.retailtouch.domain.model.paymentType.PaymentTypeResponse
import com.hashmato.retailtouch.domain.model.printer.PrinterDao
import com.hashmato.retailtouch.domain.model.printer.PrinterScreenState
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.GetPosInvoiceResult
import com.hashmato.retailtouch.domain.model.productBarCode.Barcode
import com.hashmato.retailtouch.domain.model.productBarCode.BarcodeDao
import com.hashmato.retailtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.hashmato.retailtouch.domain.model.productLocations.ProductLocationDao
import com.hashmato.retailtouch.domain.model.productLocations.ProductLocationResponse
import com.hashmato.retailtouch.domain.model.products.CRSaleOnHold
import com.hashmato.retailtouch.domain.model.products.PosInvoice
import com.hashmato.retailtouch.domain.model.products.ProductDao
import com.hashmato.retailtouch.domain.model.products.ProductItem
import com.hashmato.retailtouch.domain.model.products.ProductWithTaxByLocationResponse
import com.hashmato.retailtouch.domain.model.products.SaleOnHoldRecordDao
import com.hashmato.retailtouch.domain.model.products.ScannedProductDao
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionResult
import com.hashmato.retailtouch.domain.model.promotions.Promotion
import com.hashmato.retailtouch.domain.model.promotions.PromotionDao
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetailsDao
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.hashmato.retailtouch.domain.model.login.RTLoginUser
import com.hashmato.retailtouch.domain.model.menu.StockCategory
import com.hashmato.retailtouch.utils.AppConstants.SYNC_SALES_ERROR_TITLE
import com.hashmato.retailtouch.utils.DateTimeUtils.getCurrentDateAndTimeInEpochMilliSeconds
import com.hashmato.retailtouch.utils.DateTimeUtils.getDateFromApi
import com.hashmato.retailtouch.utils.DateTimeUtils.parseDateFromApi
import com.hashmato.retailtouch.utils.DateTimeUtils.parseDateFromApiUTC
import com.hashmato.retailtouch.utils.DateTimeUtils.parseDateTimeFromApiStringUTC
import com.hashmato.retailtouch.utils.DoubleExtension.calculatePercentage
import com.hashmato.retailtouch.utils.PaperSize
import com.hashmato.retailtouch.utils.PrinterType
import com.hashmato.retailtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
import comhashmatoretailtouchsqldelight.Printers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class DataBaseRepository: KoinComponent {
    private val dataBaseRepository: SqlRepository by inject()
    private val preferences: PreferencesRepository by inject()

    // Create a custom CoroutineScope
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    //insert

    suspend fun insertUser(
        loginResponse: LoginResponse,
        finalUrl: String,
        tenant: String,
        username: String,
        password: String
    ) {
        withContext(Dispatchers.IO) {
            // If no duplicate is found, insert the user
            val authenticateDao = AuthenticateDao(
                userId = loginResponse.userId,
                tenantId = loginResponse.tenantId ?: -1,
                userName = username,
                serverURL = finalUrl,
                tenantName = tenant,
                password = password,
                isLoggedIn = true,
                isSelected = true,
                loginDao = loginResponse
            )

            dataBaseRepository.insertAuthentication(authenticateDao)
        }
    }

    suspend fun insertLocation(
        locationResponse: LocationResponse
    ) {
        withContext(Dispatchers.IO) {
            // If no duplicate is found, insert the user
            if (locationResponse.result.items.isNotEmpty()) {
                clearExistingLocations()
                locationResponse.result.items.forEachIndexed { index, location ->
                    val mLocationDao =
                        Location(
                            locationId = location.id ?: 0,
                            menuId = location.menuId?:0,
                            name = location.name ?: "",
                            code = location.code ?: "",
                            country = location.country ?: "",
                            address1 = location.address1 ?: "",
                            address2 = location.address2 ?: "",
                            isSelected = location.id==getLocationId()
                        )
                    dataBaseRepository.insertLocation(mLocationDao)
                }
            }
        }
    }

    suspend fun insertEmployees(
        employeesResponse: EmployeesResponse
    ) {
        withContext(Dispatchers.IO) {
            //dataBaseRepository.updatePOSEmployee()
            employeesResponse.result.items.forEach { employee ->
                val mPOSEmployee = POSEmployee(
                        employeeId = employee.id,
                        employeeName = employee.name,
                        employeeCode = employee.employeeCode,
                        employeeRoleName = employee.employeeRoleName,
                        employeePassword = employee.password,
                        employeeCategoryName = employee.employeeCategoryName ?: "",
                        employeeDepartmentName = employee.employeeDepartmentName ?: "",
                        isAdmin = employee.isAdmin,
                        isDeleted = employee.isDeleted,
                        isPosEmployee = false,
                    )
                dataBaseRepository.insertEmployee(mPOSEmployee)
            }
        }
    }


    suspend fun insertEmpRole(
        employeesResponse: EmployeesResponse
    ) {
        withContext(Dispatchers.IO) {
            employeesResponse.result.items.forEach { employee ->
                val mPOSEmployee =
                    POSEmployee(
                        employeeId = employee.id,
                        employeeName = employee.name,
                        isAdmin = employee.isAdmin,
                        isDeleted = employee.isDeleted
                    )
                dataBaseRepository.insertEmpRole(mPOSEmployee)
            }
        }
    }

    suspend fun insertEmpRights(
        employeesResponse: EmployeesRights
    ) {
        withContext(Dispatchers.IO) {
            employeesResponse.result.items.forEach {
                val employee=it.employeeRole
                if(employee!=null){
                    val mPOSEmployeeRight = POSEmployeeRight(
                            id = employee.id?:0,
                            name = employee.name,
                            isAdmin = employee.isAdmin,
                            grantedPermissionNames = it.grantedPermissionNames,
                            restrictedPermissionNames = it.restrictedPermissionNames,
                            permissions = it.permissions
                        )
                    dataBaseRepository.insertEmpRights(mPOSEmployeeRight)
                }
            }
        }
    }

    suspend fun insertMembers(
        response: MemberResponse
    ) {
        try {
            withContext(Dispatchers.IO) {
                response.result?.items?.forEach { item ->
                    val dao = Member(
                        memberId = item.id.toLong(),
                        name = item.name,
                        memberCode = item.memberCode?:"",
                        mobileNo = item.mobileNo?:"",
                        email = item.email?:"",
                        address1 = item.address1?:"",
                        address2 = item.address2?:"",
                        address3 = item.address3?:"",
                        postalCode = item.postalCode?:"",
                        locationName = item.locationName?:"",
                        active = item.active?:false,
                    )
                    dataBaseRepository.insertMembers(dao)
                }
            }
        } catch (ex: Exception) {
            println("EXCEPTION MEMBER GROUP :${ex.message}")
        }
    }

    suspend fun insertMemberGroup(response: MemberGroupResponse) {
        withContext(Dispatchers.IO) {
            response.result?.items?.forEach { item ->
                val dao = MemberGroupDao(
                    memberGroupId = item.id.toLong(),
                    rowItem = item
                )
                dataBaseRepository.insertMemberGroup(dao)
            }
        }
    }

    suspend fun insertUpdateInventory(
        response: ProductWithTaxByLocationResponse,
        lastSyncDateTime: String? = null,
        stockQtyMap: Map<Int, Double?>
    ) {
        try {
            withContext(Dispatchers.IO) {
                response.result?.items?.map { item ->
                    val dao = ProductDao(
                        productId = item.id.toLong(),
                        product = POSProduct(
                            id = item.id.toLong(),
                            name = item.name?:"",
                            productCode = item.inventoryCode?:"",
                            tax = item.taxPercentage?:0.0,
                            barcode = item.barCode?:"",
                            price = if (item.specialPrice== 0.0) item.price?:0.0 else item.specialPrice?:0.0,
                            qtyOnHand = stockQtyMap[item.id] ?: 0.0,
                            image = if (!item.image.isNullOrEmpty()) "${getBaseUrl()}${item.image}".replace(
                                "\\",
                                "/"
                            ) else getBaseUrl()
                        )
                    )
                    if (lastSyncDateTime == null) {
                        dataBaseRepository.insertProduct(dao)
                    } else {
                        dataBaseRepository.updateProduct(dao)
                    }

                }
            }

        } catch (ex: Exception) {
            println("EXCEPTION INVENTORY: ${ex.message}")
        }
    }

    suspend fun insertCategories(
        response: CategoryResponse
    ) {
        withContext(Dispatchers.IO) {
            clearCategory()
            response.result.items.forEach { cat ->
                val dao = CategoryDao(
                    categoryId = cat.id?.toLong()?:0,
                    stockCategory = StockCategory(
                        id=cat.id?:0,
                        menuId = cat.menuId?:0,
                        sortOrder = cat.sortOrder?:0,
                        name = cat.name?:"",
                        fgColor = cat.foreColor?:"",
                        bgColor = cat.backColor?:"",
                        itemFgColor = cat.itemForeColor?:"",
                        itemBgColor = cat.itemBackColor?:"",
                        categoryRowCount=cat.categoryRowCount?:0,
                        itemColumnCount =cat.itemColumCount?:0,
                        itemRowCount =cat.itemRowCount?:0,
                        ))
                //= cat.copy(categoryRowCount = cat.categoryRowCount ?: 1)
                dataBaseRepository.insertStockCategories(dao)
            }
        }
    }

    suspend fun insertUpdateProductQuantity(
        response: ProductLocationResponse,
        lastSyncDateTime: String? = null
    ) {
        try {
            withContext(Dispatchers.IO) {
                clearProductQuantity()
                response.result?.items?.forEach { item ->
                    val dao = ProductLocationDao(
                        productLocationId = item.productId.toLong(),
                        rowItem = item,
                    )
                    if(lastSyncDateTime==null)
                    dataBaseRepository.insertProductLocation(dao)
                    else
                        dataBaseRepository.updateProductLocation(dao)
                }
            }

        } catch (ex: Exception) {
            println("EXCEPTION STOCK: ${ex.message}")
        }
    }

   suspend fun insertNewStock(
        newStock: List<Stock>
    ) {
        try {
            withContext(Dispatchers.IO) {
                //clearStocks()
                newStock.map { item ->
                    //println("InventoryCode: ${item.inventoryCode}")
                    val dao = MenuDao(
                        productId = item.id,
                        menuProductItem = item
                    )
                    dataBaseRepository.insertStocks(dao)
                }
            }
        } catch (ex: Exception) {
            println("EXCEPTION STOCK: ${ex.message}")
        }
    }

    suspend fun insertUpdateBarcode(
        apiResponse: ProductBarCodeResponse,
        lastSyncDateTime: String? = null,
    ) {
        try {
            withContext(Dispatchers.IO) {
                clearBarcode()
                apiResponse.result.items.forEach { code ->
                    if (code.productId != 0L && !code.code.isNullOrEmpty()) {
                        val mBarcodeDao = BarcodeDao(
                            barcodeId = code.productId,
                            barcode = code
                        )
                        if (lastSyncDateTime == null) {
                            dataBaseRepository.insertProductBarcode(mBarcodeDao)
                        } else
                            dataBaseRepository.updateProductBarcode(mBarcodeDao)

                        println("inventory barcode insertion")
                    }
                }
            }
        } catch (ex: Exception) {
            println("EXCEPTION BARCODE: ${ex.message}")
        }

    }

    suspend fun insertPromotions(
        response: GetPromotionResult
    ) {
        try {
            withContext(Dispatchers.IO) {
                clearPromotion()
                response.result?.map { item ->
                    if(item!=null){
                        val dao = PromotionDao(
                            promotionId = item.id,
                            inventoryCode = item.inventoryCode?:"",
                            promotion = Promotion(
                                id = item.id,
                                promotionValueType=item.promotionValueType?:0,
                                name = item.name?:"",
                                inventoryCode = item.inventoryCode?:"",
                                promotionType = item.promotionType?:0,
                                promotionTypeName = item.promotionTypeName,
                                qty = item.qty?:0.0,
                                amount = item.amount?:0.0,
                                startDate = item.startDate.parseDateFromApi(),
                                endDate = item.endDate.parseDateFromApi(),
                                startHour1 = item.startHour1?:0,
                                startHour2 = item.startHour2?:0,
                                endHour1 = item.endHour1?:0,
                                endHour2 = item.endHour2?:0,
                                startMinute1 = item.startMinute1?:0,
                                startMinute2 = item.startMinute2?:0,
                                endMinute1 = item.endMinute1?:0,
                                endMinute2 = item.endMinute2?:0,
                                priceBreakPromotionAttribute = parsePriceBreakPromotionAttributes(item.priceBreakPromotionAttribute)
                            ))
                        dataBaseRepository.insertPromotions(dao)
                    }
                }
            }
        }catch (ex: Exception){
            println("EXCEPTION PROMOTION: ${ex.message}")
        }
    }

    suspend fun insertPromotionDetails(promotionDetails: PromotionDetails) {
        withContext(Dispatchers.IO) {
            //val maxId = dataBaseRepository.getCount().first()
            //val newId = maxId + 1

            dataBaseRepository.insertPromotionDetails(
                PromotionDetailsDao(
                    id = promotionDetails.id,
                    promotionDetails=promotionDetails)
            )
        }
    }

    suspend fun insertPaymentType(
        response: PaymentTypeResponse
    ) {
        try {
            withContext(Dispatchers.IO) {
                response.result?.items?.forEach { item ->
                    val dao = PaymentTypeDao(
                        paymentId = item.id.toLong(),
                        rowItem = item
                    )
                    dataBaseRepository.insertPaymentType(dao)
                }
            }

        } catch (ex: Exception) {
            println("EXCEPTION PAYMENT: ${ex.message}")
        }
    }

    suspend fun insertNewLatestSales(
        response: GetPosInvoiceResult
    ) {
        try {
            withContext(Dispatchers.IO) {
                //clearedPOSSales()
                response.result?.items?.forEach { item ->
                    val dao = SaleRecord(
                        id = item.id,
                        count  = response.result.totalCount ?: 0,
                        receiptNumber = item.invoiceNo?:"",
                        amount = item.invoiceNetTotal?:0.0,
                        date = parseDateFromApiUTC(item.invoiceDate),
                        deliveryDate = item.deliveryDateTime.getDateFromApi(),
                        creationDate = parseDateTimeFromApiStringUTC(item.creationTime),
                        remarks = item.remarks?:"",
                        delivered = item.isDelivered?:false,
                        delivery = item.deliveryDateTime!=null,
                        rental = item.isRental?:false,
                        rentalCollected = item.isRentalCollected?:false,
                        selfCollection = item.selfCollection?:false,
                        type = item.type?:0,
                        status = if (item.isCancelled == true) 666 else item.status?:0,
                        memberId = item.memberId?.toLong()?:0,
                        memberName = item.memberName?:"",
                        items = item

                    )
                    dataBaseRepository.insertLatestSales(dao)
                }
            }

        } catch (ex: Exception) {
            println("$SYNC_SALES_ERROR_TITLE :${ex.message}")
        }
    }




    suspend fun insertOrUpdateScannedProduct(item: POSProduct) {
        // Switch to the IO dispatcher to perform the database operation
        withContext(Dispatchers.IO) {
            val subtotal = item.price.times(item.qtyOnHand) ?: 0.0
            val taxValue = subtotal.calculatePercentage(item.tax)
            val dao = ScannedProductDao(
                productId = item.id?.toLong()?:0,
                name = item.name ?: "",
                inventoryCode = item.productCode ?: "",
                barCode = item.barcode ?: "",
                qty = item.qtyOnHand,
                price = item.price ?: 0.0,
                subtotal = subtotal,
                discount = item.itemDiscount,
                taxValue = taxValue,
                taxPercentage = item.tax
            )
            dataBaseRepository.insertScannedProduct(dao)
        }
    }

    suspend fun insertOrUpdateHoldSaleRecord(
        response: MutableMap<Long, CRSaleOnHold>
    ) {
        try {
            withContext(Dispatchers.IO) {
                response.map { (key, value) ->
                    val holdRecord=SaleOnHoldRecordDao(
                        id=key,
                        item=value
                    )
                    dataBaseRepository.insertHoldSaleRecord(holdRecord)
                }
            }
        } catch (ex: Exception) {
            println("EXCEPTION :${ex.message}")
        }
    }

    suspend fun addUpdatePendingSales(data: PendingSaleDao){
        withContext(Dispatchers.IO) {
             val posInvoice=data.posInvoice
             val id =  if(data.isDbUpdate) data.posSaleId else getCurrentDateAndTimeInEpochMilliSeconds()
             //println("posInvoicesId : $id")
             val pendingSaleRecord=PendingSale(
                 id=id,
                 locationId = posInvoice.locationId?:0,
                 tenantId = posInvoice.tenantId?:0,
                 terminalName = posInvoice.terminalName?:"",
                 locationCode = posInvoice.locationCode?:"",
                 isRetailWebRequest = posInvoice.isRetailWebRequest?:false,
                 invoiceTotal = posInvoice.invoiceTotal?:0.0,
                 invoiceItemDiscount = posInvoice.invoiceItemDiscount,
                 invoiceTotalValue = posInvoice.invoiceTotalValue,
                 invoiceNetDiscountPerc = posInvoice.invoiceNetDiscountPerc,
                 invoiceNetDiscount = posInvoice.invoiceNetDiscount,
                 invoiceTotalAmount = posInvoice.invoiceTotalAmount,
                 invoiceSubTotal = posInvoice.invoiceSubTotal,
                 invoiceNetTotal = posInvoice.invoiceNetTotal,
                 invoiceNetCost = posInvoice.invoiceNetCost,
                 paid = posInvoice.paid,
                 employeeId = posInvoice.employeeId?:0,
                 invoiceNo = posInvoice.invoiceNo?:"",
                 invoiceDate = posInvoice.invoiceDate?:"",
                 grandTotal = posInvoice.invoiceTotalAmount,
                 globalTax = posInvoice.invoiceTax,
                 remarks = posInvoice.remarks?:"",
                 deliveryDateTime = posInvoice.deliveryDateTime?:"",
                 type = posInvoice.type?:0,
                 globalDiscount = posInvoice.invoiceNetDiscount,
                 invoiceRoundingAmount = posInvoice.invoiceRoundingAmount,
                 status = posInvoice.status?:0,
                 qty = posInvoice.qty,
                 memberId = posInvoice.memberId?:0,
                 memberName = posInvoice.customerName,
                 address1 = posInvoice.address1,
                 address2 = posInvoice.address2,
                 posPaymentConfigRecord = posInvoice.posPayments,
                 posInvoiceDetailRecord = posInvoice.posInvoiceDetails,
                 isSynced = data.isSynced
            )
            if(!data.isDbUpdate){
                dataBaseRepository.insertPosInvoiceSales(pendingSaleRecord)
            }else{
                dataBaseRepository.updatePosSales(pendingSaleRecord)
            }
            /*posInvoice.posPayments?.map { payment ->
                val paymentRecord  = PosSalePayment(
                    posPaymentRecordId = id,
                    posInvoiceId = payment.posInvoiceId,
                    paymentTypeId = payment.paymentTypeId,
                    amount = payment.amount
                )
                dataBaseRepository.insertPosConfiguredPaymentRecord(paymentRecord)
            }*/

           /* posInvoice.posInvoiceDetails?.forEach { invoice->
              val posInvoiceDetails= PosSaleDetails(
                  posPaymentRecordId = id, 
                  productId=invoice.productId,
                 inventoryCode=invoice.inventoryCode,
                 inventoryName=invoice.inventoryName,
                 qty=invoice.qty.toDouble(),
                 price=invoice.price,
                 total=invoice.total,
                totalAmount=invoice.totalAmount,
                subTotal=invoice.subTotal,
                itemDiscountPerc=invoice.itemDiscountPerc,
                itemDiscount=invoice.itemDiscount,
                finalPrice=invoice.total,
                discount=invoice.netDiscount,
                tax=invoice.tax,
                taxPercentage=invoice.taxPercentage
              )
                dataBaseRepository.insertPosDetailsRecord(posInvoiceDetails)
            }*/

        }
        }

    suspend fun updateSalesAsSynced(id:Long){
        withContext(Dispatchers.IO){
            dataBaseRepository.updateSynced(id)
        }
    }

    suspend  fun insertOrUpdatePrinter(printer: PrinterScreenState) {
        withContext(Dispatchers.IO){
            val requestDao=PrinterDao(
                printerId = printer.printerId,
                printerStationName = printer.printerStationName,
                printerName = printer.printerName?:"",
                numbersOfCopies = printer.numbersOfCopies,
                paperSize =  when (printer.paperSize) {
                    PaperSize.Size58mm -> 58
                    PaperSize.Size80mm -> 80
                },
                isReceipts = printer.isReceipts,
                isOrders = printer.isOrders,
                isRefund = printer.isRefund,
                isPrinterEnable = printer.isPrinterEnable,
                printerType = when (printer.printerType) {
                    PrinterType.Ethernet -> 1
                    PrinterType.USB -> 2
                    PrinterType.Bluetooth -> 3
                },
                networkIpAddress = printer.networkIpAddress,
                selectedBluetoothAddress = printer.selectedBluetoothAddress,
                selectedUsbId = printer.selectedUsbId,
                templateId=printer.printerTemplates.id?:0L
            )
            if(printer.printerId==0L){
                dataBaseRepository.insertPrinter(requestDao)
            }else{
                dataBaseRepository.updatePrinter(requestDao)
            }
        }
    }

    //Update

    suspend fun updateProductStockQuantity(posInvoice: PosInvoice){
        withContext(Dispatchers.IO) {
            posInvoice.posInvoiceDetails?.map { item ->
                val oldQty= dataBaseRepository.getProductQty(item.inventoryCode).first()
                val newQty=if(oldQty>0){
                    oldQty-item.qty
                }else{
                    oldQty+item.qty
                }
                dataBaseRepository.updateProductQuantity(item.inventoryCode,newQty)
            }
        }
    }

    suspend fun updateScannedProduct(updatedItem: ProductItem) {
        // Switch to the IO dispatcher to perform the database operation
        withContext(Dispatchers.IO) {
            val dao = ScannedProductDao(
                productId = updatedItem.id.toLong(),
                qty = updatedItem.qtyOnHand,
                discount = updatedItem.itemDiscount,
                subtotal = updatedItem.cartTotal ?: 0.0,
                taxValue = updatedItem.taxValue ?: 0.0
            )
            dataBaseRepository.updateScannedProduct(dao)
        }
    }


    //fetch
    fun getHoldSales() : Flow<List<CRSaleOnHold>>{
        return dataBaseRepository.getAllHoldSaleRecord().flowOn(Dispatchers.IO)
    }
    fun getAllPendingSaleRecordsCount() : Flow<Long>{
        return dataBaseRepository.getAllPendingSalesCount().flowOn(Dispatchers.IO)
    }

    fun getPosPendingSales() :Flow<List<PendingSale>>{
        return dataBaseRepository.getPendingSaleRecords().flowOn(Dispatchers.IO)
    }

    fun getAuthUser(): Flow<AuthenticateDao> {
        return dataBaseRepository.getAllAuthentication()
    }

    fun getRTLoginUser(): Flow<RTLoginUser> {
        return dataBaseRepository.getAuthUser().flowOn(Dispatchers.IO)
    }

    suspend fun getAuthUser(id:Long): AuthenticateDao {
        return dataBaseRepository.selectUserByUserId(id).first()
    }

    fun getLocation(): Flow<Location?> {
        return dataBaseRepository.getSelectedLocation()
    }

    fun getSelectedLocation(): Flow<Location?> {
        return dataBaseRepository.getSelectedLocation().flowOn(Dispatchers.IO)
    }

    fun getEmployee(empCode: String): Flow<POSEmployee?> {
        return dataBaseRepository.getEmployeeByCode(empCode)
    }

    suspend fun getEmployeeByCode(empCode: String): POSEmployee? {
        return dataBaseRepository.getEmployeeByCode(empCode.trim()).first()
    }

    fun getEmpRights(): Flow<List<POSEmployeeRight>> {
        return dataBaseRepository.getAllEmpRights()
    }

    fun getBarcode(code: String) : Flow<Barcode?> {
      return dataBaseRepository.getItemByBarcode(code)
    }

    fun getProductById(id: Long) : Flow<POSProduct?> {
         return dataBaseRepository.getProductById(id)
    }

    fun getProductByCode(code: String): Flow<POSProduct?> {
        return dataBaseRepository.getProductByCode(code)
    }

    fun getStockCategories(): Flow<List<StockCategory>> {
        return dataBaseRepository.getAllCategories().flowOn(Dispatchers.IO)
    }


    fun getMember(): Flow<List<Member>> {
        return dataBaseRepository.getAllMembers()
            .flowOn(Dispatchers.IO)
    }

    fun getProduct(): Flow<List<POSProduct>> {
        return dataBaseRepository.getAllProduct()
            .flowOn(Dispatchers.IO) // Ensure the flow runs on the IO thread
    }

    fun getSaleTransaction(): Flow<List<SaleRecord>> {
        return dataBaseRepository.getLatestSales()
            .flowOn(Dispatchers.IO) // Ensure the flow runs on the IO thread
    }

     suspend fun getStocks() = flow {
          dataBaseRepository.getStocks().collect{stock->
              val product = stock.map { item->
                 val product= dataBaseRepository.getProductByCode(item.inventoryCode).first()
                 println("product_stock : $product")
                 val updatedProduct=if(product!=null){
                     item.copy(price = product.price, tax = product.tax, barcode = product.barcode?:"", imagePath = product.image)
                 }else
                     item
                 updatedProduct
             }
             emit(product.sortedBy { it.sortOrder })
         }
    }


    fun getScannedProduct(): Flow<List<ProductItem>> =
        dataBaseRepository.fetchAllScannedProduct().map { productDao ->
            productDao.map {
                ProductItem(
                    id = it.productId.toInt(),
                    name = it.name,
                    inventoryCode = it.inventoryCode,
                    barCode = it.barCode,
                    qtyOnHand = it.qty,
                    price = it.price,
                    cartTotal = it.subtotal,
                    originalSubTotal = it.subtotal,
                    taxPercentage = it.taxPercentage,
                    taxValue = it.taxValue,
                    itemDiscount = it.discount
                )
            }
        }

    fun getPromotions() : Flow<List<Promotion>> {
        return dataBaseRepository.getPromotions().flowOn(Dispatchers.IO)
    }

    fun getPromotionDetails() : Flow<List<PromotionDetails>> {
        return dataBaseRepository.getPromotionDetails().flowOn(Dispatchers.IO)
    }

    fun getPaymentType(): Flow<List<PaymentMethod>> =
        dataBaseRepository.getAllPaymentType().map { paymentDao ->
            paymentDao.map {
                it.rowItem
            }
        }

    fun getPaymentMode(): Flow<List<PaymentMethod>> {
        return dataBaseRepository.getAllPaymentType().map { paymentDao ->
            paymentDao.map {
                it.rowItem
            }
        }.flowOn(Dispatchers.IO)
    }


    fun getPrinter() : Flow<Printers?>{
        return dataBaseRepository.getPrinter().flowOn(Dispatchers.IO)
    }

    //Delete
    suspend fun clearAuthentication() {
        dataBaseRepository.deleteAuthentication()
    }

    suspend fun clearExistingLocations() {
        dataBaseRepository.deleteAllLocations()
    }

    suspend fun clearEmployees() {
        dataBaseRepository.deleteAllEmployee()
    }

    suspend fun clearEmployeeRole() {
        dataBaseRepository.deleteAllEmpRole()
    }

    suspend fun clearMember() {
        dataBaseRepository.deleteMembers()
    }

    suspend fun clearMemberGroup() {
        dataBaseRepository.deleteMemberGroup()
    }

    suspend fun clearInventory(){
        dataBaseRepository.deleteProduct()
    }

    suspend fun clearBarcode(){
        dataBaseRepository.deleteBarcode()
    }

    suspend fun clearCategory(){
        dataBaseRepository.deleteCategories()
    }

    private suspend fun clearProductQuantity(){
        dataBaseRepository.deleteProductLocation()
    }

    suspend fun clearStocks(){
        dataBaseRepository.deleteStocks()
    }

    private suspend fun clearPromotion(){
        dataBaseRepository.deletePromotions()
    }

    suspend fun clearPromotionDetails(){
        dataBaseRepository.deletePromotions()
    }

    suspend fun clearScannedProduct() {
        dataBaseRepository.deleteAllScannedProduct()
    }


    suspend fun removeScannedItemById(id:Long){
        withContext(Dispatchers.IO) {
            dataBaseRepository.deleteScannedProductById(id)
        }
    }

    suspend fun removeHoldSaleItemById(id:Long){
        withContext(Dispatchers.IO) {
            dataBaseRepository.deleteHoldSaleById(id)
        }
    }

    suspend fun removeSalesById(id:Long){
        withContext(Dispatchers.IO){
            dataBaseRepository.deleteSaleById(id)
        }
    }

    private suspend fun clearedPOSSales(){
        dataBaseRepository.deletePosSales()
    }

    //local preference

    private suspend fun getBaseUrl(): String {
        return preferences.getBaseURL().first()
    }

    suspend fun getEmployeeCode(): String {
        return preferences.getEmployeeCode().first()
    }

    suspend fun getLocationId(): Long {
        return preferences.getLocationId().first().toLong()
    }

    // Clean up the scope (important to avoid memory leaks)
    fun clear() {
        repositoryScope.cancel()
    }

}