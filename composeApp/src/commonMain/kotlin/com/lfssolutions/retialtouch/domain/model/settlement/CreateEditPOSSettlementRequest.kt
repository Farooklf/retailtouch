package com.lfssolutions.retialtouch.domain.model.settlement

import kotlinx.serialization.Serializable

@Serializable
data class CreateEditPOSSettlementRequest(
   val posSettlement : CreateEditPOSSettlement?=null
)
