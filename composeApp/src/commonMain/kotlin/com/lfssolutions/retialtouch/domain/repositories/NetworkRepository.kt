package com.lfssolutions.retialtouch.domain.repositories

import com.lfssolutions.retialtouch.domain.ApiService
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.printer.GetPrintTemplateRequest
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.posInvoices.GetPosInvoiceForEditRequest
import com.lfssolutions.retialtouch.domain.model.products.CreatePOSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.domain.model.settlement.CreateEditPOSSettlementRequest
import com.lfssolutions.retialtouch.domain.model.settlement.GetPOSPaymentSummaryRequest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NetworkRepository : KoinComponent {
    private val api: ApiService by inject()

    fun hitLoginAPI(loginRequest: LoginRequest) =
        api.hitLoginAPI(loginRequest)

     fun getLocationForUser(mRequest: BasicApiRequest) =
        api.getLocation(mRequest)


    fun getTerminal(mRequest: BasicApiRequest) =
        api.getTerminal(mRequest)


    fun getEmployees(mRequest: BasicApiRequest) =
        api.getEmployees(mRequest)


    fun getEmployeeRole(mRequest: BasicApiRequest) =
        api.getTEmployeeRoles(mRequest)

    fun getEmployeeRights(mRequest: BasicApiRequest) =
        api.getEmployeeRights(mRequest)


    fun getMenuCategories(mRequest: BasicApiRequest) = 
        api.getMenuCategories(mRequest)
    

     fun getMenuProducts(mRequest: BasicApiRequest) =  
        api.getMenuProducts(mRequest)
    

     fun getNextPOSSaleInvoice(mRequest: BasicApiRequest) =  
        api.getNextPOSSaleInvoice(mRequest)
   

     fun getLatestSales(mRequest: POSInvoiceRequest) =
        api.getLatestSales(mRequest)
   

     fun getProductsWithTax(mRequest: BasicApiRequest) =  
        api.getProductsWithTax(mRequest)
   

     fun getProductLocation(menuRequest: BasicApiRequest) =  
        api.getProductLocation(menuRequest)
   

     fun getMembers(mRequest: BasicApiRequest) =  
        api.getMembers(mRequest)
   

     fun getMemberGroup(mRequest: BasicApiRequest) =
         api.getMemberGroup(mRequest)

     fun getPaymentTypes(mRequest: BasicApiRequest) =  
        api.getPaymentTypes(mRequest)

    fun getPromotions(mRequest: PromotionRequest) =
        api.getPromotions(mRequest)

    fun getPromotionsByQty(mRequest: PromotionRequest) =
        api.getPromotionsByQty(mRequest)

    fun getPromotionsByPrice(mRequest: PromotionRequest) =
        api.getPromotionsByPrice(mRequest)

     fun getProductBarCode(mRequest: BasicApiRequest) =
        api.getProductBarCode(mRequest)

    fun syncAllApis(mRequest: BasicApiRequest) =
        api.syncAllApis(mRequest)

    fun createUpdatePosInvoice(mRequest: CreatePOSInvoiceRequest) =
        api.createUpdatePosInvoice(mRequest)

    fun getPrintTemplate(mRequest: GetPrintTemplateRequest) =
        api.getPrintTemplate(mRequest)

    fun getPosInvoiceForEdit(mRequest: GetPosInvoiceForEditRequest) =
        api.getPosInvoiceForEdit(mRequest)

    fun getPosPaymentSummary(mRequest: GetPOSPaymentSummaryRequest) =
        api.getPosPaymentSummary(mRequest)

    fun createOrUpdatePosSettlement(mRequest: CreateEditPOSSettlementRequest) =
        api.createOrUpdatePosSettlementOutput(mRequest)

}