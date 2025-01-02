package com.lfssolutions.retialtouch.presentation.ui.settings


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SettingViewModel : BaseViewModel(), KoinComponent {

    private val _settingUiState = MutableStateFlow(SettingUIState())
    val settingUiState: StateFlow<SettingUIState> = _settingUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _settingUiState.update { state->
                state.copy(
                    serverUrl = getCurrentServer(),
                    tenant = authUser.value?.tenantName?:"",
                    user = authUser.value?.userName?:"",
                    networkConfig = getNetworkConfig()
                )}
        }

        viewModelScope.launch {
            observeNetworkConfig().collect{updatedValue->
                _settingUiState.update { state -> state.copy(networkConfig = updatedValue,showNetworkConfigDialog = false) }
            }
        }
    }

    fun updateNetworkConfigDialogVisibility(value: Boolean) {
        _settingUiState.update { state -> state.copy(showNetworkConfigDialog = value) }
    }

    fun updateNetworkConfig(updatedValue: String) {
        viewModelScope.launch {
            _settingUiState.update { state -> state.copy(networkConfig = updatedValue,showNetworkConfigDialog = false) }
            setNetworkConfig(updatedValue)
        }
    }

}