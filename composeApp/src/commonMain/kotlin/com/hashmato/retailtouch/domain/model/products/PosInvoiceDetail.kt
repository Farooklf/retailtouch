package com.hashmato.retailtouch.domain.model.products


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
    val itemDiscount: Double ,
    @SerialName("itemDiscountPerc")
    val itemDiscountPerc: Double,
    @SerialName("netCost")
    val netCost: Double,
    @SerialName("netDiscount")
    val netDiscount: Double,
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
    val qty: Double,
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