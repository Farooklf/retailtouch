package com.lfssolutions.retialtouch.domain.model.products


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosInvoice(
    @SerialName("deliveryDateTime")
    val deliveryDateTime: String? = null,
    @SerialName("employeeId")
    val employeeId: Int? = 0,
    @SerialName("id")
    val id: Long = 0,
    @SerialName("invoiceDate")
    val invoiceDate: String? = null,
    @SerialName("creationTime")
    val creationTime: String? = null,
    @SerialName("invoiceItemDiscount")
    val invoiceItemDiscount: Double = 0.0,
    @SerialName("invoiceNetCost")
    val invoiceNetCost: Double = 0.0,
    @SerialName("invoiceNetDiscount")
    val invoiceNetDiscount: Double = 0.0,
    @SerialName("invoiceNetDiscountPerc")
    val invoiceNetDiscountPerc: Double? = 0.0,
    @SerialName("invoiceNetTotal")
    val invoiceNetTotal: Double = 0.0,
    @SerialName("invoiceNo")
    val invoiceNo: String? = null,
    @SerialName("invoiceRoundingAmount")
    val invoiceRoundingAmount: Double = 0.0,
    @SerialName("invoiceSubTotal")
    val invoiceSubTotal: Double = 0.0,
    @SerialName("invoiceTax")
    val invoiceTax: Double = 0.0,
    @SerialName("invoiceTotal")
    val invoiceTotal: Double? = 0.0,
    @SerialName("invoiceTotalAmount")
    val invoiceTotalAmount: Double = 0.0,
    @SerialName("invoiceTotalValue")
    val invoiceTotalValue: Double = 0.0,
    @SerialName("isCancelled")
    val isCancelled: Boolean? = false,
    @SerialName("isDelivered")
    val isDelivered: Boolean? = false,
    @SerialName("isRental")
    val isRental: Boolean? = false,
    @SerialName("isRentalCollected")
    val isRentalCollected: Boolean? = false,
    @SerialName("isRetailWebRequest")
    val isRetailWebRequest: Boolean? = false,
    @SerialName("locationCode")
    val locationCode: String? = null,
    @SerialName("locationId")
    val locationId: Long? = 0,
    @SerialName("memberId")
    val memberId: Int? = 0,
    @SerialName("paid")
    val paid: Double = 0.0,
    @SerialName("posInvoiceDetails")
    val posInvoiceDetails: List<PosInvoiceDetail> = emptyList(),
    @SerialName("posPayments")
    val posPayments: List<PosPayment> = emptyList(),
    @SerialName("remarks")
    val remarks: String? = null,
    @SerialName("selfCollection")
    val selfCollection: Boolean? = false,
    @SerialName("status")
    val status: Int? = 0,
    @SerialName("tenantId")
    val tenantId: Int? = 0,
    @SerialName("terminalId")
    val terminalId: Long? = 0,
    @SerialName("terminalName")
    val terminalName: String? = null,
    @SerialName("type")
    val type: Int? = 0,

    val pendingInvoices :Long = 0,
    val qty :Int = 0,
    val customerName :String = "",
    val address1 :String = "",
    val address2 :String = "",
    val invoicePromotionDiscount:Double=0.0,
    val invoiceOutstandingAmt:Double=0.0
)