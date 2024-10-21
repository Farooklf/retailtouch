package com.lfssolutions.retialtouch.domain.model.productLocations


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductLocationItem(
    @SerialName("actualQty")
    val actualQty: Double? = 0.0,
    @SerialName("averageCost")
    val averageCost: Double? = 0.0,
    @SerialName("brandId")
    val brandId: String? = null,
    @SerialName("categoryId")
    val categoryId: Int? = 0,
    @SerialName("creationTime")
    val creationTime: String? = "",
    @SerialName("creatorUserId")
    val creatorUserId: String? = null,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("departmentId")
    val departmentId: Int? = 0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("inTransit")
    val inTransit: Double? = 0.0,
    @SerialName("inventoryCode")
    val inventoryCode: String? = "",
    @SerialName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerialName("isFranchise")
    val isFranchise: Boolean? = false,
    @SerialName("lastModificationTime")
    val lastModificationTime: String? = "",
    @SerialName("lastModifierUserId")
    val lastModifierUserId: String? = null,
    @SerialName("lastPurchaseCost")
    val lastPurchaseCost: Double? = 0.0,
    @SerialName("locationCode")
    val locationCode: String? = "",
    @SerialName("locationId")
    val locationId: Int? = 0,
    @SerialName("maximumQty")
    val maximumQty: Double? = 0.0,
    @SerialName("minimumQty")
    val minimumQty: Double? = 0.0,
    @SerialName("mrp")
    val mrp: Double? = 0.0,
    @SerialName("name")
    val name: String? = null,
    @SerialName("orderedQuantity")
    val orderedQuantity: Double? = 0.0,
    @SerialName("price")
    val price: Double? = 0.0,
    @SerialName("productCreationTime")
    val productCreationTime: String? = "",
    @SerialName("productId")
    val productId: Int = 0,
    @SerialName("productLastModificationTime")
    val productLastModificationTime: String? = "",
    @SerialName("qtyOnHand")
    val qtyOnHand: Double? = 0.0,
    @SerialName("reOrderLevel")
    val reOrderLevel: Double? = 0.0,
    @SerialName("reserverdQty")
    val reserverdQty: Double? = 0.0,
    @SerialName("specialPrice")
    val specialPrice: Double? = 0.0,
    @SerialName("totalCost")
    val totalCost: Double? = 0.0,
    @SerialName("unitCost")
    val unitCost: Double? = 0.0,
    @SerialName("vendorEmail")
    val vendorEmail: String? = null,
    @SerialName("vendorId")
    val vendorId: String? = null
)