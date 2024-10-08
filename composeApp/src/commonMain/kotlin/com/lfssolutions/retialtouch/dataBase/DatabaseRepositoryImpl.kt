package com.lfssolutions.retialtouch.dataBase


import com.lfssolutions.retialtouch.domain.model.location.LocationDao
import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupDao
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.menu.MenuCategoriesDao
import com.lfssolutions.retialtouch.domain.model.menu.MenuProductsDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleDao
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeDao
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceDao
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationDao
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxDao
import com.lfssolutions.retialtouch.domain.model.productWithTax.ScannedProductDao
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllDao
import com.lfssolutions.retialtouch.retailTouchDB
import com.lfssolutions.retialtouch.utils.serializers.db.toJson
import com.lfssolutions.retialtouch.utils.serializers.db.toLogin
import com.lfssolutions.retialtouch.utils.serializers.db.toMemberGroupItem
import com.lfssolutions.retialtouch.utils.serializers.db.toMemberItem
import com.lfssolutions.retialtouch.utils.serializers.db.toMenuCategoryItem
import com.lfssolutions.retialtouch.utils.serializers.db.toMenuProductItem
import com.lfssolutions.retialtouch.utils.serializers.db.toNextPosSaleItem
import com.lfssolutions.retialtouch.utils.serializers.db.toPaymentTypeItem
import com.lfssolutions.retialtouch.utils.serializers.db.toPosInvoiceItem
import com.lfssolutions.retialtouch.utils.serializers.db.toProductLocationItem
import com.lfssolutions.retialtouch.utils.serializers.db.toProductTaxItem
import com.lfssolutions.retialtouch.utils.serializers.db.toSyncItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow



class DatabaseRepositoryImpl(driverFactory: DatabaseDriverFactory) : DatabaseRepository{

    private var retailTouch: retailTouchDB = retailTouchDB(driverFactory.create())

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
            println("dbAuth:$body")
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

    override suspend fun deleteAuthentication() {
        retailTouch.userTenanatQueries.deleteAuth()
    }

    override suspend fun insertLocation(locationDao: LocationDao) {
        retailTouch.userLocationQueries.insertLocation(
            locationId  = locationDao.locationId,
            name = locationDao.name,
            code = locationDao.code,
            address1 = locationDao.address1,
            address2 = locationDao.address2,
            country = locationDao.country,
            isSelected = locationDao.isSelected
        )
    }

    override suspend fun deleteAllLocations() {
        retailTouch.userLocationQueries.deleteAllLocation()
    }

    override suspend fun insertEmployee(employeeDao: EmployeeDao) {
        retailTouch.employeesQueries.insertEmployees(
            employeeId = employeeDao.employeeId.toLong(),
            employeeCode = employeeDao.employeeCode.uppercase(),
            employeeName = employeeDao.employeeName,
            employeeRoleName = employeeDao.employeeRoleName,
            employeePassword = employeeDao.employeePassword,
            employeeCategoryName = employeeDao.employeeCategoryName,
            employeeDepartmentName = employeeDao.employeeDepartmentName,
            creationTime = employeeDao.creationTime,
            isAdmin = employeeDao.isAdmin,
            isDeleted = employeeDao.isDeleted
        )
    }

    override fun getEmployeeByCode(employeeCode: String): Flow<EmployeeDao?> = flow {
        retailTouch.employeesQueries.selectEmployeeByCode(employeeCode.uppercase()).executeAsOneOrNull().let { body->
            println("employee : $body")
            if(body!=null){
                emit(
                    EmployeeDao(
                        employeeId = body.employeeId.toInt(),
                        employeeCode = body.employeeCode,
                        employeeName = body.employeeName,
                        employeePassword = body.employeePassword,
                        employeeCategoryName = body.employeeCategoryName,
                        employeeDepartmentName = body.employeeDepartmentName,
                        employeeRoleName = body.employeeRoleName,
                        isAdmin = body.isAdmin ?: false,
                        isDeleted = body.isDeleted ?: false
                    )
                )
            }else{
                emit(body)
            }
        }

    }

    override suspend fun deleteAllEmployee() {
        retailTouch.employeesQueries.deleteEmployees()
    }

    override suspend fun insertEmpRole(employeeDao: EmployeeDao) {
        retailTouch.employeeRoleQueries.insertEmpRole(
            empRoleId = employeeDao.employeeId.toLong(),
            empRoleName = employeeDao.employeeName,
            isAdmin = employeeDao.isAdmin,
            isDeleted = employeeDao.isDeleted
        )
    }

    override fun getAllEmpRole(): Flow<List<EmployeeDao>> = flow {
        retailTouch.employeeRoleQueries.getAllEmpRole().executeAsList().let { list ->
            emit(
                list.map { body ->
                    EmployeeDao(
                        employeeId = body.empRoleId.toInt(),
                        employeeName = body.empRoleName,
                        isDeleted = body.isDeleted ?: false,
                        isAdmin = body.isAdmin ?: false
                    )
                }

            )
        }
    }

    override suspend fun deleteAllEmpRole() {
        retailTouch.employeeRoleQueries.deleteEmpRole()
    }

    override suspend fun insertMenuCategories(menuCategoriesDao: MenuCategoriesDao) {
        retailTouch.menuCategoryQueries.insertMenuCategory(
            categoryId = menuCategoriesDao.categoryId,
            categoryItem = menuCategoriesDao.categoryItem.toJson()
        )
    }

