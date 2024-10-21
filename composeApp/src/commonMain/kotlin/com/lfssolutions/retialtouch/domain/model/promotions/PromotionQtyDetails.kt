package com.lfssolutions.retialtouch.domain.model.promotions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionQtyDetails(
    @SerialName("id") val id: Long? = null,
    @SerialName("promotionId") val promotionId: Int?=null,
    @SerialName("productId") val productId: Int?=null,
    @SerialName("price") val price: Double?=null,
    @SerialName("inventoryCode") val inventoryCode: String?=null,
    @SerialName("tenantId") val tenantId: Int,
    @SerialName("productName") val productName: String? = null,
    @SerialName("barcode") val barcode: String? = null,
    @SerialName("isDeleted") val isDeleted: Boolean,
    @SerialName("deleterUserId") val deleterUserId: Int? = null,
    @SerialName("deletionTime") val deletionTime: String? = null,
    @SerialName("lastModificationTime") val lastModificationTime: String? = null,
    @SerialName("lastModifierUserId") val lastModifierUserId: Int? = null,
    @SerialName("creationTime") val creationTime: String?=null,
    @SerialName("creatorUserId") val creatorUserId: Int? = null
)
