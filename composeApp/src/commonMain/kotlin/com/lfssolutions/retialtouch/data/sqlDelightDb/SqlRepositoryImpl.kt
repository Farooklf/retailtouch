package com.lfssolutions.retialtouch.data.sqlDelightDb


import com.lfssolutions.retialtouch.domain.SqlRepository
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.employee.POSEmployee
import com.lfssolutions.retialtouch.domain.model.employee.POSEmployeeRight
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosSalePayment
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosSaleDetails
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupDao
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.menu.CategoryDao
import com.lfssolutions.retialtouch.domain.model.menu.MenuDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleDao
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeDao
import com.lfssolutions.retialtouch.domain.model.printer.PrinterDao
import com.lfssolutions.retialtouch.domain.model.productBarCode.Barcode
import com.lfssolutions.retialtouch.domain.model.productBarCode.BarcodeDao
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationDao
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.products.ProductDao
import com.lfssolutions.retialtouch.domain.model.products.SaleOnHoldRecordDao
import com.lfssolutions.retialtouch.domain.model.products.ScannedProductDao
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDao
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetailsDao
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.lfssolutions.retialtouch.domain.model.login.RTLoginUser
import com.lfssolutions.retialtouch.domain.model.menu.StockCategory
import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllDao
import com.lfssolutions.retialtouch.retailTouchDB
import com.lfssolutions.retialtouch.utils.serializers.db.toHoldSaleRecord
import com.lfssolutions.retialtouch.utils.serializers.db.toJson
import com.lfssolutions.retialtouch.utils.serializers.db.toLogin
import com.lfssolutions.retialtouch.utils.serializers.db.toMemberGroupItem
import com.lfssolutions.retialtouch.utils.serializers.db.toMemberItem
import com.lfssolutions.retialtouch.utils.serializers.db.toMenuProductItem
import com.lfssolutions.retialtouch.utils.serializers.db.toNextPosSaleItem
import com.lfssolutions.retialtouch.utils.serializers.db.toPOSEmployeeRight
import com.lfssolutions.retialtouch.utils.serializers.db.toPaymentTypeItem
import com.lfssolutions.retialtouch.utils.serializers.db.toPosInvoiceDetailRecord
import com.lfssolutions.retialtouch.utils.serializers.db.toPosInvoicePendingSaleRecord
import com.lfssolutions.retialtouch.utils.serializers.db.toPosPaymentConfigRecord
import com.lfssolutions.retialtouch.utils.serializers.db.toProductLocationItem
import com.lfssolutions.retialtouch.utils.serializers.db.toPromotion
import com.lfssolutions.retialtouch.utils.serializers.db.toPromotionDetails
import com.lfssolutions.retialtouch.utils.serializers.db.toSaleInvoiceItem
import com.lfssolutions.retialtouch.utils.serializers.db.toSaleRecord
import com.lfssolutions.retialtouch.utils.serializers.db.toStockCategory
import com.lfssolutions.retialtouch.utils.serializers.db.toSyncItem
import comlfssolutionsretialtouch.Printers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow



class SqlRepositoryImpl(private val retailTouch: retailTouchDB) : SqlRepository {


    override suspend fun insertAuthentication(authenticateDao: AuthenticateDao) {

        retailTouch.userTenanatQueries.insert(
            userId = authenticateDao.userId.toLong(),
            tenantId = authenticateDao.tenantId.toLong(),
            url = authenticateDao.serverURL,
            tenantname = authenticateDao.tenantName,
            username = authenticateDao.userName,
            password = authenticateDao.password,
            isLoggedIn = authenticateDao.isLoggedIn,
            isSelected = authenticateDao.isSelected,
            loginDao = authenticateDao.loginDao.toJson()
        )
    }

    override  fun selectUserByUserId(userId: Long): Flow<AuthenticateDao> = flow{
        retailTouch.userTenanatQueries.selectUserByUserId(userId).executeAsOneOrNull().let { body->
           // println("dbAuth:$body")
            body?.let {
                emit(
                    AuthenticateDao(
                        userId = it.userId.toInt(),
                        tenantId = it.tenantId.toInt(),
                        serverURL = it.url,
                        tenantName = it.tenantname,
                        userName = it.username,
                        password = it.password,
                        isLoggedIn = it.isLoggedIn ?: false,
                        isSelected = it.isSelected ?: false,
                        loginDao = it.loginDao.toLogin()
                    )
                )
            }
        }
    }

    override fun getAllAuthentication(): Flow<AuthenticateDao> = flow {
        retailTouch.userTenanatQueries.getAll().executeAsOneOrNull().let { body ->
            body?.let {
                emit(
                    AuthenticateDao(
                        userId = it.userId.toInt(),
                        tenantId = it.tenantId.toInt(),
                        serverURL = it.url,
                        tenantName = it.tenantname,
                        userName = it.username,
                        password = it.password,
                        isLoggedIn = it.isLoggedIn ?: false,
                        isSelected = it.isSelected ?: false,
                        loginDao = it.loginDao.toLogin()
                    )
                )
            }
        }
    }