    override fun selectCategoryById(id: Long): Flow<MenuCategoriesDao?> = flow{
        retailTouch.menuCategoryQueries.getCategoryById(id).executeAsOneOrNull().let { body->
            println("menu category : $body")
            if(body!=null){
                emit(
                    MenuCategoriesDao(
                        categoryId = body.categoryId,
                        categoryItem = body.categoryItem.toMenuCategoryItem()
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getAllCategories(): Flow<List<MenuCategoriesDao>> = flow{
        retailTouch.menuCategoryQueries.getAllMenuCategory().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        MenuCategoriesDao(
                            categoryId = body.categoryId,
                            categoryItem = body.categoryItem.toMenuCategoryItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
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

    override suspend fun insertMenuProducts(menuProductsDao: MenuProductsDao) {
        retailTouch.menuProductQueries.insertMenuProduct(
            productId = menuProductsDao.productId,
            productItem = menuProductsDao.menuProductItem.toJson()
        )
    }

    override fun selectProductsById(id: Long): Flow<MenuProductsDao?> = flow{
        retailTouch.menuProductQueries.getProductById(id).executeAsOneOrNull().let { body->
            println("menu products : $body")
            if(body!=null){
                emit(
                    MenuProductsDao(
                        productId = body.productId,
                        menuProductItem = body.productItem.toMenuProductItem()
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getAllProducts(): Flow<List<MenuProductsDao>> = flow{
        retailTouch.menuProductQueries.getAllMenuProduct().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        MenuProductsDao(
                            productId = body.productId,
                            menuProductItem = body.productItem.toMenuProductItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteProducts() {
        retailTouch.menuProductQueries.deleteMenuProduct()
    }

    override  fun getMenuCProductsCount():  Flow<Int> = flow {
        retailTouch.menuProductQueries.countMenuProducts().executeAsOne().let {count->
            emit(count.toInt())
        }
    }

    override suspend fun insertNextPosSale(nextPOSSaleDao: NextPOSSaleDao) {
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
    }

    override suspend fun insertPosInvoice(posInvoiceDao: POSInvoiceDao) {
        retailTouch.pOSInvoiceQueries.insert(
            posInvoiceId = posInvoiceDao.posInvoiceId,
            totalCount = posInvoiceDao.totalCount,
            rowItem = posInvoiceDao.posItem.toJson()
        )
    }

    override fun getPosInvoiceById(id: Long): Flow<POSInvoiceDao?> = flow{
        retailTouch.pOSInvoiceQueries.getRowById(id).executeAsOneOrNull().let { body->
            println("pos invoice : $body")
            if(body!=null){
                emit(
                    POSInvoiceDao(
                        posInvoiceId = body.posInvoiceId,
                        totalCount = body.totalCount,
                        posItem = body.rowItem.toPosInvoiceItem()
                    )
                )
            }else{
                emit(body)
            }
        }
    }

    override fun getAllPosInvoice(): Flow<List<POSInvoiceDao>> = flow{
        retailTouch.pOSInvoiceQueries.getAll().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        POSInvoiceDao(
                            posInvoiceId = body.posInvoiceId,
                            totalCount = body.totalCount,
                            posItem = body.rowItem.toPosInvoiceItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deletePosInvoice() {
        retailTouch.pOSInvoiceQueries.delete()
    }

    override fun getPosInvoiceCount(): Flow<Int> = flow{
        retailTouch.pOSInvoiceQueries.getCount().executeAsOne().let {count->
            emit(count.toInt())
        }
    }

    override suspend fun insertProductWithTax(productTaxDao: ProductTaxDao) {
        retailTouch.productWithTaxQueries.insertProductWithTax(
            productTaxId = productTaxDao.productTaxId,
            isScanned = productTaxDao.isScanned,
            rowItem = productTaxDao.rowItem.toJson()
        )
    }

    override suspend fun updateProductWithTax(productTaxDao: ProductTaxDao) {
       retailTouch.productWithTaxQueries.updateProductWithTax(
           rowItem = productTaxDao.rowItem.toJson(),
           productTaxId = productTaxDao.productTaxId
       )
    }

    override fun getAllProductWithTax(): Flow<List<ProductTaxDao>> = flow{
        retailTouch.productWithTaxQueries.getAllProductWithTax().executeAsList().let { list ->
            if(list.isNotEmpty()) {
                emit(
                    list.map { body ->
                        ProductTaxDao(
                            productTaxId = body.productTaxId,
                            rowItem = body.rowItem.toProductTaxItem()
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun deleteProductWithTax() {
        retailTouch.productWithTaxQueries.deleteProductWithTax()
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
                            taxPercentage = body.taxPercentage
                        )
                    }

                )
            }else{
                emit(emptyList())
            }
        }
    }

   /* override suspend fun deleteScannedProductById(productId: Long) {
        retailTouch.scannedProductWithTaxQueries.deleteProductById(productId)
    }

    override suspend fun deleteAllScannedProduct() {
       retailTouch.scannedProductWithTaxQueries.deleteAllProduct()
    }*/

    override suspend fun insertProductLocation(productLocationDao: ProductLocationDao) {
        retailTouch.productLocationQueries.insertProductLocation(
            productLocationId = productLocationDao.productLocationId,
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
        retailTouch.productWithTaxQueries.deleteProductWithTax()
    }

    override suspend fun insertMembers(memberDao: MemberDao) {
        retailTouch.membersQueries.insert(
            memberId = memberDao.memberId,
            rowItem = memberDao.rowItem.toJson()
        )
    }

    override fun getAllMembers(): Flow<List<MemberDao>> = flow{
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