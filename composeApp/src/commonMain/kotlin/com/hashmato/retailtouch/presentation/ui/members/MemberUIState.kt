package com.hashmato.retailtouch.presentation.ui.members

import com.hashmato.retailtouch.domain.model.members.Member

data class MemberUIState(
    val searchQuery:String="",
    val members: List<Member>  = emptyList(),
    val selectedMembers: Set<Long> = emptySet() // Store selected product IDs
)
