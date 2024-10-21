package com.lfssolutions.retialtouch.domain.model.promotions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionPriceDetails(
    @SerialName("id") val id: Int? = null,
    @SerialName("promotionId") val promotionId: Int,
    @SerialName("productId") val productId: Int,
    @SerialName("price") val price: Double,
    @SerialName("promotionPrice") val promotionPrice: Double,
    @SerialName("promotionPerc") val promotionPerc: Double,
    @SerialName("inventoryCode") val inventoryCode: String,
    @SerialName("departmentId") val departmentId: Int? = null,
    @SerialName("departmentName") val departmentName: String? = null,
    @SerialName("categoryId") val categoryId: Int? = null,
    @SerialName("categoryName") val categoryName: String? = null,
    @SerialName("tenantId") val tenantId: Int,
    @SerialName("productName") val productName: String? = null,
    @SerialName("sno") val sno: Int,
    @SerialName("barcode") val barcode: String? = null,
    @SerialName("isDeleted") val isDeleted: Boolean,
    @SerialName("deleterUserId") val deleterUserId: Int? = null,
    @SerialName("deletionTime") val deletionTime: String? = null,
    @SerialName("lastModificationTime") val lastModificationTime: String? = null,
    @SerialName("lastModifierUserId") val lastModifierUserId: Int? = null,
    @SerialName("creationTime") val creationTime: String,
    @SerialName("creatorUserId") val creatorUserId: Int? = null
)
