package com.lfssolutions.retialtouch.domain.model.products


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosInvoiceDetail(
    @SerialName("averageCost")
    val averageCost: Double = 0.0,
    @SerialName("inventoryCode")
    val inventoryCode: String = "",
    @SerialName("inventoryName")
    val inventoryName: String = "",
    @SerialName("itemDiscount")
    val itemDiscount: Double = 0.0,
    @SerialName("itemDiscountPerc")
    val itemDiscountPerc: Double = 0.0,
    @SerialName("netCost")
    val netCost: Double = 0.0,
    @SerialName("netDiscount")
    val netDiscount: Double = 0.0,
    @SerialName("netTotal")
    val netTotal: Double = 0.0,
    @SerialName("posInvoiceId")
    val posInvoiceId: Int = 0,
    @SerialName("price")
    val price: Double = 0.0,
    @SerialName("productId")
    val productId: Long = 0,
    @SerialName("promotionId")
    val promotionId: Int? = 0,
    @SerialName("qty")
    val qty: Double = 1.0,
    @SerialName("roundingAmount")
    val roundingAmount: Double = 0.0,
    @SerialName("subTotal")
    val subTotal: Double = 0.0,
    @SerialName("tax")
    val tax: Double = 0.0,
    @SerialName("taxPercentage")
    val taxPercentage: Double = 0.0,
    @SerialName("total")
    val total: Double = 0.0,
    @SerialName("totalAmount")
    val totalAmount: Double = 0.0,
    @SerialName("totalValue")
    val totalValue: Double = 0.0
)