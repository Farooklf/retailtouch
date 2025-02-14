package com.hashmato.retailtouch.domain.model.promotions


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Promotion(
    @SerialName("id") val id: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("inventoryCode") val inventoryCode: String = "",
    @SerialName("promotionType") val promotionType: Int = 0,
    @SerialName("promotionValueType") val promotionValueType: Int = 0,
    @SerialName("promotionTypeName") val promotionTypeName: String? = "",
    @SerialName("qty") val qty: Double = 0.0,
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("startDate") val startDate: String = "",
    @SerialName("endDate") val endDate: String = "",
    @SerialName("startHour1") val startHour1: Int = 0,
    @SerialName("startHour2") val startHour2: Int = 0,
    @SerialName("endHour1") val endHour1: Int = 0,
    @SerialName("endHour2") val endHour2: Int = 0,
    @SerialName("startMinute1") val startMinute1: Int = 0,
    @SerialName("startMinute2") val startMinute2: Int =0,
    @SerialName("endMinute1") val endMinute1: Int = 0,
    @SerialName("endMinute2") val endMinute2: Int = 0,
    @SerialName("priceBreakPromotionAttribute") val priceBreakPromotionAttribute: List<PriceBreakPromotionAttribute>? = emptyList()
)

