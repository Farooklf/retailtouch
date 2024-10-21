package com.lfssolutions.retialtouch.domain


import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesRights
import com.lfssolutions.retialtouch.domain.model.location.LocationResponse
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.menu.CategoryResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuResponse
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceResponse
import com.lfssolutions.retialtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.CreatePOSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosInvoiceResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByQtyResult
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionResult
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByPriceResult
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllResponse
import com.lfssolutions.retialtouch.domain.model.terminal.TerminalResponse
import kotlinx.coroutines.flow.Flow

interface ApiService {

    fun hitLoginAPI(loginRequest: LoginRequest): Flow<RequestState<LoginResponse>>
    fun getLocation(mBasicApiRequest: BasicApiRequest): Flow<RequestState<LocationResponse>>
    fun getTerminal(mBasicApiRequest: BasicApiRequest): Flow<RequestState<TerminalResponse>>
    fun getEmployees(mBasicApiRequest: BasicApiRequest): Flow<RequestState<EmployeesResponse>>
    fun getTEmployeeRoles(mBasicApiRequest: BasicApiRequest): Flow<RequestState<EmployeesResponse>>
    fun getEmployeeRights(mBasicApiRequest: BasicApiRequest): Flow<RequestState<EmployeesRights>>
    fun getMenuCategories(mBasicApiRequest: BasicApiRequest): Flow<RequestState<CategoryResponse>>
    fun getMenuProducts(mBasicApiRequest: BasicApiRequest): Flow<RequestState<MenuResponse>>
    fun getNextPOSSaleInvoice(mBasicApiRequest: BasicApiRequest): Flow<RequestState<NextPOSSaleInvoiceNoResponse>>
    fun getPOSInvoice(mBasicApiRequest: POSInvoiceRequest): Flow<RequestState<POSInvoiceResponse>>
    fun getProductsWithTax(mBasicApiRequest: BasicApiRequest): Flow<RequestState<ProductWithTaxByLocationResponse>>
    fun getProductLocation(mBasicApiRequest: BasicApiRequest): Flow<RequestState<ProductLocationResponse>>
    fun getMembers(mBasicApiRequest: BasicApiRequest): Flow<RequestState<MemberResponse>>
    fun getMemberGroup(mBasicApiRequest: BasicApiRequest): Flow<RequestState<MemberGroupResponse>>
    fun getPaymentTypes(mBasicApiRequest: BasicApiRequest): Flow<RequestState<PaymentTypeResponse>>
    fun getPromotions(mBasicApiRequest: PromotionRequest): Flow<RequestState<GetPromotionResult>>
    fun getPromotionsByQty(mBasicApiRequest: PromotionRequest): Flow<RequestState<GetPromotionsByQtyResult>>
    fun getPromotionsByPrice(mBasicApiRequest: PromotionRequest): Flow<RequestState<GetPromotionsByPriceResult>>
    fun getProductBarCode(mBasicApiRequest: BasicApiRequest): Flow<RequestState<ProductBarCodeResponse>>
    fun syncAllApis(mBasicApiRequest: BasicApiRequest): Flow<RequestState<SyncAllResponse>>
    fun createUpdatePosInvoice(mBasicApiRequest: CreatePOSInvoiceRequest): Flow<RequestState<PosInvoiceResponse>>
}