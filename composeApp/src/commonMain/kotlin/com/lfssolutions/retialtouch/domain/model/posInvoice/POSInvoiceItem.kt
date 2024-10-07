package com.lfssolutions.retialtouch.domain.model.posInvoice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class POSInvoiceItem(
    @SerialName("addOn")
    val addOn: String? = null,
    @SerialName("apiThirdPartyResponse")
    val apiThirdPartyResponse: String? = null,
    @SerialName("balance")
    val balance: Double? = 0.0,
    @SerialName("createdBy")
    val createdBy: String? = null,
    @SerialName("createdDateTime")
    val createdDateTime: String? = "",
    @SerialName("creationTime")
    val creationTime: String? = "",
    @SerialName("deliveryDateTime")
    val deliveryDateTime: String? = null,
    @SerialName("earnedPoints")
    val earnedPoints: Double? = 0.0,
    @SerialName("employeeId")
    val employeeId: String? = null,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("invoiceDate")
    val invoiceDate: String? = "",
    @SerialName("invoiceItemDiscount")
    val invoiceItemDiscount: Double? = 0.0,
    @SerialName("invoiceNetCost")
    val invoiceNetCost: Double? = 0.0,
    @SerialName("invoiceNetDiscount")
    val invoiceNetDiscount: Double? = 0.0,
    @SerialName("invoiceNetDiscountPerc")
    val invoiceNetDiscountPerc: Double? = 0.0,
    @SerialName("invoiceNetTotal")
    val invoiceNetTotal: Double? = 0.0,
    @SerialName("invoiceNo")
    val invoiceNo: String? = "",
    @SerialName("invoiceRoundingAmount")
    val invoiceRoundingAmount: Double? = 0.0,
    @SerialName("invoiceSubTotal")
    val invoiceSubTotal: Double? = 0.0,
    @SerialName("invoiceTax")
    val invoiceTax: Double? = 0.0,
    @SerialName("invoiceTotal")
    val invoiceTotal: Double? = 0.0,
    @SerialName("invoiceTotalAmount")
    val invoiceTotalAmount: Double? = 0.0,
    @SerialName("invoiceTotalValue")
    val invoiceTotalValue: Double? = 0.0,
    @SerialName("isApiResponseSuccess")
    val isApiResponseSuccess: Boolean? = false,
    @SerialName("isCancelled")
    val isCancelled: Boolean? = false,
    @SerialName("isDelivered")
    val isDelivered: Boolean? = false,
    @SerialName("isNetsuite")
    val isNetsuite: Boolean? = false,
    @SerialName("isPosted")
    val isPosted: Boolean? = false,
    @SerialName("isRental")
    val isRental: Boolean? = false,
    @SerialName("isRentalCollected")
    val isRentalCollected: Boolean? = false,
    @SerialName("isSelfCollection")
    val isSelfCollection: Boolean? = false,
    @SerialName("locationCode")
    val locationCode: String? = null,
    @SerialName("locationId")
    val locationId: Int? = 0,
    @SerialName("locationName")
    val locationName: String? = null,
    @SerialName("memberCode")
    val memberCode: String? = null,
    @SerialName("memberId")
    val memberId: String? = null,
    @SerialName("memberName")
    val memberName: String? = null,
    @SerialName("modifiedBy")
    val modifiedBy: String? = null,
    @SerialName("modifiedDateTime")
    val modifiedDateTime: String? = null,
    @SerialName("paid")
    val paid: Double? = 0.0,
    @SerialName("paymentType")
    val paymentType: String? = "",
    @SerialName("paymentTypeId")
    val paymentTypeId: Int? = 0,
    @SerialName("posInvoiceDetails")
    val posInvoiceDetails: String? = null,
    @SerialName("posPayments")
    val posPayments: String? = null,
    @SerialName("remarks")
    val remarks: String? = null,
    @SerialName("shift")
    val shift: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("tenantId")
    val tenantId: Int? = 0,
    @SerialName("terminalId")
    val terminalId: String? = null,
    @SerialName("terminalName")
    val terminalName: String? = "",
    @SerialName("type")
    val type: String? = null,
    @SerialName("usedPoints")
    val usedPoints: Double? = 0.0
)