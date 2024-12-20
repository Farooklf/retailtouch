package com.lfssolutions.retialtouch.domain.model

import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails

data class LoadData(
    val members: List<MemberDao> = emptyList(),
    val promotionsDetails: List<PromotionDetails> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val location : Location? = Location(),
    val holdSale : List<CRSaleOnHold> = emptyList()
)
