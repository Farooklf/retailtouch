package com.hashmato.retailtouch.domain.model

import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.members.Member
import com.hashmato.retailtouch.domain.model.products.CRSaleOnHold
import com.hashmato.retailtouch.domain.model.promotions.Promotion
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails

data class LoadData(
    val members: List<Member> = emptyList(),
    val promotionsDetails: List<PromotionDetails> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val location : Location? = Location(),
    val holdSale : List<CRSaleOnHold> = emptyList()
)
