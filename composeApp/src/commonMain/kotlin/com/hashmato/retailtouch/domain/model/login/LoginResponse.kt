package com.hashmato.retailtouch.domain.model.login


import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("__abp")
    val abp: Boolean? = false,
    @SerialName("countryCode")
    val countryCode: String? = "",
    @SerialName("currencyCode")
    val currencyCode: String? = "",
    @SerialName("currencySymbol")
    val currencySymbol: String? = "",
    @SerialName("customer")
    val customer: Boolean? = false,
    @SerialName("customerId")
    val customerId: Int? = null,
    @SerialName("dashboard")
    val dashboard: Boolean? = false,
    @SerialName("defaultLocation")
    val defaultLocation: String? = "",
    @SerialName("defaultLocationId")
    val defaultLocationId: Int? = 0,
    @SerialName("employee")
    val employee: Boolean? = false,
    @SerialName("error")
    val error: ErrorResponse? = null,
    @SerialName("isEditPrice")
    val isEditPrice: Boolean? = false,
    @SerialName("isSuperVisor")
    val isSuperVisor: Boolean? = false,
    @SerialName("locationCode")
    val locationCode: String? = "",
    @SerialName("member")
    val member: Boolean? = false,
    @SerialName("posInvoiceRounded")
    val posInvoiceRounded: Double? = 0.0,
    @SerialName("result")
    val result: String? = "",
    @SerialName("retailTabPurchase")
    val retailTabPurchase: Boolean? = false,
    @SerialName("salesPersonId")
    val salesPersonId: Int? = null,
    @SerialName("salesTaxInclusive")
    val salesTaxInclusive: Boolean? = false,
    @SerialName("success")
    val success: Boolean? = false,
    @SerialName("targetUrl")
    val targetUrl: String? = null,
    @SerialName("tenantId")
    val tenantId: Int? = 0,
    @SerialName("timeZoneUtc")
    val timeZoneUtc: String? = "",
    @SerialName("unAuthorizedRequest")
    val unAuthorizedRequest: Boolean? = false,
    @SerialName("userId")
    val userId: Int = 0,
    @SerialName("userName")
    val userName: String = "",
    @SerialName("vendorId")
    val vendorId: Int? = null,
    @SerialName("wareHouseLocationId")
    val wareHouseLocationId: Int? = 0,
    @SerialName("wareHouseLocationName")
    val wareHouseLocationName: String? = ""
)