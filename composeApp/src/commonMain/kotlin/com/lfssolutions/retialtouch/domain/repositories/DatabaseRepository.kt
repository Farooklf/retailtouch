package com.lfssolutions.retialtouch.domain.repositories

import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.SqlPreference
import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.inventory.Stock
import com.lfssolutions.retialtouch.domain.model.location.LocationDao
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
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceDao
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceResponse
import com.lfssolutions.retialtouch.domain.model.productBarCode.BarcodeDao
import com.lfssolutions.retialtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationDao
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxDao
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ScannedProductDao
import com.lfssolutions.retialtouch.utils.DoubleExtension.calculatePercentage
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
                        LocationDao(
                            locationId = location.id ?: 0,
                            name = location.name ?: "",
                            code = location.code ?: "",
                            country = location.country ?: "",
                            address1 = location.address1 ?: "",
                            address2 = location.address2 ?: "",
                            isSelected = false
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
                    val dao = ProductTaxDao(
                        // tax = item.taxPercentage ?: "0.0",
                        productTaxId = item.id.toLong(),
                        productCode = item.inventoryCode,
                        name = item.name,
                        rowItem = item.copy(
                            price = if (item.specialPrice == 0.0) item.price else item.specialPrice,
                            qtyOnHand = stockQtyMap[item.id] ?: 1.0,
                            image = if (!item.image.isNullOrEmpty()) "${getBaseUrl()}${item.image}".replace(
                                "\\",
                                "/"
                            ) else ""
                        )
                    )
                    if (lastSyncDateTime == null) {
                        dataBaseRepository.insertProductWithTax(dao)
                    } else {
                        dataBaseRepository.updateProductWithTax(dao)
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

    suspend fun insertNewStock(
        newStock: List<Stock>
    ) {
        try {
            withContext(Dispatchers.IO) {
                clearStocks()
                newStock.map { item ->
                    if (item.id != null && item.id != 0) {
                        var product = getProductById(item.productId ?: 0)
                        product = getProductByCode(item.inventoryCode.orEmpty())

                        item.icon = if (!item.icon.isNullOrEmpty()) {
                            "${preferences.getBaseURL()}${item.icon}".replace("\\", "/")
                        } else {
                            ""
                        }

                        product?.let {
                            item.stockPrice = it.price
                            if (!it.image.isNullOrEmpty() && item.icon.isNullOrEmpty()) {
                                item.icon = it.image
                            }
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
                apiResponse.result.items.forEach { code ->
                    if (code.productId != 0 && !code.code.isNullOrEmpty()) {
                        val mBarcodeDao = BarcodeDao(
                            barcodeId = code.productId,
                            barcode = code
                        )
                        if (lastSyncDateTime == null) {
                            dataBaseRepository.deleteBarcode()
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

    suspend fun insertPosInvoice(
        response: POSInvoiceResponse
    ) {
        try {
            withContext(Dispatchers.IO) {
                response.result?.items?.forEach { item ->
                    val dao = POSInvoiceDao(
                        posInvoiceId = item.id.toLong(),
                        totalCount = response.result.totalCount?.toLong() ?: 0,
                        posItem = item,
                    )
                    dataBaseRepository.insertPosInvoice(dao)
                }
            }

        } catch (ex: Exception) {
            println("EXCEPTION POSINVOICE: ${ex.message}")
        }
    }


    suspend fun insertOrUpdateScannedProduct(item: ProductTaxItem) {
        // Switch to the IO dispatcher to perform the database operation
        withContext(Dispatchers.IO) {
            val subtotal = item.price?.times(item.qtyOnHand) ?: 0.0
            val taxValue = subtotal.calculatePercentage(item.taxPercentage ?: 0.0)
            val dao = ScannedProductDao(
                productId = item.id.toLong(),
                name = item.name ?: "",
                inventoryCode = item.inventoryCode ?: "",
                barCode = item.barCode ?: "",
                qty = item.qtyOnHand,
                price = item.price ?: 0.0,
                subtotal = subtotal,
                discount = item.itemDiscount,
                taxValue = taxValue,
                taxPercentage = item.taxPercentage ?: 0.0
            )
            dataBaseRepository.insertScannedProduct(dao)
        }
    }

    suspend fun insertProductLocation(
        response: ProductLocationResponse
    ) {
        try {
            withContext(Dispatchers.IO) {
                response.result?.items?.forEach { item ->
                    val dao = ProductLocationDao(
                        productLocationId = item.id.toLong(),
                        rowItem = item,
                    )
                    dataBaseRepository.insertProductLocation(dao)
                }
            }

        } catch (ex: Exception) {

        }
    }


    //Update

    suspend fun updateScannedProduct(updatedItem: ProductTaxItem) {
        // Switch to the IO dispatcher to perform the database operation
        withContext(Dispatchers.IO) {
            val dao = ScannedProductDao(
                productId = updatedItem.id.toLong(),
                qty = updatedItem.qtyOnHand,
                discount = updatedItem.itemDiscount,
                subtotal = updatedItem.subtotal ?: 0.0,
                taxValue = updatedItem.taxValue ?: 0.0
            )
            // Call the repository method to update the product
            dataBaseRepository.updateScannedProduct(dao)
        }
    }


    //fetch
    fun getAuthUser(): Flow<AuthenticateDao> {
        return dataBaseRepository.getAllAuthentication()
    }

    fun getEmployee(empCode: String): Flow<EmployeeDao?> {
        return dataBaseRepository.getEmployeeByCode(empCode)
    }

    suspend fun getEmployeeByCode(empCode: String): EmployeeDao? {
        return dataBaseRepository.getEmployeeByCode(empCode.trim()).first()
    }

    suspend fun getProductById(id: Int): ProductTaxDao? {
        return dataBaseRepository.getProductById(id.toLong()).first()
    }

    suspend fun getProductByCode(code: String): ProductTaxDao? {
        return dataBaseRepository.getProductByCode(code).first()
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

    /*fun getCategories() : Flow<CategoryItem?> = flow {
        dataBaseRepository.getAllCategories().collectLatest { categoryDao->
            categoryDao.map { cat->
                emit(cat.categoryItem)
            }
        }
    }*/

    fun getMember(): Flow<List<MemberDao>> {
        println("membersCall")
        return dataBaseRepository.getAllMembers()
            .flowOn(Dispatchers.IO)
    }

    fun getProduct(): Flow<List<ProductTaxDao>> {
        println("ProductDao : call")
        return dataBaseRepository.getAllProductWithTax()
            .flowOn(Dispatchers.IO) // Ensure the flow runs on the IO thread
    }

    suspend fun getAllProducts(): List<ProductTaxItem> {
        val productList = mutableListOf<ProductTaxItem>()
        dataBaseRepository.getAllProductWithTax().collectLatest { products ->
            println("ProductDao : $products")
            val updatedList=products.map {
                it.rowItem
            }
            productList.addAll(updatedList)
        }
        println("productList :$productList")
        return productList
    }

    suspend fun getStocks(): MutableList<Stock> {
        val stockList = mutableListOf<Stock>()
        dataBaseRepository.getStocks().toList()
        dataBaseRepository.getStocks().collectLatest { itemDao ->
            val updatedList = itemDao.map { item ->
                val inventory = getProductById(item.menuProductItem.id ?: 0)
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

    fun getScannedProduct(): Flow<List<ProductTaxItem>> =
        dataBaseRepository.fetchAllScannedProduct().map { productDao ->
            productDao.map {
                ProductTaxItem(
                    id = it.productId.toInt(),
                    name = it.name,
                    inventoryCode = it.inventoryCode,
                    barCode = it.barCode,
                    qtyOnHand = it.qty,
                    price = it.price,
                    subtotal = it.subtotal,
                    originalSubTotal = it.subtotal,
                    taxPercentage = it.taxPercentage,
                    taxValue = it.taxValue,
                    itemDiscount = it.discount
                )
            }
        }

    fun getPaymentType(): Flow<List<PaymentTypeItem>> =
        dataBaseRepository.getAllPaymentType().map { paymentDao ->
            paymentDao.map {
                it.rowItem
            }
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
        dataBaseRepository.deleteProductWithTax()
    }

    suspend fun clearBarcode(){
        dataBaseRepository.deleteBarcode()
    }

    suspend fun clearCategory(){
        dataBaseRepository.deleteCategories()
    }

    suspend fun clearStocks(){
        dataBaseRepository.deleteStocks()
    }

    suspend fun clearScannedProduct() {
        dataBaseRepository.deleteAllScannedProduct()
    }


    suspend fun removeScannedItemById(id:Long){
        withContext(Dispatchers.IO) {
            dataBaseRepository.deleteScannedProductById(id)
        }
    }

    //local preference

    suspend fun getBaseUrl(): String {
        return preferences.getBaseURL().first()
    }

    suspend fun getEmployeeCode(): String {
        return preferences.getEmployeeCode().first()
    }

    // Clean up the scope (important to avoid memory leaks)
    fun clear() {
        repositoryScope.cancel()
    }

}