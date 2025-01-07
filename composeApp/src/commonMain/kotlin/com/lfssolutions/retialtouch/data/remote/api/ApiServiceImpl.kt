package com.lfssolutions.retialtouch.data.remote.api


import com.lfssolutions.retialtouch.domain.SqlRepository
import com.lfssolutions.retialtouch.domain.ApiService
import com.lfssolutions.retialtouch.domain.ApiUtils.handleApiResponse
import com.lfssolutions.retialtouch.domain.ApiUtils.handleException
import com.lfssolutions.retialtouch.domain.ApiUtils.performApiRequestWithBaseUrl
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.ApiRoutes
import com.lfssolutions.retialtouch.domain.RequestState
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
import com.lfssolutions.retialtouch.domain.model.printer.GetPrintTemplateRequest
import com.lfssolutions.retialtouch.domain.model.printer.GetPrintTemplateResult
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.GetPosInvoiceResult
import com.lfssolutions.retialtouch.domain.model.posInvoices.GetPosInvoiceForEditRequest
import com.lfssolutions.retialtouch.domain.model.posInvoices.GetPosInvoiceForEditResult
import com.lfssolutions.retialtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.products.CreatePOSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.products.PosInvoiceResponse
import com.lfssolutions.retialtouch.domain.model.products.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionResult
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByPriceResult
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByQtyResult
import com.lfssolutions.retialtouch.domain.model.settlement.CreateEditPOSSettlementRequest
import com.lfssolutions.retialtouch.domain.model.settlement.CreateEditPOSSettlementResult
import com.lfssolutions.retialtouch.domain.model.settlement.GetPOSPaymentSummaryRequest
import com.lfssolutions.retialtouch.domain.model.settlement.PosPaymentSummaryResult
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllResponse
import com.lfssolutions.retialtouch.domain.model.terminal.TerminalResponse
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getCurrentDateAndTimeInEpochMilliSeconds
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getHoursDifferenceFromEpochMillSeconds
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN_EXPIRY_THRESHOLD
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.append
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


 class ApiServiceImpl(
    private val httpClient: HttpClient,
    private val preferences: PreferencesRepository,
    private val sqlRepository: SqlRepository
) : ApiService{

    private suspend fun getBaseUrl() : String {
        return preferences.getBaseURL().first() // Collects the first emitted value from Flow
    }


     private suspend fun isTokenExpired(): Boolean {
        val tokenTime = preferences.getTokenTime().first()
        val currentTime = getCurrentDateAndTimeInEpochMilliSeconds()
        val hoursPassed = getHoursDifferenceFromEpochMillSeconds(tokenTime, currentTime)
        return hoursPassed > TOKEN_EXPIRY_THRESHOLD
    }


     private suspend fun refreshToken(): String {
         return runBlocking {
             try {
                 var result=""
                 withContext(Dispatchers.IO) {
                    hitLoginAPI(getLoginDetails()).collect{response->
                         when(response){
                             is RequestState.Success -> {
                                 val token=response.data.result
                                 preferences.setToken(token?:"")
                                 result=token?:""
                             }
                             else ->{

                             }
                         }
                     }
                     result
                 }
             } catch (e: Exception) {
                 e.printStackTrace()
                 ""
             }
         }
     }

     private suspend fun getLoginDetails(): LoginRequest {
         val authDao=sqlRepository.selectUserByUserId(preferences.getUserId().first()).first()
         val loginRequest = LoginRequest(
             usernameOrEmailAddress = authDao.userName,
             tenancyName = authDao.tenantName,
             password = authDao.password,
         )
         return loginRequest

     }


     override fun hitLoginAPI(loginRequest: LoginRequest): Flow<RequestState<LoginResponse>> =
        flow{
            emit(RequestState.Loading)  // Indicate loading state
            try {
                val response=httpClient.post(getBaseUrl() + ApiRoutes.LOGIN_API){
                    setBody(loginRequest)
                    headers {
                        append(ContentType, io.ktor.http.ContentType.Application.Json)
                    }
                }
                emit(handleApiResponse<LoginResponse>(response))
            } catch (e: Exception) {
                emit(handleException(e))
            }
    }

    override fun getLocation(mBasicApiRequest: BasicApiRequest
    ): Flow<RequestState<LocationResponse>>{
        return performApiRequestWithBaseUrl(
            httpClient = httpClient,
            apiUrl = ApiRoutes.LOCATION_FOR_USER_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<LocationResponse>(response)
        }
    }

    override  fun getTerminal(mBasicApiRequest: BasicApiRequest): Flow<RequestState<TerminalResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient = httpClient,
            apiUrl = ApiRoutes.TERMINAL_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<TerminalResponse>(response)
        }
    }

    override  fun getEmployees(mBasicApiRequest: BasicApiRequest): Flow<RequestState<EmployeesResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient = httpClient,
            apiUrl = ApiRoutes.EMPLOYEES_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<EmployeesResponse>(response)
        }
    }

    override fun getTEmployeeRoles(mBasicApiRequest: BasicApiRequest): Flow<RequestState<EmployeesResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.EMPLOYEES_ROLE_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<EmployeesResponse>(response)
        }
    }

     override fun getEmployeeRights(mBasicApiRequest: BasicApiRequest): Flow<RequestState<EmployeesRights>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.EMPLOYEES_RIGHTS_API,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<EmployeesRights>(response)
         }
     }

     override fun getMenuCategories(mBasicApiRequest: BasicApiRequest): Flow<RequestState<CategoryResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_MENU_CATEGORIES_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<CategoryResponse>(response)
        }
    }

    override fun getMenuProducts(mBasicApiRequest: BasicApiRequest): Flow<RequestState<MenuResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_MENU_PRODUCTS_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<MenuResponse>(response)
        }
    }

    override fun getNextPOSSaleInvoice(mBasicApiRequest: BasicApiRequest): Flow<RequestState<NextPOSSaleInvoiceNoResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.NEXT_POS_SALE_INVOICE_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<NextPOSSaleInvoiceNoResponse>(response)
        }
    }

    override fun getLatestSales(mBasicApiRequest: POSInvoiceRequest): Flow<RequestState<GetPosInvoiceResult>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.POS_INVOICE_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<GetPosInvoiceResult>(response)
        }
    }

    override fun getProductsWithTax(mBasicApiRequest: BasicApiRequest): Flow<RequestState<ProductWithTaxByLocationResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.PRODUCT_WITH_TAX_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<ProductWithTaxByLocationResponse>(response)
        }
    }

    override fun getProductLocation(mBasicApiRequest: BasicApiRequest): Flow<RequestState<ProductLocationResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_PRODUCT_LOCATION_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<ProductLocationResponse>(response)
        }
    }

    override fun getMembers(mBasicApiRequest: BasicApiRequest): Flow<RequestState<MemberResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_MEMBER_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<MemberResponse>(response)
        }
    }

    override fun getMemberGroup(mBasicApiRequest: BasicApiRequest): Flow<RequestState<MemberGroupResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_MEMBER_GROUP_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<MemberGroupResponse>(response)
        }
    }

    override fun getPaymentTypes(mBasicApiRequest: BasicApiRequest): Flow<RequestState<PaymentTypeResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_PAYMENT_TYPE_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<PaymentTypeResponse>(response)
        }
    }

    override fun getPromotions(mBasicApiRequest: PromotionRequest): Flow<RequestState<GetPromotionResult>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_PROMOTIONS_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<GetPromotionResult>(response)
        }
    }

     override fun getPromotionsByQty(mBasicApiRequest: PromotionRequest): Flow<RequestState<GetPromotionsByQtyResult>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.GET_FIXED_QTY_PROMOTIONS_API,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<GetPromotionsByQtyResult>(response)
         }
     }

     override fun getPromotionsByPrice(mBasicApiRequest: PromotionRequest): Flow<RequestState<GetPromotionsByPriceResult>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.GET_FIXED_PRICE_PROMOTIONS_API,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<GetPromotionsByPriceResult>(response)
         }
     }

     override fun getProductBarCode(mBasicApiRequest: BasicApiRequest): Flow<RequestState<ProductBarCodeResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_PRODUCT_BARCODE_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<ProductBarCodeResponse>(response)
        }
    }

    override fun syncAllApis(mBasicApiRequest: BasicApiRequest): Flow<RequestState<SyncAllResponse>> {
        return performApiRequestWithBaseUrl(
            httpClient=httpClient,
            apiUrl = ApiRoutes.GET_ALL_SYNC_API,
            requestBody = mBasicApiRequest
        ) { response ->
            handleApiResponse<SyncAllResponse>(response)
        }
    }

     override fun createUpdatePosInvoice(mBasicApiRequest: CreatePOSInvoiceRequest): Flow<RequestState<PosInvoiceResponse>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.CREATE_UPDATE_POS_INVOICE,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<PosInvoiceResponse>(response)
         }
     }

     override fun getPrintTemplate(mBasicApiRequest: GetPrintTemplateRequest): Flow<RequestState<GetPrintTemplateResult>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.GET_RECEIPT_TEMPLATE_LOCATION,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<GetPrintTemplateResult>(response)
         }
     }

     override fun getPosInvoiceForEdit(mBasicApiRequest: GetPosInvoiceForEditRequest): Flow<RequestState<GetPosInvoiceForEditResult>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.POS_INVOICE_EDIT_API,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<GetPosInvoiceForEditResult>(response)
         }
     }

     override fun getPosPaymentSummary(mBasicApiRequest: GetPOSPaymentSummaryRequest): Flow<RequestState<PosPaymentSummaryResult>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.GET_POS_PAYMENT_SUMMARY,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<PosPaymentSummaryResult>(response)
         }
     }

     override fun createOrUpdatePosSettlementOutput(mBasicApiRequest: CreateEditPOSSettlementRequest): Flow<RequestState<CreateEditPOSSettlementResult>> {
         return performApiRequestWithBaseUrl(
             httpClient=httpClient,
             apiUrl = ApiRoutes.CREATE_UPDATE_POS_SETTLEMENT,
             requestBody = mBasicApiRequest
         ) { response ->
             handleApiResponse<CreateEditPOSSettlementResult>(response)
         }
     }
 }