     override fun getAuthUser(): Flow<RTLoginUser> = flow{
         retailTouch.userTenanatQueries.getAll().executeAsOneOrNull().let { body ->
             body?.let {
                 val login=it.loginDao.toLogin()
                 emit(
                        RTLoginUser(
                         userId = login.userId,
                         tenantId = login.tenantId?:0,
                         serverURL = it.url,
                         tenantName = it.tenantname,
                         userName = it.username,
                         password = it.password,
                         currency = login.currencySymbol?:""
                     )
                 )
             }
         }
     }

     override suspend fun deleteAuthentication() {
        retailTouch.userTenanatQueries.deleteAuth()
    }

    override suspend fun insertLocation(locationDao: Location) {
        retailTouch.userLocationQueries.insertLocation(
            locationId  = locationDao.locationId,
            name = locationDao.name,
            code = locationDao.code,
            address1 = locationDao.address1,
            address2 = locationDao.address2,
            country = locationDao.country,
            isSelected = locationDao.isSelected,
            menuId = locationDao.menuId)
    }

     override fun getSelectedLocation(): Flow<Location?> = flow{
         retailTouch.userLocationQueries.getSelectedLocation().executeAsOneOrNull().let { body ->
             if(body!=null){
                 emit(
                     Location(
                         locationId = body.locationId,
                         name = body.name,
                         code = body.code,
                         address1 = body.address1,
                         address2= body.address2,
                         country = body.country,
                         menuId = body.menuId,
                         isSelected = body.isSelected ?: false
                     )
                 )
             }else{
                 emit(body)
             }
         }
     }

     override suspend fun deleteAllLocations() {
        retailTouch.userLocationQueries.deleteAllLocation()
    }

    override suspend fun insertEmployee(mPOSEmployee: POSEmployee) {
        retailTouch.employeesQueries.insertEmployees(
            employeeId = mPOSEmployee.employeeId.toLong(),
            employeeCode = mPOSEmployee.employeeCode.uppercase(),
            employeeName = mPOSEmployee.employeeName,
            employeeRoleName = mPOSEmployee.employeeRoleName,
            employeePassword = mPOSEmployee.employeePassword,
            employeeCategoryName = mPOSEmployee.employeeCategoryName,
            employeeDepartmentName = mPOSEmployee.employeeDepartmentName,
            creationTime = mPOSEmployee.creationTime,
            isAdmin = mPOSEmployee.isAdmin,
            isDeleted = mPOSEmployee.isDeleted,
            isPOSEmployee = mPOSEmployee.isPosEmployee
        )
    }

     override suspend fun updatePOSEmployee(mPOSEmployee: POSEmployee) {
         retailTouch.employeesQueries.updatePOSEmployee(
             id = mPOSEmployee.employeeId.toLong(),
             isPOSEmployee = mPOSEmployee.isPosEmployee
         )
     }

     override fun getEmployeeByCode(employeeCode: String): Flow<POSEmployee?> = flow {
        retailTouch.employeesQueries.selectEmployeeByCode(employeeCode.uppercase()).executeAsOneOrNull().let { body->
            //println("employee : $body")
            if(body!=null){
                emit(
                    POSEmployee(
                        employeeId = body.employeeId.toInt(),
                        employeeCode = body.employeeCode,
                        employeeName = body.employeeName,
                        employeePassword = body.employeePassword,
                        employeeCategoryName = body.employeeCategoryName,
                        employeeDepartmentName = body.employeeDepartmentName,
                        employeeRoleName = body.employeeRoleName,
                        isAdmin = body.isAdmin ?: false,
                        isDeleted = body.isDeleted ?: false,
                        isPosEmployee = body.isPOSEmployee ?: false,
                    )
                )
            }else{
                emit(body)
            }
        }

    }

     override fun getPOSEmployees(): Flow<List<POSEmployee>> = flow{
         retailTouch.employeesQueries.getAllEmployees().executeAsList().let { list ->
             emit(
                 list.map { body ->
                     POSEmployee(
                         employeeId = body.employeeId.toInt(),
                         employeeName = body.employeeName,
                         employeeCode = body.employeeCode,
                         employeePassword = body.employeePassword,
                         employeeRoleName = body.employeeRoleName,
                         creationTime = body.creationTime,
                         isDeleted = body.isDeleted ?: false,
                         isAdmin = body.isAdmin ?: false,
                         isPosEmployee = body.isPOSEmployee ?: false,
                     )
                 })
         }
     }

     override suspend fun deleteAllEmployee() {
        retailTouch.employeesQueries.deleteEmployees()
    }

    override suspend fun insertEmpRole(mPOSEmployee: POSEmployee) {
        retailTouch.employeeRoleQueries.insertEmpRole(
            empRoleId = mPOSEmployee.employeeId.toLong(),
            empRoleName = mPOSEmployee.employeeName,
            isAdmin = mPOSEmployee.isAdmin,
            isDeleted = mPOSEmployee.isDeleted
        )
    }

