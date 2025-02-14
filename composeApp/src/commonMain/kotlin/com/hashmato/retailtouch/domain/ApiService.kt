package com.hashmato.retailtouch.domain


import com.hashmato.retailtouch.domain.model.basic.BasicApiRequest
import com.hashmato.retailtouch.domain.model.employee.EmployeesResponse
import com.hashmato.retailtouch.domain.model.employee.EmployeesRights
import com.hashmato.retailtouch.domain.model.location.LocationResponse
import com.hashmato.retailtouch.domain.model.login.LoginRequest
import com.hashmato.retailtouch.domain.model.login.LoginResponse
import com.hashmato.retailtouch.domain.model.memberGroup.MemberGroupResponse
import com.hashmato.retailtouch.domain.model.members.MemberResponse
import com.hashmato.retailtouch.domain.model.menu.CategoryResponse
import com.hashmato.retailtouch.domain.model.menu.MenuResponse
import com.hashmato.retailtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.hashmato.retailtouch.domain.model.paymentType.PaymentTypeResponse
import com.hashmato.retailtouch.domain.model.printer.GetPrintTemplateRequest
import com.hashmato.retailtouch.domain.model.printer.GetPrintTemplateResult
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.POSInvoiceRequest
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.GetPosInvoiceResult
import com.hashmato.retailtouch.domain.model.payout.CreateExpensesRequest
import com.hashmato.retailtouch.domain.model.payout.GetExpensesResult
import com.hashmato.retailtouch.domain.model.posInvoices.GetPosInvoiceForEditRequest
import com.hashmato.retailtouch.domain.model.posInvoices.GetPosInvoiceForEditResult
import com.hashmato.retailtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.hashmato.retailtouch.domain.model.productLocations.ProductLocationResponse
import com.hashmato.retailtouch.domain.model.products.CreatePOSInvoiceRequest
import com.hashmato.retailtouch.domain.model.products.PosInvoiceResponse
import com.hashmato.retailtouch.domain.model.products.ProductWithTaxByLocationResponse
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionsByQtyResult
import com.hashmato.retailtouch.domain.model.promotions.PromotionRequest
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionResult
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionsByPriceResult
import com.hashmato.retailtouch.domain.model.settlement.CreateEditPOSSettlementRequest
import com.hashmato.retailtouch.domain.model.settlement.CreateEditPOSSettlementResult
import com.hashmato.retailtouch.domain.model.settlement.GetPOSPaymentSummaryRequest
import com.hashmato.retailtouch.domain.model.settlement.PosPaymentSummaryResult
import com.hashmato.retailtouch.domain.model.sync.SyncAllResponse
import com.hashmato.retailtouch.domain.model.terminal.TerminalResponse
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
    fun getLatestSales(mBasicApiRequest: POSInvoiceRequest): Flow<RequestState<GetPosInvoiceResult>>
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
    fun getPrintTemplate(mBasicApiRequest: GetPrintTemplateRequest): Flow<RequestState<GetPrintTemplateResult>>
    fun getPosInvoiceForEdit(mBasicApiRequest: GetPosInvoiceForEditRequest): Flow<RequestState<GetPosInvoiceForEditResult>>
    fun getPosPaymentSummary(mBasicApiRequest: GetPOSPaymentSummaryRequest): Flow<RequestState<PosPaymentSummaryResult>>
    fun createOrUpdatePosSettlementOutput(mBasicApiRequest: CreateEditPOSSettlementRequest):Flow<RequestState<CreateEditPOSSettlementResult>>
    fun createOrUpdatePayoutIn(mBasicApiRequest: CreateExpensesRequest):Flow<RequestState<GetExpensesResult>>
}