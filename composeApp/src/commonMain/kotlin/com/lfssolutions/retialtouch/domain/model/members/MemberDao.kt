package com.lfssolutions.retialtouch.domain.model.members

import kotlinx.serialization.Serializable


@Serializable
data class MemberDao(
    val memberId: Long = 0L,
    val rowItem: MemberItem = MemberItem(),
)