    override fun getAllEmpRole(): Flow<List<POSEmployee>> = flow {
        retailTouch.employeeRoleQueries.getAllEmpRole().executeAsList().let { list ->
            emit(
                list.map { body ->
                    POSEmployee(
                        employeeId = body.empRoleId.toInt(),
                        employeeName = body.empRoleName,
                        isDeleted = body.isDeleted ?: false,
                        isAdmin = body.isAdmin ?: false
                    )
                })
        }
    }

    override suspend fun deleteAllEmpRole() {
        retailTouch.employeeRoleQueries.deleteEmpRole()
    }

    override suspend fun insertEmpRights(mPOSEmployeeRight: POSEmployeeRight) {
        retailTouch.employeeRightsQueries.insert(
            id = mPOSEmployeeRight.id.toLong(),
            employee = mPOSEmployeeRight.toJson())
    }

    override fun getAllEmpRights(): Flow<List<POSEmployeeRight>> = flow{
        retailTouch.employeeRightsQueries.getAll().executeAsList().let { list ->
            emit(
                list.map { body ->
                    body.employee.toPOSEmployeeRight()
                    /*POSEmployeeRight(
                        id = body.id.toInt(),
                        name = employee.name,
                        isAdmin = employee.isAdmin,
                        permissions = employee.permissions,
                        grantedPermissionNames = employee.grantedPermissionNames,
                        restrictedPermissionNames = employee.restrictedPermissionNames
                    )*/
                }

            )
        }
    }

    override suspend fun deleteEmpRights() {
       retailTouch.employeeRightsQueries.delete()
    }

    override suspend fun insertStockCategories(menuCategoriesDao: CategoryDao) {
        retailTouch.menuCategoryQueries.insertMenuCategory(
            categoryId = menuCategoriesDao.categoryId,
            categoryItem = menuCategoriesDao.stockCategory.toJson()
        )
    }

