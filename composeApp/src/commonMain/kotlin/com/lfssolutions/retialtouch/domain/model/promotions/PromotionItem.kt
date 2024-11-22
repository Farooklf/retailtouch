package com.lfssolutions.retialtouch.domain.model.promotions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PromotionItem(
    @SerialName("promotionType") val promotionType: Int? = null,
    @SerialName("promotionValueType") val promotionValueType: Int? = null,
    @SerialName("promotionTypeName") val promotionTypeName: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("endDate") val endDate: String? = null,
    @SerialName("qty") val qty: Double? = null,
    @SerialName("isActive") val isActive: Boolean? = null,
    @SerialName("mustBuy") val mustBuy: Boolean? = null,
    @SerialName("memberGroups") val memberGroups: Boolean? = null,
    @SerialName("group") val group: Boolean? = null,
    @SerialName("locations") val locations: String? = null,
    @SerialName("isHourlyPromotion") val isHourlyPromotion: Boolean? = null,
    @SerialName("startHour1") val startHour1: Int? = null,
    @SerialName("startMinute1") val startMinute1: Int? = null,
    @SerialName("endHour1") val endHour1: Int? = null,
    @SerialName("endMinute1") val endMinute1: Int? = null,
    @SerialName("startHour2") val startHour2: Int? = null,
    @SerialName("startMinute2") val startMinute2: Int? = null,
    @SerialName("endHour2") val endHour2: Int? = null,
    @SerialName("endMinute2") val endMinute2: Int? = null,
    @SerialName("inventoryCode") val inventoryCode: String? = null,
    @SerialName("discountPercentage") val discountPercentage: Double? = null,
    @SerialName("productPromotionPrice") val productPromotionPrice: Double? = null,
    @SerialName("memberGroupAttribute") val memberGroupAttribute : String?=null,
    @SerialName("priceBreakPromotionAttribute") val priceBreakPromotionAttribute: String? = null,
    @SerialName("id") val id: Long = 0
) {
    // Function to check if the promotion is by quantity
    fun isPromotionByQty(): Boolean =
        promotionTypeName?.equals("PromotionByQty", ignoreCase = true) == true

    // Function to check if the promotion is by price
    fun isPromotionByPrice(): Boolean =
        promotionTypeName?.equals("PromotionByPrice", ignoreCase = true) == true
}

@Serializable
data class PromotionAttribute(
    @SerialName("id") val id: Int? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("qty") val qty: Int? = null
)

@Serializable
data class  PriceBreakPromotionAttribute (
    @SerialName("price") val price: Double? = null,
    @SerialName("qty") val qty: Double? = null
)

