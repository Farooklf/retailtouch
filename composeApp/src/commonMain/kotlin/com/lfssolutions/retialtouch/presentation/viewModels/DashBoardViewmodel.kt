package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.dashBoard.DashBoardUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


class DashBoardViewmodel() : BaseViewModel(), KoinComponent {

    // Set up UI state using product
    private val _dashBoardUIState= MutableStateFlow(DashBoardUIState())
    val dashBoardUIState: StateFlow<DashBoardUIState> = _dashBoardUIState.asStateFlow()



    // Called when employee logs in
    fun onEmployeeLoggedIn() {
        viewModelScope.launch {
            _dashBoardUIState.update {
                it.copy(hasEmployeeLoggedIn = true, isShowEmployeeScreen = false, isDashBoardBlur = false)
            }
        }
    }

    // Reset the employee login state (called when app restarts)
    fun resetLoginState() {
        viewModelScope.launch {
            _dashBoardUIState.update {
                it.copy(isShowEmployeeScreen = true, isDashBoardBlur = true, hasEmployeeLoggedIn = false)
            }
        }
    }


    fun updateEmployeeScreen(isDashBlur: Boolean, isEmployeeScreen:Boolean) {
        viewModelScope.launch {
            _dashBoardUIState.update { it.copy(isShowEmployeeScreen = isEmployeeScreen, isDashBoardBlur = isDashBlur) }
        }
    }

}