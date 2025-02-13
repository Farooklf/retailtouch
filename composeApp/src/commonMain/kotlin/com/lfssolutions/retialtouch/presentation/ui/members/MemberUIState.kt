package com.lfssolutions.retialtouch.presentation.ui.members

import com.lfssolutions.retialtouch.domain.model.members.Member

data class MemberUIState(
    val searchQuery:String="",
    val members: List<Member>  = emptyList(),
    val selectedMembers: Set<Long> = emptySet() // Store selected product IDs
)
