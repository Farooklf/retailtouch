package com.lfssolutions.retialtouch.domain.model.memberGroup

import kotlinx.serialization.Serializable


@Serializable
data class MemberGroupDao(
    val memberGroupId: Long = 0L,
    val rowItem: MemberGroupItem = MemberGroupItem(),
)
