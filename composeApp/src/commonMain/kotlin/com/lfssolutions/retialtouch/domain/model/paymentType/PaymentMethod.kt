package com.lfssolutions.retialtouch.domain.model.paymentType


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethod(
    @SerialName("acceptChange")
    val acceptChange: Boolean? = false,
    @SerialName("accountCode")
    val accountCode: String? = null,
    @SerialName("creationTime")
    val creationTime: String? = "",
    @SerialName("creatorUserId")
    val creatorUserId: Int? = 0,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("hide")
    val enabled: Boolean? = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerialName("isForeignCurrency")
    val isForeignCurrency: Boolean? = false,
    @SerialName("lastModificationTime")
    val lastModificationTime: String? = "",
    @SerialName("lastModifierUserId")
    val lastModifierUserId: Int? = 0,
    @SerialName("name")
    val name: String? = "",
    @SerialName("posPaymentAttributes")
    val posPaymentAttributes: String? = "",
    @SerialName("rate")
    val rate: Double? = 0.0,
    @SerialName("sortOrder")
    val sortOrder: Int? = 0,
    @SerialName("tenantId")
    val tenantId: Int? = 0,

    val paymentProcessorName: String = "",
    val paymentProcessor: Int = 0,
    val amount: Double = 0.0,
    val isSelected: Boolean = false,
    val isShowPaidAmount: Boolean = false,
    val paidAmount: String = "$",
)