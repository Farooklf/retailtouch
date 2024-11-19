package com.lfssolutions.retialtouch.domain.repositories

import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.SqlPreference
import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesRights
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSaleRecordDao
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosConfiguredPaymentRecord
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosInvoiceDetailRecord
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosInvoicePendingSaleRecord
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.location.LocationResponse
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupDao
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.menu.CategoryDao
import com.lfssolutions.retialtouch.domain.model.menu.CategoryItem
import com.lfssolutions.retialtouch.domain.model.menu.CategoryResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeDao
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.printer.PrinterDao
import com.lfssolutions.retialtouch.domain.model.printer.PrinterScreenState
import com.lfssolutions.retialtouch.domain.model.sales.GetPosInvoiceResult
import com.lfssolutions.retialtouch.domain.model.productBarCode.Barcode
import com.lfssolutions.retialtouch.domain.model.productBarCode.BarcodeDao
import com.lfssolutions.retialtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationDao
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.domain.model.products.ProductDao
import com.lfssolutions.retialtouch.domain.model.products.ProductItem
import com.lfssolutions.retialtouch.domain.model.products.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.products.SaleOnHoldRecordDao
import com.lfssolutions.retialtouch.domain.model.products.ScannedProductDao
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionResult
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDao
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetailsDao
import com.lfssolutions.retialtouch.domain.model.sales.SaleRecord
import com.lfssolutions.retialtouch.utils.AppConstants.SYNC_SALES_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.DateTime.getCurrentDateAndTimeInEpochMilliSeconds
import com.lfssolutions.retialtouch.utils.DateTime.parseDateFromApiString
import com.lfssolutions.retialtouch.utils.DateTime.parseDateFromApiStringUTC
import com.lfssolutions.retialtouch.utils.DoubleExtension.calculatePercentage
import com.lfssolutions.retialtouch.utils.PaperSize
import com.lfssolutions.retialtouch.utils.PrinterType
import com.lfssolutions.retialtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
import comlfssolutionsretialtouch.Printers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class DataBaseRepository: KoinComponent {
    private val dataBaseRepository: SqlPreference by inject()
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
            employeesResponse.result.items.forEach { employee ->
                val mEmployeeDao =
                    EmployeeDao(
                        employeeId = employee.id,
                        employeeName = employee.name,
                        employeeCode = employee.employeeCode,
                        employeeRoleName = employee.employeeRoleName,
                        employeePassword = employee.password,
                        employeeCategoryName = employee.employeeCategoryName ?: "",
                        employeeDepartmentName = employee.employeeDepartmentName ?: "",
                        isAdmin = employee.isAdmin,
                        isDeleted = employee.isDeleted
                    )
                dataBaseRepository.insertEmployee(mEmployeeDao)
            }
        }
    }

    suspend fun insertEmpRole(
        employeesResponse: EmployeesResponse
    ) {
        withContext(Dispatchers.IO) {
            employeesResponse.result.items.forEach { employee ->
                val mEmployeeDao =
                    EmployeeDao(
                        employeeId = employee.id,
                        employeeName = employee.name,
                        isAdmin = employee.isAdmin,
                        isDeleted = employee.isDeleted
                    )
                dataBaseRepository.insertEmpRole(mEmployeeDao)
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
                    val mEmployeeDao = EmployeeDao(
                            employeeId = employee.id?:0,
                            employeeName = employee.name,
                            isAdmin = employee.isAdmin,
                            isDeleted = employee.isDeleted,
                            grantedPermissionNames = it.grantedPermissionNames,
                            restrictedPermissionNames = it.restrictedPermissionNames,
                            permissions = it.permissions
                        )
                    dataBaseRepository.insertEmpRights(mEmployeeDao)
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
                    val dao = MemberDao(
                        memberId = item.id.toLong(),
                        rowItem = item,
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
                        product = Product(
                            id = item.id.toLong(),
                            name = item.name,
                            productCode = item.inventoryCode,
                            tax = item.taxPercentage?:0.0,
                            barcode = item.barCode,
                            price = if (item.specialPrice == 0.0) item.price else item.specialPrice,
                            qtyOnHand = stockQtyMap[item.id] ?: 0.0,
                            image = if (!item.image.isNullOrEmpty()) "${getBaseUrl()}${item.image}".replace(
                                "\\",
                                "/"
                            ) else ""
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
            response.result.items.forEach { item ->
                val dao = CategoryDao(
                    categoryId = item.id.toLong(),
                    categoryItem = item.copy(categoryRowCount = item.categoryRowCount ?: 1)
                )
                dataBaseRepository.insertMenuCategories(dao)
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
                clearStocks()
                newStock.map { item ->
                    if (item.id != null && item.id != 0L) {
                         //var product = getProductById(item.productId ?: 0)
                         getProductByCode(item.inventoryCode.orEmpty()).collectLatest { product->
                             product?.let {
                                 item.stockPrice = it.price
                                 if (it.image?.isNotEmpty() == true && item.icon.isNullOrEmpty()) {
                                     item.icon = it.image
                                 }
                             }
                        }

                        item.icon = if (!item.icon.isNullOrEmpty()) {
                            "${preferences.getBaseURL()}${item.icon}".replace("\\", "/")
                        } else {
                            ""
                        }
                        val dao = MenuDao(
                            productId = item.id.toLong(),
                            menuProductItem = item
                        )

                        dataBaseRepository.insertStocks(dao)
                    }
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
                                name = item.name?:"",
                                inventoryCode = item.inventoryCode?:"",
                                promotionType = item.promotionType?:0,
                                promotionTypeName = item.promotionTypeName,
                                qty = item.qty?:0.0,
                                amount = item.amount?:0.0,
                                startDate = item.startDate.parseDateFromApiString(),
                                endDate = item.endDate.parseDateFromApiString(),
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
                    id = promotionDetails.id.toLong(),
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
                response.result?.items?.forEach { item ->
                    val dao = SaleRecord(
                        id = item.id,
                        count  = response.result.totalCount ?: 0,
                        receiptNumber = item.invoiceNo?:"",
                        amount = item.invoiceNetTotal?:0.0,
                        date  = item.invoiceDate.parseDateFromApiString(),
                        deliveryDate  = item.deliveryDateTime.parseDateFromApiString(),
                        creationDate  = item.creationTime.parseDateFromApiStringUTC(),
                        remarks = item.remarks?:"",
                        delivered = item.isDelivered?:false,
                        delivery = item.deliveryDateTime!=null,
                        rental = item.isRental?:false,
                        rentalCollected= item.isRentalCollected?:false,
                        selfCollection=item.selfCollection?:false,
                        type = item.type?:0,
                        status= if (item.isCancelled == true) 666 else item.status,
                        memberId = item.memberId?:0
                    )
                    dataBaseRepository.insertLatestSales(dao)
                }
            }

        } catch (ex: Exception) {
            println("$SYNC_SALES_ERROR_TITLE :${ex.message}")
        }
    }

    suspend fun insertNextPosSale(
        response: NextPOSSaleInvoiceNoResponse
    ) {
        try {
            withContext(Dispatchers.IO) {
                response.result?.let {
                    val dao = NextPOSSaleDao(
                        posItem = it
                    )
                    dataBaseRepository.insertNextPosSale(dao)
                }
            }
        } catch (ex: Exception) {
            println("EXCEPTION NEXTPOSSALE: ${ex.message}")
        }
    }


    suspend fun insertOrUpdateScannedProduct(item: Product) {
        // Switch to the IO dispatcher to perform the database operation
        withContext(Dispatchers.IO) {
            val subtotal = item.price?.times(item.qtyOnHand) ?: 0.0
            val taxValue = subtotal.calculatePercentage(item.tax ?: 0.0)
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
                taxPercentage = item.tax ?: 0.0
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

    suspend fun addNewPendingSales(data: PendingSaleRecordDao){
        withContext(Dispatchers.IO) {
            val posInvoice=data.posInvoice
             val id =  if(data.isDbUpdate) posInvoice.id else getCurrentDateAndTimeInEpochMilliSeconds()
             println("posInvoicesId : $id")
             val pendingSaleRecord=PosInvoicePendingSaleRecord(
                 id=id,
                 locationId = posInvoice.locationId?:0,
                 tenantId = posInvoice.tenantId?:0,
                 terminalName = posInvoice.terminalName?:"",
                 locationCode = posInvoice.locationCode?:"",
                 isRetailWebRequest = posInvoice.isRetailWebRequest?:false,
                 invoiceTotal = posInvoice.invoiceTotal?:0.0,
                 invoiceItemDiscount = posInvoice.invoiceItemDiscount?:0.0,
                 invoiceTotalValue = posInvoice.invoiceTotalValue,
                 invoiceNetDiscountPerc = posInvoice.invoiceNetDiscountPerc?:0.0,
                 invoiceNetDiscount = posInvoice.invoiceNetDiscount,
                 invoiceTotalAmount = posInvoice.invoiceTotalAmount,
                 invoiceSubTotal = posInvoice.invoiceSubTotal,
                 invoiceNetTotal = posInvoice.invoiceNetTotal?:0.0,
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
                 memberId = posInvoice.memberId?:0,
                 posPaymentConfigRecord = posInvoice.posPayments?: emptyList(),
                 posInvoiceDetailRecord = posInvoice.posInvoiceDetails?: emptyList(),
                 isSynced = data.isSynced
            )
            if(!data.isDbUpdate){
                dataBaseRepository.insertPosPendingSaleRecord(pendingSaleRecord)
            }else{
                dataBaseRepository.updatePosSales(pendingSaleRecord)
            }
            posInvoice.posPayments?.map { payment ->
                val paymentRecord  = PosConfiguredPaymentRecord(
                    posPaymentRecordId = id,
                    posInvoiceId = payment.posInvoiceId,
                    paymentTypeId = payment.paymentTypeId,
                    amount = payment.amount
                )
                dataBaseRepository.insertPosConfiguredPaymentRecord(paymentRecord)
            }

            posInvoice.posInvoiceDetails?.forEach { invoice->
              val posInvoiceDetails= PosInvoiceDetailRecord(
                  posPaymentRecordId = id, 
                  productId=invoice.productId,
                 inventoryCode=invoice.inventoryCode,
                 inventoryName=invoice.inventoryName,
                 qty=invoice.qty,
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
            }

        }
        }

    suspend  fun insertPrinter(printer: PrinterScreenState) {
        withContext(Dispatchers.IO){
            dataBaseRepository.insertPrinter(
                PrinterDao(
                printerStationName = printer.printerStationName,
                printerName = printer.printerName?:"",
                numbersOfCopies = printer.numbersOfCopies.toLong(),
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
            ))
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
    fun getAllPendingSaleRecordsCount() : Flow<Long>{
        return dataBaseRepository.getAllPendingSalesCount().flowOn(Dispatchers.IO)
    }

    fun getPosPendingSales() :Flow<List<PosInvoicePendingSaleRecord>>{
        return dataBaseRepository.getPendingSaleRecords().flowOn(Dispatchers.IO)
    }

    fun getAuthUser(): Flow<AuthenticateDao> {
        return dataBaseRepository.getAllAuthentication()
    }

    suspend fun getAuthUser(id:Long): AuthenticateDao {
        return dataBaseRepository.selectUserByUserId(id).first()
    }

    fun getSelectedLocation(): Flow<Location?> {
        return dataBaseRepository.getSelectedLocation().flowOn(Dispatchers.IO)
    }

    fun getEmployee(empCode: String): Flow<EmployeeDao?> {
        return dataBaseRepository.getEmployeeByCode(empCode)
    }

    suspend fun getEmployeeByCode(empCode: String): EmployeeDao? {
        return dataBaseRepository.getEmployeeByCode(empCode.trim()).first()
    }

    fun getEmpRights(): Flow<List<EmployeeDao>> {
        return dataBaseRepository.getAllEmpRights()
    }

    fun getBarcode(code: String) : Flow<Barcode?> {
      return dataBaseRepository.getItemByBarcode(code)
    }

    fun getProductById(id: Long) : Flow<Product?> {
         return dataBaseRepository.getProductById(id)
    }

    fun getProductByCode(code: String): Flow<Product?> {
        return dataBaseRepository.getProductByCode(code)
    }

    fun getCategories(): Flow<CategoryItem> =
        dataBaseRepository.getAllCategories()
            .flatMapConcat { categoryDao ->
                flow {
                    categoryDao.forEach { cat ->
                        emit(cat.categoryItem)
                    }
                }
            }

    fun getMember(): Flow<List<MemberDao>> {
        return dataBaseRepository.getAllMembers()
            .flowOn(Dispatchers.IO)
    }

    fun getProduct(): Flow<List<Product>> {
        return dataBaseRepository.getAllProduct()
            .flowOn(Dispatchers.IO) // Ensure the flow runs on the IO thread
    }

    suspend fun getStocks(): MutableList<Stock> {
        val stockList = mutableListOf<Stock>()
        dataBaseRepository.getStocks().toList()
        dataBaseRepository.getStocks().collectLatest { itemDao ->
            val updatedList = itemDao.map { item ->
                val inventory = getProductById(item.menuProductItem.id ?: 0).first()
                if (inventory != null) {
                    item.menuProductItem.copy(tax = inventory.tax)
                } else {
                    item.menuProductItem
                }
            }
            stockList.addAll(updatedList.sortedBy { it.sortOrder })
        }
        return stockList
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

    fun getAllPrinterList() : Flow<List<Printers>>{
        return dataBaseRepository.getAllPrinterList().flowOn(Dispatchers.IO)
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