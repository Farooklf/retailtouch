package com.lfssolutions.retialtouch.domain



import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.inventory.Product
import com.lfssolutions.retialtouch.domain.model.inventory.Stock
import com.lfssolutions.retialtouch.domain.model.location.LocationDao
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupDao
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.menu.CategoryDao
import com.lfssolutions.retialtouch.domain.model.menu.MenuDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleDao
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeDao
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceDao
import com.lfssolutions.retialtouch.domain.model.productBarCode.Barcode
import com.lfssolutions.retialtouch.domain.model.productBarCode.BarcodeDao
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationDao
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxDao
import com.lfssolutions.retialtouch.domain.model.productWithTax.ScannedProductDao
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDao
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetailsDao
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionQtyDetails
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllDao
import kotlinx.coroutines.flow.Flow

interface SqlPreference {

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

    suspend fun insertEmpRights(employeeDao: EmployeeDao)
    fun getAllEmpRights(): Flow<List<EmployeeDao>>
    suspend fun deleteEmpRights()

    //Member
    suspend fun insertMembers(memberDao: MemberDao)
    fun getAllMembers(): Flow<List<MemberDao>>
    suspend fun deleteMembers()

    //Member Group
    suspend fun insertMemberGroup(memberGroupDao: MemberGroupDao)
    fun getAllMemberGroup(): Flow<List<MemberGroupDao>>
    suspend fun deleteMemberGroup()

    //Menu Category
    suspend fun insertMenuCategories(menuCategoriesDao: CategoryDao)
    fun selectCategoryById(id: Long): Flow<CategoryDao?>
    fun getAllCategories(): Flow<List<CategoryDao>>
    suspend fun deleteCategories()
    fun getMenuCategoriesCount():Flow<Int>


    //Menu Products
    suspend fun insertStocks(menuProductsDao: MenuDao)
    fun selectProductsById(id: Long): Flow<MenuDao?>
    fun getStocks(): Flow<List<MenuDao>>
    suspend fun deleteStocks()
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
    suspend fun insertProduct(productTaxDao: ProductTaxDao)
    suspend fun updateProduct(productTaxDao: ProductTaxDao)
    fun getAllProduct(): Flow<List<Product>>
    fun getProductById(id: Long): Flow<Product?>
    fun getProductByCode(code: String): Flow<Product?>
    suspend fun deleteProduct()

    //Scanned ProductWithTax
    suspend fun insertScannedProduct(productTaxDao: ScannedProductDao)
    suspend fun updateScannedProduct(productTaxDao: ScannedProductDao)
    fun fetchAllScannedProduct(): Flow<List<ScannedProductDao>>
    suspend fun deleteScannedProductById(productId: Long)
    suspend fun deleteAllScannedProduct()

    //ProductLocation
    suspend fun insertProductLocation(productLocationDao: ProductLocationDao)
    suspend fun updateProductLocation(productLocationDao: ProductLocationDao)
    fun getAllProductLocation(): Flow<List<ProductLocationDao>>
    suspend fun deleteProductLocation()

    //BarCode
    suspend fun insertProductBarcode(barcodeDao: BarcodeDao)
    suspend fun updateProductBarcode(barcodeDao: BarcodeDao)
    fun getAllBarcode(): Flow<List<Barcode>>
    fun getItemByBarcode(code:String): Flow<Barcode?>
    fun getItemByInventoryCode(code:String): Flow<Barcode?>
    fun getItemByProductId(code:Long): Flow<Barcode?>
    suspend fun deleteBarcode()

    //Promotions
    suspend fun insertPromotions(promotionDao: PromotionDao)
    suspend fun updatePromotions(promotionDao: PromotionDao)
    fun getPromotions(): Flow<List<Promotion>>
    fun getPromotionById(id: Long): Flow<Promotion?>
    suspend fun deletePromotions()

    //Promotion Details
    suspend fun insertPromotionDetails(promotionDetails: PromotionDetailsDao)
    suspend fun updatePromotionDetails(promotionDetails: PromotionDetailsDao)
    fun getPromotionDetails(): Flow<List<PromotionDetails>>
    fun getPromotionDetailsById(id: Long): Flow<PromotionDetails?>
    suspend fun getCount():Flow<Int>
    suspend fun deletePromotionDetails()


    //Payment Type
    suspend fun insertPaymentType(paymentTypeDao: PaymentTypeDao)
    fun getAllPaymentType(): Flow<List<PaymentTypeDao>>
    suspend fun deletePaymentType()


    //Sync
    suspend fun insertSyncAll(syncAllDao: SyncAllDao)
    fun getSyncAll(): Flow<List<SyncAllDao>>
    suspend fun deleteSyncAll()
}