    override fun selectCategoryById(id: Long): Flow<CategoryDao?> = flow{
        retailTouch.menuCategoryQueries.getCategoryById(id).executeAsOneOrNull().let { body->
            //println("menu category : $body")
            if(body!=null){
                emit(
                    CategoryDao(
                        categoryId = body.categoryId,
                        stockCategory = body.categoryItem.toStockCategory()
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getAllCategories(): Flow<List<StockCategory>> = flow{
        retailTouch.menuCategoryQueries.getAllMenuCategory().executeAsList().let { list ->
            emit(
                list.map { body ->
                     body.categoryItem.toStockCategory()
                })
        }
    }

    override suspend fun deleteCategories() {
        retailTouch.menuCategoryQueries.deleteMenuCategory()
    }

    override fun getMenuCategoriesCount(): Flow<Int> = flow{
        retailTouch.menuCategoryQueries.countMenuCategory().executeAsOne().let {count->
            emit(count.toInt())
        }
    }

    override suspend fun insertStocks(menuProductsDao: MenuDao) {
        retailTouch.menuProductQueries.insertMenuProduct(
            productId = menuProductsDao.productId,
            productItem = menuProductsDao.menuProductItem.toJson()
        )
    }

    override fun selectProductsById(id: Long): Flow<MenuDao?> = flow{
        retailTouch.menuProductQueries.getProductById(id).executeAsOneOrNull().let { body->
            println("menu products : $body")
            if(body!=null){
                emit(
                    MenuDao(
                        productId = body.productId,
                        menuProductItem = body.productItem.toMenuProductItem()
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getStocks(): Flow<List<Stock>> = flow{
        retailTouch.menuProductQueries.getAllMenuProduct().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        body.productItem.toMenuProductItem()
                    })
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteStocks() {
        retailTouch.menuProductQueries.deleteMenuProduct()
    }

    override  fun getMenuItemsCount():  Flow<Int> = flow {
        retailTouch.menuProductQueries.countMenuProducts().executeAsOne().let {count->
            emit(count.toInt())
        }
    }

   /* override suspend fun insertNextPosSale(nextPOSSaleDao: NextPOSSaleDao) {
        retailTouch.nextPOSSaleQueries.insertNextPosSale(
            posItem = nextPOSSaleDao.posItem.toJson()
        )
    }

    override fun getNextPosSaleById(id: Long): Flow<NextPOSSaleDao?> = flow{
        retailTouch.nextPOSSaleQueries.getPosSaleById(id).executeAsOneOrNull().let { body->
            println("next pos sale : $body")
            if(body!=null){
                emit(
                    NextPOSSaleDao(
                        posId = body.posId,
                        posItem = body.posItem.toNextPosSaleItem()
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getAllNextPosSale(): Flow<List<NextPOSSaleDao>> = flow{
        retailTouch.nextPOSSaleQueries.getAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        NextPOSSaleDao(
                            posId = body.posId,
                            posItem = body.posItem.toNextPosSaleItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteNextPosSale() {
        retailTouch.nextPOSSaleQueries.deleteNextPosSale()
    }

    override fun getNextPosSaleCount(): Flow<Int> = flow{
        retailTouch.nextPOSSaleQueries.getCount().executeAsOne().let {count->
            emit(count.toInt())
        }
    }*/

    override suspend fun insertLatestSales(saleRecord: SaleRecord) {
        saleRecord.items?.let {
            retailTouch.invoiceSalesQueries.insert(
                posInvoiceId = saleRecord.id,
                totalCount = saleRecord.count?:0,
                salesRecord = saleRecord.toJson(),
                saleDetals = it.toJson()
            )
        }
    }

    override fun getLatestSalesById(id: Long): Flow<SaleRecord?> = flow{
        retailTouch.invoiceSalesQueries.getRowById(id).executeAsOneOrNull().let { body->
            println("latest sale : $body")
            if(body!=null){
                emit(
                    SaleRecord(
                        id = body.posInvoiceId,
                        count  = body.totalCount
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getLatestSales(): Flow<List<SaleRecord>> = flow{
        retailTouch.invoiceSalesQueries.getAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        val sale= body.salesRecord.toSaleRecord()
                        SaleRecord(
                            id = sale.id,
                            count  = sale.count,
                            receiptNumber = sale.receiptNumber,
                            amount = sale.amount,
                            date = sale.date,
                            creationDate = sale.creationDate,
                            remarks = sale.remarks,
                            memberId = sale.memberId,
                            memberName = sale.memberName,
                            deliveryDate = sale.deliveryDate,
                            delivery = sale.delivery,
                            delivered = sale.delivered,
                            rental = sale.rental,
                            rentalCollected  = sale.rentalCollected,
                            type = sale.type,
                            status = sale.status,
                            selfCollection = sale.selfCollection,
                            items = body.saleDetals.toSaleInvoiceItem()
                            )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deletePosSales() {
        retailTouch.invoiceSalesQueries.delete()
    }

    override fun getSalesCount(): Flow<Int> = flow{
        retailTouch.invoiceSalesQueries.getCount().executeAsOne().let {count->
            emit(count.toInt())
        }
    }

    override suspend fun insertProduct(productDao: ProductDao) {
        retailTouch.productsQueries.insertProduct(
            productId = productDao.productId,
            name = productDao.product.name?:"",
            inventoryCode = productDao.product.productCode?:"",
            barcode = productDao.product.barcode?:"",
            image = productDao.product.image?:"",
            quantity = productDao.product.qtyOnHand,
            price = productDao.product.price?:0.0,
            itemDiscount = productDao.product.itemDiscount,
            tax = productDao.product.tax?:0.0,
            isScanned = productDao.isScanned,
            rowItem = productDao.product.toJson()
        )
    }


    override suspend fun updateProduct(productDao: ProductDao) {
       retailTouch.productsQueries.updateProduct(
           productId = productDao.productId,
           name = productDao.product.name?:"",
           inventoryCode = productDao.product.productCode?:"",
           barcode = productDao.product.barcode?:"",
           image = productDao.product.image?:"",
           qty = productDao.product.qtyOnHand,
           price = productDao.product.price?:0.0,
           discount = productDao.product.itemDiscount,
           tax = productDao.product.tax?:0.0,
           isScanned = productDao.isScanned,
           rowItem = productDao.product.toJson()
       )
    }

     override suspend fun updateProductQuantity(productCode: String,quantity:Double) {
         retailTouch.productsQueries.updateProductQuantity(
             inventoryCode =productCode,
             quantity = quantity
         )
     }

     override fun getAllProduct(): Flow<List<Product>> = flow{
        retailTouch.productsQueries.getAllProduct().executeAsList().let { list ->
            emit(
                list.map { product ->
                    Product(
                        id = product.productId,
                        productCode = product.inventoryCode,
                        name = product.name,
                        barcode = product.barcode,
                        image = product.image,
                        tax = product.tax,
                        price = product.price,
                        qtyOnHand = product.quantity,
                        itemDiscount = product.itemDiscount
                    )
                }

            )
        }
    }

     override fun getProducts(): Flow<Product> = flow {
         retailTouch.productsQueries.getAllProduct().executeAsList().let { list ->
             list.map { product ->
                 emit(Product(
                     id = product.productId,
                     productCode = product.inventoryCode,
                     name = product.name,
                     barcode = product.barcode,
                     image = product.image,
                     tax = product.tax,
                     price = product.price,
                     qtyOnHand = product.quantity,
                     itemDiscount = product.itemDiscount
                 ))
             }}
     }

     override fun getProductById(id: Long) : Flow<Product?> = flow{
        retailTouch.productsQueries.getProductById(id).executeAsOneOrNull().let { product->
            //println("product_db_data : $product")
            if(product!=null){
                //val product=body.rowItem.toProduct()
                emit(
                Product(
                    id = product.productId,
                    productCode = product.inventoryCode,
                    name = product.name,
                    barcode = product.barcode,
                    image = product.image,
                    tax = product.tax,
                    price = product.price,
                    qtyOnHand = product.quantity,
                    itemDiscount = product.itemDiscount
                )
                )
            }else{
                emit(product)
            }
        }
    }

    override fun getProductByCode(code: String): Flow<Product?> = flow{
        retailTouch.productsQueries.getProductByInventory(code).executeAsOneOrNull().let { product->
            //println("product_db_data : $product")
            if(product!=null){
                emit(
                    Product(
                        id = product.productId,
                        productCode = product.inventoryCode,
                        name = product.name,
                        barcode = product.barcode,
                        image = product.image,
                        tax = product.tax,
                        price = product.price,
                        qtyOnHand = product.quantity,
                        itemDiscount = product.itemDiscount
                    )
                )
            }else{
                emit(product)
            }
        }
    }

     override fun getProductQty(code: String): Flow<Double> = flow{
         retailTouch.productsQueries.getProductQty(code).executeAsOneOrNull()
     }

     override fun getProductCount(): Flow<Int> = flow{
         retailTouch.productsQueries.getCount().executeAsOne().let {count->
             emit(count.toInt())
         }
     }

     override suspend fun deleteProduct() {
        retailTouch.productsQueries.deleteProduct()
    }

    override suspend fun insertScannedProduct(productTaxDao: ScannedProductDao) {
        retailTouch.scannedProductQueries.insertScannedProduct(
            productTaxId = productTaxDao.productId,
            productName = productTaxDao.name,
            inventoryCode = productTaxDao.inventoryCode,
            barCode = productTaxDao.barCode,
            qtyOnHand = productTaxDao.qty,
            price = productTaxDao.price,
            subTotal = productTaxDao.subtotal,
            discount = productTaxDao.discount,
            taxValue = productTaxDao.taxValue,
            taxPercentage = productTaxDao.taxPercentage
        )
    }

    override suspend fun updateScannedProduct(productTaxDao: ScannedProductDao) {
        retailTouch.scannedProductQueries.updateScannedProduct(
            qty = productTaxDao.qty,
            subTotal = productTaxDao.subtotal,
            discount = productTaxDao.discount,
            productTaxId = productTaxDao.productId
        )
    }

    override fun fetchAllScannedProduct(): Flow<List<ScannedProductDao>> = flow{
        retailTouch.scannedProductQueries.fetchAllScannedProduct().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        ScannedProductDao(
                            productId = body.productTaxId,
                            name = body.productName,
                            inventoryCode = body.inventoryCode,
                            barCode = body.barCode,
                            qty = body.qtyOnHand,
                            price = body.price,
                            subtotal = body.subTotal,
                            taxValue = body.taxValue,
                            discount = body.discount,
                            taxPercentage = body.taxPercentage
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteScannedProductById(productId: Long) {
        retailTouch.scannedProductQueries.deleteProductById(productId)
    }

    override suspend fun deleteAllScannedProduct() {
       retailTouch.scannedProductQueries.deleteAllProduct()
    }

     override suspend fun insertHoldSaleRecord(crSaleOnHold: SaleOnHoldRecordDao) {
         retailTouch.holdSaleRecordQueries.insert(
             holdSaleId =  crSaleOnHold.id,
             holdSaleItem = crSaleOnHold.item.toJson()
         )
     }

     override suspend fun updateHoldSaleRecord(crSaleOnHold: SaleOnHoldRecordDao) {
         retailTouch.holdSaleRecordQueries.update(
             holdSaleId =  crSaleOnHold.id,
             rowItem= crSaleOnHold.item.toJson()
         )
     }

     override fun getAllHoldSaleRecord(): Flow<List<CRSaleOnHold>> = flow{
         retailTouch.holdSaleRecordQueries.getAll().executeAsList().let { list ->
             if(list.isNotEmpty()) {
                 emit(
                     list.map { body ->
                         val item=body.holdSaleItem.toHoldSaleRecord()
                         CRSaleOnHold(
                               ts = item.ts,
                               collectionId = item.collectionId,
                               grandTotal = item.grandTotal,
                               member = item.member,
                               items = item.items,
                         )
                     }

                 )
             }else{
                 emit(emptyList())
             }
         }
     }

     override fun getHoldSaleById(id: Long): Flow<CRSaleOnHold?> = flow{
         retailTouch.holdSaleRecordQueries.getSaleRecordById(id).executeAsOneOrNull().let { body->
             //println("product_db_data : $body")
             if(body!=null){
                 val item=body.holdSaleItem.toHoldSaleRecord()
                 CRSaleOnHold(
                     ts = item.ts,
                     collectionId = item.collectionId,
                     grandTotal = item.grandTotal,
                     member = item.member,
                     items = item.items,
                 )
             }else{
                 emit(body)
             }
         }
     }

     override suspend fun deleteHoldSaleById(id: Long) {
         retailTouch.holdSaleRecordQueries.deleteSaleById(id)
     }

     override suspend fun deleteHoldSale() {
         retailTouch.holdSaleRecordQueries.delete()
     }

     override suspend fun insertProductLocation(productLocationDao: ProductLocationDao) {
        retailTouch.productLocationQueries.insertProductLocation(
            productLocationId = productLocationDao.productLocationId,
            rowItem = productLocationDao.rowItem.toJson()
        )
    }

     override suspend fun updateProductLocation(productLocationDao: ProductLocationDao) {
         retailTouch.productLocationQueries.updateProductLocation(
             productTaxId = productLocationDao.productLocationId,
             rowItem = productLocationDao.rowItem.toJson()
         )

     }

     override fun getAllProductLocation(): Flow<List<ProductLocationDao>> = flow{
        retailTouch.productLocationQueries.getAllProductLocation().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        ProductLocationDao(
                            productLocationId = body.productLocationId,
                            rowItem = body.rowItem.toProductLocationItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteProductLocation() {
        retailTouch.productLocationQueries.deleteProductLocation()
    }

    override suspend fun insertProductBarcode(barcodeDao: BarcodeDao) {
        retailTouch.productBarcodeQueries.insert(
            barcodeId = barcodeDao.barcodeId,
            productId = barcodeDao.barcode.productId,
            productCode = barcodeDao.barcode.productCode?:"",
            barcode = barcodeDao.barcode.code?:"",
            rowItem = barcodeDao.barcode.toJson()
        )
    }

    override suspend fun updateProductBarcode(barcodeDao: BarcodeDao) {
        retailTouch.productBarcodeQueries.updateBarcode(
            barcodeId = barcodeDao.barcodeId,
            productId = barcodeDao.barcode.productId,
            productCode = barcodeDao.barcode.productCode?:"",
            barcode = barcodeDao.barcode.code?:"",
            rowItem = barcodeDao.barcode.toJson()
        )
    }

    override fun getAllBarcode(): Flow<List<Barcode>>  = flow{
        retailTouch.productBarcodeQueries.getAllBarcode().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        Barcode(
                             productId = body.productId,
                             productCode = body.productCode,
                             code = body.barcode
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }

    }

    override fun getItemByBarcode(code: String): Flow<Barcode?> = flow{
        retailTouch.productBarcodeQueries.getItemByBarcode(code).executeAsOneOrNull().let { body->
            //println("db data : $body")
            if(body!=null){
                emit(
                    Barcode(
                        productId = body.productId,
                        productCode = body.productCode,
                        code = body.barcode
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getItemByInventoryCode(code: String): Flow<Barcode?> = flow{
        retailTouch.productBarcodeQueries.getItemByProductCode(code).executeAsOneOrNull().let { body->
            //println("db_data : $body")
            if(body!=null){
                emit(
                    Barcode(
                        productId = body.productId,
                        productCode = body.productCode,
                        code = body.barcode
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getItemByProductId(code: Long): Flow<Barcode?> = flow{
        retailTouch.productBarcodeQueries.getItemByProductId(code).executeAsOneOrNull().let { body->
            //println("db data : $body")
            if(body!=null){
                emit(
                    Barcode(
                        productId = body.productId,
                        productCode = body.productCode,
                        code = body.barcode
                    )
                )
            }else{
                emit(body)
            }
        }
    }

     override suspend fun getBarcodeCount(): Flow<Int> = flow{
         retailTouch.productBarcodeQueries.getCount().executeAsOne().let {count->
             emit(count.toInt())
         }
     }

     override suspend fun deleteBarcode() {
        retailTouch.productBarcodeQueries.delete()
    }

    override suspend fun insertPromotions(promotionDao: PromotionDao) {
        retailTouch.promotionsQueries.insert(
            promotionId = promotionDao.promotionId,
            inventoryCode = promotionDao.inventoryCode,
            promotion = promotionDao.promotion.toJson()
        )
    }

    override suspend fun updatePromotions(promotionDao: PromotionDao) {
        retailTouch.promotionsQueries.updatePromotions(
            promotionId = promotionDao.promotionId,
            promotion = promotionDao.promotion.toJson()
        )
    }

    override fun getPromotions(): Flow<List<Promotion>> = flow{
        retailTouch.promotionsQueries.getPromotions().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        body.promotion.toPromotion()
                    }
                )
            }else{
                emit(emptyList())
            }
        }
    }

     override fun getPromotionById(id: Long): Flow<Promotion?> = flow{
       retailTouch.promotionsQueries.getPromotionsById(id).executeAsOneOrNull().let { body->
           if(body!=null){
             emit(
               body.promotion.toPromotion()
             )
           }else{
               emit(body)
           }
       }
    }

    override suspend fun deletePromotions() {
        retailTouch.promotionsQueries.delete()
    }

    override suspend fun insertPromotionDetails(promotionDetails: PromotionDetailsDao) {
        retailTouch.promotionDetailsQueries.insert(
             id =  promotionDetails.id.toLong(),
             promotionDetails = promotionDetails.promotionDetails.toJson()
        )
    }

    override suspend fun updatePromotionDetails(promotionDetails: PromotionDetailsDao) {

    }

    override fun getPromotionDetails(): Flow<List<PromotionDetails>> = flow{
        retailTouch.promotionDetailsQueries.getAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        body.promotionDetails.toPromotionDetails()
                    }
                )
            }else{
                emit(emptyList())
            }
        }
    }

    override fun getPromotionDetailsById(id: Long): Flow<PromotionDetails?> = flow{
        retailTouch.promotionDetailsQueries.getRowById(id).executeAsOneOrNull().let { body->
            if(body!=null){
                emit(body.promotionDetails.toPromotionDetails()
                )
            }else{
                emit(body)
            }
        }
    }

    override suspend fun getCount() : Flow<Int> = flow{
        retailTouch.promotionDetailsQueries.getCount().executeAsOne().let {count->
            emit(count.toInt())
        }
    }

    override suspend fun deletePromotionDetails() {
        retailTouch.promotionDetailsQueries.delete()
    }

    override suspend fun insertMembers(memberDao: MemberDao) {
        retailTouch.membersQueries.insert(
            memberId = memberDao.memberId,
            rowItem = memberDao.rowItem.toJson()
        )
    }

    override fun getAllMembers(): Flow<List<MemberDao>> = flow {
        retailTouch.membersQueries.getAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        MemberDao(
                            memberId = body.memberId,
                            rowItem = body.rowItem.toMemberItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteMembers() {
        retailTouch.membersQueries.delete()
    }

    override suspend fun insertMemberGroup(memberGroupDao: MemberGroupDao) {
        retailTouch.memberGroupQueries.insert(
            memberGroupId = memberGroupDao.memberGroupId,
            rowItem = memberGroupDao.rowItem.toJson()
        )
    }

    override fun getAllMemberGroup(): Flow<List<MemberGroupDao>> = flow{
        retailTouch.memberGroupQueries.getAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        MemberGroupDao(
                            memberGroupId = body.memberGroupId,
                            rowItem = body.rowItem.toMemberGroupItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteMemberGroup() {
        retailTouch.memberGroupQueries.delete()
    }

    override suspend fun insertPaymentType(paymentTypeDao: PaymentTypeDao) {
        retailTouch.paymentTypeQueries.insert(
            paymentId = paymentTypeDao.paymentId,
            rowItem = paymentTypeDao.rowItem.toJson()
        )
    }

    override fun getAllPaymentType(): Flow<List<PaymentTypeDao>> = flow{
        retailTouch.paymentTypeQueries.getAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        PaymentTypeDao(
                            paymentId = body.paymentId,
                            rowItem = body.rowItem.toPaymentTypeItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deletePaymentType() {
        retailTouch.paymentTypeQueries.delete()
    }

     override suspend fun insertPosPendingSaleRecord(posPaymentRecordDao: PendingSale) {
        retailTouch.posInvoicePendingSaleRecordQueries.insert(
            id = posPaymentRecordDao.id,
            isSync = posPaymentRecordDao.isSynced,
            posInvoice = posPaymentRecordDao.toJson()
        )
     }

     override suspend fun updatePosSales(posPaymentRecordDao: PendingSale) {
         println("posPaymentRecordDao :$posPaymentRecordDao")
         retailTouch.posInvoicePendingSaleRecordQueries.updatePosSale(
             ticketId = posPaymentRecordDao.id,
             isSynced = posPaymentRecordDao.isSynced,
             ticket = posPaymentRecordDao.toJson()
         )
     }

     override suspend fun updateSynced(id: Long) {
         retailTouch.posInvoicePendingSaleRecordQueries.updateSynced(isSynced = true, ticketId =id)
     }

     override fun getAllPosSale(): Flow<List<PendingSale>> = flow{
         retailTouch.posInvoicePendingSaleRecordQueries.getAll().executeAsList().let { list ->
             if(list.isNotEmpty()) {
                 emit(
                     list.map { body ->
                         val data= body.posInvoice.toPosInvoicePendingSaleRecord()
                         PendingSale(
                             id = data.id,
                             isSynced = data.isSynced,
                             locationId = data.locationId,
                             locationCode = data.locationCode
                         )
                     }

                 )
             }else{
                 emit(emptyList())
             }
         }
     }

     override fun getPendingSaleRecords(): Flow<List<PendingSale>> = flow{
         retailTouch.posInvoicePendingSaleRecordQueries.getPendingSale().executeAsList().let { list ->
             if(list.isNotEmpty()) {
                 emit(
                     list.map { body ->
                          body.posInvoice.toPosInvoicePendingSaleRecord()
                     }

                 )
             }else{
                 emit(emptyList())
             }
         }
     }

     override suspend fun deletePosPendingSaleRecord() {
         retailTouch.posInvoicePendingSaleRecordQueries.delete()
     }

     override suspend fun deleteSaleById(id: Long) {
         retailTouch.posInvoicePendingSaleRecordQueries.deleteById(id)
     }

     override fun getAllPendingSalesCount(): Flow<Long> = flow{
         retailTouch.posInvoicePendingSaleRecordQueries.getPendingSaleCount().executeAsOne().let {count->
             emit(count)
         }
     }

     override suspend fun insertPosDetailsRecord(posInvoice: PosSaleDetails) {
         retailTouch.posInvoiceDetailRecordQueries.insert(
             id = null,
             posInvoiceDetails = posInvoice.toJson()
         )
     }

     override fun getPosDetailsRecord(): Flow<List<PosSaleDetails>> = flow{
         retailTouch.posInvoiceDetailRecordQueries.getAll().executeAsList().let { list ->
             if(list.isNotEmpty()) {
                 emit(
                     list.map { body ->
                         val data= body.posInvoiceDetails.toPosInvoiceDetailRecord()
                         PosSaleDetails(
                             id = data.id,
                             productId = data.productId,
                             posPaymentRecordId = data.posPaymentRecordId
                         )
                     }

                 )
             }else{
                 emit(emptyList())
             }
         }
     }

     override suspend fun deletePosDetailsRecord() {
         retailTouch.posInvoiceDetailRecordQueries.delete()
     }

     override suspend fun insertPosConfiguredPaymentRecord(posInvoice: PosSalePayment) {
         retailTouch.posInvoiceConfiguredPaymentQueries.insert(
             id = null,
             posInvoiceConfiguredPayment = posInvoice.toJson()
         )
     }

     override fun getPosConfiguredPaymentRecord(): Flow<List<PosSalePayment>> =flow{
         retailTouch.posInvoiceConfiguredPaymentQueries.getAll().executeAsList().let { list ->
             if(list.isNotEmpty()) {
                 emit(
                     list.map { body ->
                        val data= body.posInvoiceConfiguredPayment.toPosPaymentConfigRecord()
                         PosSalePayment(
                             id = data.id,
                             paymentTypeId = data.paymentTypeId,
                             posInvoiceId = data.posInvoiceId,
                             amount = data.amount,
                             posPaymentRecordId = data.posPaymentRecordId,
                         )
                     }

                 )
             }else{
                 emit(emptyList())
             }
         }
     }

     override suspend fun deletePosConfiguredPaymentRecord() {
        retailTouch.posInvoiceConfiguredPaymentQueries.delete()
     }

     override suspend fun insertPrinter(printerDao: PrinterDao) {
         retailTouch.printersQueries.insertIntoPrinter(
            printerStationName = printerDao.printerStationName,
            printerName = printerDao.printerName,
            printerType =  printerDao.printerType,
             usbId = printerDao.selectedUsbId,
            paperSize = printerDao.paperSize,
            noOfCopies = printerDao.numbersOfCopies,
            networkAddress = printerDao.networkIpAddress,
             bluetoothAddress = printerDao.selectedBluetoothAddress,
            isRefund = printerDao.isRefund,
            isReceipts = printerDao.isReceipts,
            isOrders = printerDao.isOrders,
            templateId = printerDao.templateId,
         )
     }

     override suspend fun updatePrinter(printerDao: PrinterDao) {
         retailTouch.printersQueries.updatePrinter(
             id = printerDao.printerId,
             printerStationName = printerDao.printerStationName,
             printerName = printerDao.printerName,
             printerType =  printerDao.printerType,
             usbId = printerDao.selectedUsbId,
             paperSize = printerDao.paperSize,
             noOfCopies = printerDao.numbersOfCopies,
             networkAddress = printerDao.networkIpAddress,
             bluetoothAddress = printerDao.selectedBluetoothAddress,
             isRefund = printerDao.isRefund,
             isReceipt = printerDao.isReceipts,
             isOrders = printerDao.isOrders,
             templateId = printerDao.templateId,
         )
     }

     override fun getPrinter(): Flow<Printers?> = flow {
         emit(retailTouch.printersQueries.getAllPrinters().executeAsOneOrNull())
     }

     override suspend fun deleteAllPrinters() {
         retailTouch.printersQueries.deleteAll()
     }

     override suspend fun insertSyncAll(syncAllDao: SyncAllDao) {
        retailTouch.syncAllQueries.insertSyncAll(
            syncId = syncAllDao.syncId,
            rowItem = syncAllDao.rowItem.toJson()
        )
    }

    override fun getSyncAll(): Flow<List<SyncAllDao>> = flow{
        retailTouch.syncAllQueries.getAllSyncAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        SyncAllDao(
                            syncId = body.syncId,
                            rowItem = body.rowItem.toSyncItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteSyncAll() {
        retailTouch.syncAllQueries.deleteSyncAll()
    }


}