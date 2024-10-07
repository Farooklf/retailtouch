package com.lfssolutions.retialtouch.dataBase



import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.location.LocationDao
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
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {

    suspend fun insertAuthentication(authenticateDao: AuthenticateDao)
    fun selectUserByUserId(userId: Long): Flow<AuthenticateDao>
    fun getAllAuthentication(): Flow<AuthenticateDao>
    suspend fun deleteAuthentication()

    //Location
    suspend fun insertLocation(locationDao: LocationDao)
    suspend fun deleteAllLocations()

    //Employees
    suspend fun insertEmployee(employeeDao: EmployeeDao)
    fun getEmployeeByCode(employeeCode: String): Flow<EmployeeDao?>
    suspend fun deleteAllEmployee()


    //Employee Role
    suspend fun insertEmpRole(employeeDao: EmployeeDao)
    fun getAllEmpRole(): Flow<List<EmployeeDao>>
    suspend fun deleteAllEmpRole()

    //Menu Category
    suspend fun insertMenuCategories(menuCategoriesDao: MenuCategoriesDao)
    fun selectCategoryById(id: Long): Flow<MenuCategoriesDao?>
    fun getAllCategories(): Flow<List<MenuCategoriesDao>>
    suspend fun deleteCategories()
    fun getMenuCategoriesCount():Flow<Int>


    //Menu Products
    suspend fun insertMenuProducts(menuProductsDao: MenuProductsDao)
    fun selectProductsById(id: Long): Flow<MenuProductsDao?>
    fun getAllProducts(): Flow<List<MenuProductsDao>>
    suspend fun deleteProducts()
    fun getMenuCProductsCount():Flow<Int>


    //Next POS Sale

    suspend fun insertNextPosSale(nextPOSSaleDao: NextPOSSaleDao)
    fun getNextPosSaleById(id: Long): Flow<NextPOSSaleDao?>
    fun getAllNextPosSale(): Flow<List<NextPOSSaleDao>>
    suspend fun deleteNextPosSale()
    fun getNextPosSaleCount():Flow<Int>


    //POS Invoice
    suspend fun insertPosInvoice(posInvoiceDao: POSInvoiceDao)
    fun getPosInvoiceById(id: Long): Flow<POSInvoiceDao?>
    fun getAllPosInvoice(): Flow<List<POSInvoiceDao>>
    suspend fun deletePosInvoice()
    fun getPosInvoiceCount():Flow<Int>

    //ProductWithTax
    suspend fun insertProductWithTax(productTaxDao: ProductTaxDao)
    suspend fun updateProductWithTax(productTaxDao: ProductTaxDao)
    fun getAllProductWithTax(): Flow<List<ProductTaxDao>>
    suspend fun deleteProductWithTax()

    //Scanned ProductWithTax
    suspend fun insertScannedProduct(productTaxDao: ScannedProductDao)
    suspend fun updateScannedProduct(productTaxDao: ScannedProductDao)
    fun fetchAllScannedProduct(): Flow<List<ScannedProductDao>>
    suspend fun deleteScannedProductById(productId: Long)
    suspend fun deleteAllScannedProduct()

    //ProductLocation
    suspend fun insertProductLocation(productLocationDao: ProductLocationDao)
    fun getAllProductLocation(): Flow<List<ProductLocationDao>>
    suspend fun deleteProductLocation()

    //Member
    suspend fun insertMembers(memberDao: MemberDao)
    fun getAllMembers(): Flow<List<MemberDao>>
    suspend fun deleteMembers()

    //Member Group
    suspend fun insertMemberGroup(memberGroupDao: MemberGroupDao)
    fun getAllMemberGroup(): Flow<List<MemberGroupDao>>
    suspend fun deleteMemberGroup()

    //Payment Type
    suspend fun insertPaymentType(paymentTypeDao: PaymentTypeDao)
    fun getAllPaymentType(): Flow<List<PaymentTypeDao>>
    suspend fun deletePaymentType()


    //Sync
    suspend fun insertSyncAll(syncAllDao: SyncAllDao)
    fun getSyncAll(): Flow<List<SyncAllDao>>
    suspend fun deleteSyncAll()
}