package com.hashmato.retailtouch.domain



import com.hashmato.retailtouch.domain.model.employee.POSEmployee
import com.hashmato.retailtouch.domain.model.employee.POSEmployeeRight
import com.hashmato.retailtouch.domain.model.posInvoices.PosSalePayment
import com.hashmato.retailtouch.domain.model.posInvoices.PosSaleDetails
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSale
import com.hashmato.retailtouch.domain.model.products.POSProduct
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.login.AuthenticateDao
import com.hashmato.retailtouch.domain.model.memberGroup.MemberGroupDao
import com.hashmato.retailtouch.domain.model.members.Member
import com.hashmato.retailtouch.domain.model.menu.CategoryDao
import com.hashmato.retailtouch.domain.model.menu.MenuDao
import com.hashmato.retailtouch.domain.model.paymentType.PaymentTypeDao
import com.hashmato.retailtouch.domain.model.printer.PrinterDao
import com.hashmato.retailtouch.domain.model.productBarCode.Barcode
import com.hashmato.retailtouch.domain.model.productBarCode.BarcodeDao
import com.hashmato.retailtouch.domain.model.productLocations.ProductLocationDao
import com.hashmato.retailtouch.domain.model.products.CRSaleOnHold
import com.hashmato.retailtouch.domain.model.products.ProductDao
import com.hashmato.retailtouch.domain.model.products.SaleOnHoldRecordDao
import com.hashmato.retailtouch.domain.model.products.ScannedProductDao
import com.hashmato.retailtouch.domain.model.promotions.Promotion
import com.hashmato.retailtouch.domain.model.promotions.PromotionDao
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetailsDao
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.hashmato.retailtouch.domain.model.login.RTLoginUser
import com.hashmato.retailtouch.domain.model.menu.StockCategory
import com.hashmato.retailtouch.domain.model.printer.PrintReceiptTemplate
import com.hashmato.retailtouch.domain.model.products.Stock
import com.hashmato.retailtouch.domain.model.sync.SyncAllDao
import comhashmatoretailtouchsqldelight.Printers
import kotlinx.coroutines.flow.Flow

interface SqlRepository {

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
    suspend fun insertMembers(member: Member)
    fun getAllMembers(): Flow<List<Member>>
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
    suspend fun deleteMenuItems()
    fun getMenuItemsCount():Flow<Int>


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
    fun getAllProduct(): Flow<List<POSProduct>>
    fun getProducts(): Flow<POSProduct>
    fun getProductById(id: Long): Flow<POSProduct?>
    fun getProductByCode(code: String): Flow<POSProduct?>
    fun getProductByBarCode(code: String): Flow<POSProduct?>
    fun getSearchedProducts(query: String): Flow<POSProduct?>
    fun getProductQty(code: String) : Flow<Double>
    fun getProductCount():Flow<Int>
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

    //ProductQuantity
    suspend fun insertProductLocation(productLocationDao: ProductLocationDao)
    suspend fun updateProductLocation(productLocationDao: ProductLocationDao)
    fun getAllProductLocation(): Flow<List<ProductLocationDao>>
    suspend fun deleteStocksQty()

    //BarCode
    suspend fun insertProductBarcode(barcodeDao: BarcodeDao)
    suspend fun updateProductBarcode(barcodeDao: BarcodeDao)
    fun getAllBarcode(): Flow<List<Barcode>>
    fun getItemByBarcode(code:String): Flow<Barcode?>
    fun getItemByInventoryCode(code:String): Flow<Barcode?>
    fun getItemByProductId(code:Long): Flow<Barcode?>
    suspend fun getBarcodeCount():Flow<Int>
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
    suspend fun insertPosInvoiceSales(mPendingSale: PendingSale)
    suspend fun updatePosSales(mPendingSale: PendingSale)
    suspend fun updateSynced(id:Long)
    fun getAllPosSale(): Flow<List<PendingSale>>
    fun getPendingSales(): Flow<List<PendingSale>>
    suspend fun deletePosPendingSales()
    suspend fun deleteSaleById(id:Long)
    fun getAllPendingSalesCount():Flow<Long>

    //Printer
    suspend fun insertPrinter(printerDao: PrinterDao)
    suspend fun updatePrinter(printerDao: PrinterDao)
    fun getPrinter():Flow<Printers?>
    suspend fun deleteAllPrinters()

    //Receipt template
    suspend fun insertTemplate(printReceiptTemplate: PrintReceiptTemplate)
    fun getPrintTemplateByType(type:Long):Flow<PrintReceiptTemplate?>
    suspend fun deletePOSReceiptTemplate()


}