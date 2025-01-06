package com.lfssolutions.retialtouch.domain



import com.lfssolutions.retialtouch.domain.model.employee.POSEmployee
import com.lfssolutions.retialtouch.domain.model.employee.POSEmployeeRight
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosSalePayment
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosSaleDetails
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.location.Location
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
import comlfssolutionsretialtouch.Printers
import kotlinx.coroutines.flow.Flow

interface SqlPreference {

    suspend fun insertAuthentication(authenticateDao: AuthenticateDao)
    fun selectUserByUserId(userId: Long): Flow<AuthenticateDao>
    fun getAllAuthentication(): Flow<AuthenticateDao>
    fun getAuthUser(): Flow<RTLoginUser>
    suspend fun deleteAuthentication()

    //Location
    suspend fun insertLocation(locationDao: Location)
    fun getSelectedLocation() : Flow<Location?>
    suspend fun deleteAllLocations()

    //Employees
    suspend fun insertEmployee(mPOSEmployee: POSEmployee)
    suspend fun updatePOSEmployee(mPOSEmployee: POSEmployee)
    fun getEmployeeByCode(employeeCode: String): Flow<POSEmployee?>
    fun getPOSEmployees(): Flow<List<POSEmployee>>
    suspend fun deleteAllEmployee()


    //Employee Role
    suspend fun insertEmpRole(mPOSEmployee: POSEmployee)
    fun getAllEmpRole(): Flow<List<POSEmployee>>
    suspend fun deleteAllEmpRole()

    suspend fun insertEmpRights(mPOSEmployeeRight: POSEmployeeRight)
    fun getAllEmpRights(): Flow<List<POSEmployeeRight>>
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
    suspend fun insertStockCategories(menuCategoriesDao: CategoryDao)
    fun selectCategoryById(id: Long): Flow<CategoryDao?>
    fun getAllCategories(): Flow<List<StockCategory>>
    suspend fun deleteCategories()
    fun getMenuCategoriesCount():Flow<Int>


    //Menu Products
    suspend fun insertStocks(menuProductsDao: MenuDao)
    fun selectProductsById(id: Long): Flow<MenuDao?>
    fun getStocks(): Flow<List<Stock>>
    suspend fun deleteStocks()
    fun getMenuCProductsCount():Flow<Int>


    //Next POS Sale

    suspend fun insertNextPosSale(nextPOSSaleDao: NextPOSSaleDao)
    fun getNextPosSaleById(id: Long): Flow<NextPOSSaleDao?>
    fun getAllNextPosSale(): Flow<List<NextPOSSaleDao>>
    suspend fun deleteNextPosSale()
    fun getNextPosSaleCount():Flow<Int>


    //Latest POS Invoice Sales
    suspend fun insertLatestSales(saleRecord: SaleRecord)
    fun getLatestSalesById(id: Long): Flow<SaleRecord?>
    fun getLatestSales(): Flow<List<SaleRecord>>
    suspend fun deletePosSales()
    fun getSalesCount():Flow<Int>

    //Product
    suspend fun insertProduct(productDao: ProductDao)
    suspend fun updateProduct(productDao: ProductDao)
    suspend fun updateProductQuantity(productCode: String,quantity:Double)
    fun getAllProduct(): Flow<List<Product>>
    fun getProductById(id: Long): Flow<Product?>
    fun getProductByCode(code: String): Flow<Product?>
    fun getProductQty(code: String) : Flow<Double>
    suspend fun deleteProduct()

    //Scanned Product
    suspend fun insertScannedProduct(productTaxDao: ScannedProductDao)
    suspend fun updateScannedProduct(productTaxDao: ScannedProductDao)
    fun fetchAllScannedProduct(): Flow<List<ScannedProductDao>>
    suspend fun deleteScannedProductById(productId: Long)
    suspend fun deleteAllScannedProduct()

    //Hold Sale
    suspend fun insertHoldSaleRecord(crSaleOnHold: SaleOnHoldRecordDao)
    suspend fun updateHoldSaleRecord(crSaleOnHold: SaleOnHoldRecordDao)
    fun getAllHoldSaleRecord(): Flow<List<CRSaleOnHold>>
    fun getHoldSaleById(id: Long): Flow<CRSaleOnHold?>
    suspend fun deleteHoldSaleById(id: Long)
    suspend fun deleteHoldSale()

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

    //posInvoiceDetails
    suspend fun insertPosPendingSaleRecord(posPaymentRecordDao: PendingSale)
    suspend fun updatePosSales(posPaymentRecordDao: PendingSale)
    suspend fun updateSynced(id:Long)
    fun getAllPosSale(): Flow<List<PendingSale>>
    fun getPendingSaleRecords(): Flow<List<PendingSale>>
    suspend fun deletePosPendingSaleRecord()
    suspend fun deleteSaleById(id:Long)
    fun getAllPendingSalesCount():Flow<Long>

    suspend fun insertPosDetailsRecord(posInvoice: PosSaleDetails)
    fun getPosDetailsRecord(): Flow<List<PosSaleDetails>>
    suspend fun deletePosDetailsRecord()

    suspend fun insertPosConfiguredPaymentRecord(posInvoice: PosSalePayment)
    fun getPosConfiguredPaymentRecord(): Flow<List<PosSalePayment>>
    suspend fun deletePosConfiguredPaymentRecord()

    //Printer
    suspend fun insertPrinter(printerDao: PrinterDao)
    suspend fun updatePrinter(printerDao: PrinterDao)
    fun getPrinter():Flow<Printers?>
    suspend fun deleteAllPrinters()

    //Sync
    suspend fun insertSyncAll(syncAllDao: SyncAllDao)
    fun getSyncAll(): Flow<List<SyncAllDao>>
    suspend fun deleteSyncAll()
}