package com.lfssolutions.retialtouch.domain.model.productWithTax


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosInvoice(
    @SerialName("deliveryDateTime")
    val deliveryDateTime: String = "",
    @SerialName("employeeId")
    val employeeId: Int? = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("invoiceDate")
    val invoiceDate: String = "",
    @SerialName("invoiceItemDiscount")
    val invoiceItemDiscount: Double = 0.0,
    @SerialName("invoiceNetCost")
    val invoiceNetCost: Double = 0.0,
    @SerialName("invoiceNetDiscount")
    val invoiceNetDiscount: Double = 0.0,
    @SerialName("invoiceNetDiscountPerc")
    val invoiceNetDiscountPerc: Double = 0.0,
    @SerialName("invoiceNetTotal")
    val invoiceNetTotal: Double = 0.0,
    @SerialName("invoiceNo")
    val invoiceNo: String = "",
    @SerialName("invoiceRoundingAmount")
    val invoiceRoundingAmount: Double = 0.0,
    @SerialName("invoiceSubTotal")
    val invoiceSubTotal: Double = 0.0,
    @SerialName("invoiceTax")
    val invoiceTax: Double = 0.0,
    @SerialName("invoiceTotal")
    val invoiceTotal: Double = 0.0,
    @SerialName("invoiceTotalAmount")
    val invoiceTotalAmount: Double = 0.0,
    @SerialName("invoiceTotalValue")
    val invoiceTotalValue: Double = 0.0,
    @SerialName("isCancelled")
    val isCancelled: Boolean = false,
    @SerialName("isDelivered")
    val isDelivered: Boolean = false,
    @SerialName("isRental")
    val isRental: Boolean = false,
    @SerialName("isRentalCollected")
    val isRentalCollected: Boolean = false,
    @SerialName("isRetailWebRequest")
    val isRetailWebRequest: Boolean = false,
    @SerialName("locationCode")
    val locationCode: String = "",
    @SerialName("locationId")
    val locationId: Int = 0,
    @SerialName("memberId")
    val memberId: Int? = 0,
    @SerialName("paid")
    val paid: Double = 0.0,
    @SerialName("posInvoiceDetails")
    val posInvoiceDetails: List<PosInvoiceDetail> = listOf(),
    @SerialName("posPayments")
    val posPayments: List<PosPayment> = listOf(),
    @SerialName("remarks")
    val remarks: String = "",
    @SerialName("selfCollection")
    val selfCollection: Boolean = false,
    @SerialName("status")
    val status: Int = 0,
    @SerialName("tenantId")
    val tenantId: Int = 0,
    @SerialName("terminalId")
    val terminalId: Int = 0,
    @SerialName("terminalName")
    val terminalName: String = "",
    @SerialName("type")
    val type: Int = 0
)