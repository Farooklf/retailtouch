package com.lfssolutions.retialtouch.presentation.ui.members

import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.members.Member
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class MemberViewModel : BaseViewModel() , KoinComponent {

    private val _uiState = MutableStateFlow(MemberUIState())
    val uiState: StateFlow<MemberUIState> = _uiState.asStateFlow()
    private var originalMemberList: List<Member> = emptyList()

    init {
        getMembers()
    }

    private fun getMembers() {
        viewModelScope.launch {
            sqlRepository.getAllMembers()
                .collect { members ->
                    _uiState.update { currentState ->
                        originalMemberList = members
                        currentState.copy(members =members)
                    } }
        }
    }


    fun toggleMemberSelection(memberId: Long) {
        _uiState.update { state ->
            val newSelection = state.selectedMembers.toMutableSet()
            if (newSelection.contains(memberId)) {
                newSelection.remove(memberId)
            } else {
                newSelection.add(memberId)
            }
            state.copy(selectedMembers = newSelection)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedMembers = emptySet()) }
    }
}