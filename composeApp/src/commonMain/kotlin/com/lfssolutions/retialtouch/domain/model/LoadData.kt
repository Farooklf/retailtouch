package com.lfssolutions.retialtouch.domain.model

import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.members.Member
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails

data class LoadData(
    val members: List<Member> = emptyList(),
    val promotionsDetails: List<PromotionDetails> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val location : Location? = Location(),
    val holdSale : List<CRSaleOnHold> = emptyList()
)
