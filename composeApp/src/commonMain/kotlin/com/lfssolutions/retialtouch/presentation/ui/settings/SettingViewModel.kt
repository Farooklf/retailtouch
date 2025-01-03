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
            val rtUser= dataBaseRepository.getRTLoginUser().first()

            _settingUiState.update { state->
                state.copy(
                    serverUrl = getCurrentServer(),
                    tenant = rtUser.tenantName,
                    user = rtUser.userName,
                    networkConfig = getNetworkConfig(),
                    gridViewOption = getGridViewOptions(),
                    mergeCartItems = getMergeCartItems(),
                    roundOffOption = getRoundOffOption(),
                    paymentConfirmPopup = getPaymentConfirmPopup(),
                    fastPaymode = getFastPaymentMode()
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

    fun updateGridViewOptionsDialogVisibility(value: Boolean) {
        viewModelScope.launch {
            _settingUiState. update { state -> state.copy(showGridViewOptionsDialog = value) }
        }
    }

    fun updateGridViewOption(updatedValue: Int) {
        viewModelScope.launch {
            setGridViewOptions(updatedValue)
            _settingUiState. update { state -> state.copy(gridViewOption = updatedValue,showGridViewOptionsDialog = false) }
        }
    }

    fun updateMergeCartItems() {
        viewModelScope.launch {
            _settingUiState. update { state ->
                setMergeCartItems(!state.mergeCartItems)
                state.copy(mergeCartItems = !state.mergeCartItems)
            }
        }
    }

    fun updatePaymentConfirmPopup() {
        viewModelScope.launch {
            _settingUiState. update { state ->
                setPaymentConfirmPopup(!state.paymentConfirmPopup)
                state.copy(paymentConfirmPopup = !state.paymentConfirmPopup)
            }
        }
    }

    fun updateFastPayMode() {
        viewModelScope.launch {
            _settingUiState. update { state ->
                setFastPaymentMode(!state.fastPaymode)
                state.copy(fastPaymode = !state.fastPaymode)
            }
        }
    }

    fun updateRoundOffOptionsDialogVisibility(value: Boolean){
        viewModelScope.launch {
            _settingUiState. update { state -> state.copy(showRoundOffDialog = value) }
        }
    }

    fun updateRoundOffOption(value: Int) {
        viewModelScope.launch {
            _settingUiState. update { state ->
                setRoundOffOption(value)
                state.copy(roundOffOption = value,showRoundOffDialog = false)
            }
        }
    }
}