package com.lfssolutions.retialtouch.domain.model.payout

import kotlinx.serialization.Serializable

@Serializable
data class CreateExpensesRequest(
  val payOutIn: PayOutIn?=null
)
