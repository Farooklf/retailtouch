package com.lfssolutions.retialtouch.domain

import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.location.LocationResponse
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuCategoryResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuProductResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuRequest
import com.lfssolutions.retialtouch.dataBase.DatabaseRepository
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionResponse
import com.lfssolutions.retialtouch.utils.DateTime.getCurrentDateAndTimeInEpochMilliSeconds
import com.lfssolutions.retialtouch.utils.DateTime.getHoursDifferenceFromEpochMillSeconds
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.append
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val TOKEN_EXPIRY_THRESHOLD = 6

class RemoteService(
    private val httpClient: HttpClient,
    private val localRepository: LocalRepository,
    private val databaseRepository: DatabaseRepository,
){

    private fun getBaseurl() = localRepository.getBaseURL()

    private fun getBearerToken(): String {
        return if (isTokenExpired())
            "Bearer ${refreshToken()}"
        else
            "Bearer ${localRepository.getToken()}"
    }

    private fun isTokenExpired(): Boolean {
        val tokenTime = localRepository.getTokenTime().toLong()
        val currentTime = getCurrentDateAndTimeInEpochMilliSeconds()
        val hoursPassed = getHoursDifferenceFromEpochMillSeconds(tokenTime, currentTime)
        return hoursPassed > TOKEN_EXPIRY_THRESHOLD
    }


    private fun refreshToken(): String {
        return runBlocking {
            try {
                val loginResponse = withContext(Dispatchers.IO) {
                    hitLoginAPI(getLoginDetails())
                }
                localRepository.setToken(loginResponse.result?:"")
                loginResponse.result?:""
            } catch (e: Exception) {
                e.printStackTrace()
                "" // Handle error case
            }
        }
    }

    private suspend fun getLoginDetails(): LoginRequest {
        var loginRequest = LoginRequest()
        databaseRepository.selectUserByUserId(localRepository.getUserId().toLong())
            .collect { authDao ->
                loginRequest = LoginRequest(
                    usernameOrEmailAddress = authDao.userName,
                    tenancyName = authDao.tenantName,
                    password = authDao.password,
                )
            }
        return loginRequest
    }

    suspend fun hitLoginAPI(loginRequest: LoginRequest): LoginResponse {
        return httpClient.post(getBaseurl() + ApiRoutes.LOGIN_API) {
            setBody(loginRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
            }
        }.body<LoginResponse>()
    }

    suspend fun getLocation(locationRequest: BasicApiRequest): LocationResponse =
        httpClient.post(getBaseurl() + ApiRoutes.LOCATION_FOR_USER_API) {
            setBody(locationRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<LocationResponse>()

    suspend fun getTerminal(locationRequest: BasicApiRequest): LocationResponse =
        httpClient.post(getBaseurl() + ApiRoutes.TERMINAL_API) {
            setBody(locationRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<LocationResponse>()

    suspend fun getEmployees(locationRequest: BasicApiRequest): EmployeesResponse =
        httpClient.post(getBaseurl() + ApiRoutes.EMPLOYEES_API) {
            setBody(locationRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<EmployeesResponse>()


    suspend fun getTEmployeeRoles(locationRequest: BasicApiRequest): EmployeesResponse =
        httpClient.post(getBaseurl() + ApiRoutes.EMPLOYEES_ROLE_API) {
            setBody(locationRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<EmployeesResponse>()


    suspend fun getMenuCategories(menuRequest:MenuRequest): MenuCategoryResponse =
        httpClient.post(getBaseurl() + ApiRoutes.GET_MENU_CATEGORIES_API) {
            setBody(menuRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<MenuCategoryResponse>()


    suspend fun getMenuProducts(menuRequest: MenuRequest): MenuProductResponse =
        httpClient.post(getBaseurl() + ApiRoutes.GET_MENU_PRODUCTS_API) {
            setBody(menuRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<MenuProductResponse>()


    suspend fun getNextPOSSaleInvoice(basicRequest: BasicApiRequest): NextPOSSaleInvoiceNoResponse =
        httpClient.post(getBaseurl() + ApiRoutes.NEXT_POS_SALE_INVOICE_API) {
            setBody(basicRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<NextPOSSaleInvoiceNoResponse>()

    suspend fun getPOSInvoice(basicRequest: POSInvoiceRequest): POSInvoiceResponse =
        httpClient.post(getBaseurl() + ApiRoutes.POS_INVOICE_API) {
            setBody(basicRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<POSInvoiceResponse>()

    suspend fun getProductsWithTax(basicRequest: BasicApiRequest): ProductWithTaxByLocationResponse =
        httpClient.post(getBaseurl() + ApiRoutes.PRODUCT_WITH_TAX_API) {
            setBody(basicRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<ProductWithTaxByLocationResponse>()

    suspend fun getProductLocation(basicRequest: BasicApiRequest): ProductLocationResponse =
        httpClient.post(getBaseurl() + ApiRoutes.GET_PRODUCT_LOCATION_API) {
            setBody(basicRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<ProductLocationResponse>()


    suspend fun getMembers(basicRequest: BasicApiRequest): MemberResponse =
        httpClient.post(getBaseurl() + ApiRoutes.GET_MEMBER_API) {
            setBody(basicRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<MemberResponse>()

    suspend fun getMemberGroup(): MemberGroupResponse =
        httpClient.post(getBaseurl() + ApiRoutes.GET_MEMBER_GROUP_API) {
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<MemberGroupResponse>()

    suspend fun getPaymentTypes(menuRequest: BasicApiRequest): PaymentTypeResponse =
        httpClient.post(getBaseurl() + ApiRoutes.GET_PAYMENT_TYPE_API) {
            setBody(menuRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<PaymentTypeResponse>()


    suspend fun getPromotions(promotionRequest: PromotionRequest): PromotionResponse =
        httpClient.post(getBaseurl() + ApiRoutes.GET_PROMOTIONS_API) {
            setBody(promotionRequest)
            headers {
                append(ContentType, io.ktor.http.ContentType.Application.Json)
                append("Authorization", getBearerToken())
            }
        }.body<PromotionResponse>()


}