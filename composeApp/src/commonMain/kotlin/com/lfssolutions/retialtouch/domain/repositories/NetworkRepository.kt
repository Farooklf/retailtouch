package com.lfssolutions.retialtouch.domain.repositories

import com.lfssolutions.retialtouch.domain.ApiService
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
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


    fun getMenuCategories(mRequest: BasicApiRequest) = 
        api.getMenuCategories(mRequest)
    

     fun getMenuProducts(mRequest: BasicApiRequest) =  
        api.getMenuProducts(mRequest)
    

     fun getNextPOSSaleInvoice(mRequest: BasicApiRequest) =  
        api.getNextPOSSaleInvoice(mRequest)
   

     fun getPOSInvoice(mRequest: POSInvoiceRequest) =  
        api.getPOSInvoice(mRequest)
   

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

     fun getProductBarCode(mRequest: BasicApiRequest) =
        api.getProductBarCode(mRequest)

    fun syncAllApis(mRequest: BasicApiRequest) =
        api.syncAllApis(mRequest)

}