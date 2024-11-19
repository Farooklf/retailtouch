package com.lfsolutions.paymentslibrary.paytm


data class Data(
    val AccessCode: String,
    val AccountId: String,
    val Currency: String,
    val Enable3DSecure: Boolean,
    val EnablePayNow: Boolean,
    val HideCard: Boolean,
    val Key: String,
    val Log: Boolean,
    val MerchantId: String,
    val PostUrl: String,
    val PublishKey: String,
    val SecretKey: String,
    val StatusUrl: String,
    val Type: String,
    val UniqueName: String,
    val Mid: String? = null,
    val Tid: String? = null,
    val MerchantNumber: String? = null,
    val Production: String? = null,
) {
    fun getPayTMDDetails(): PayTMDetails {
        return PayTMDetails(this.Mid, this.Tid, this.MerchantNumber, this.Production)

    }
}

data class PayTMDetails(
    val Mid: String? = null,
    val Tid: String? = null,
    val MerchantNumber: String? = null,
    val Production: String? = null,
)