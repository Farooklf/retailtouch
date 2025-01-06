package com.lfssolutions.retialtouch.presentation.ui.settings


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ROLE_ERROR_TITLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
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
        viewModelScope.launch(Dispatchers.IO) {
            val posEmployees = getPosEmployees()
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
                    fastPaymode = getFastPaymentMode(),
                    posEmployees = posEmployees
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

    fun syncStaff(){
        viewModelScope.launch {
          //Employee API
            syncEmployees()
            syncEmployeeRole()
        }
    }


    private suspend fun syncEmployees(){
        try {
            println("Syncing Employees")
            //updateLoginSyncStatus("Syncing Employees...")
            networkRepository.getEmployees(getBasicRequest()).collect{apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateSyncLoader(true)
                    },
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            dataBaseRepository.insertEmployees(apiData)
                            val posEmployees = getPosEmployees()
                            _settingUiState.update { it.copy(posEmployees = posEmployees) }
                        }
                    },
                    onError = {errorMsg->
                        updateError(EMPLOYEE_ERROR_TITLE,errorMsg)
                        updateSyncLoader(false)
                    }
                )
            }
        }catch (e: Exception){
            val error=e.message.toString()
            updateError(EMPLOYEE_ERROR_TITLE,error)
            updateSyncLoader(false)
        }
    }

    private suspend fun syncEmployeeRole(){
        try {
            println("Syncing Employees Role")
            networkRepository.getEmployeeRole(getBasicRequest()).collect{apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {   updateSyncLoader(true)},
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            dataBaseRepository.insertEmpRole(apiData)
                            updateSyncLoader(false)
                        } },
                    onError = {errorMsg->
                        updateError(EMPLOYEE_ROLE_ERROR_TITLE,errorMsg)
                        updateSyncLoader(false)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateError(EMPLOYEE_ROLE_ERROR_TITLE,error)
            updateSyncLoader(false)
        }
    }

    private fun updateSyncLoader(value:Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _settingUiState.update { it.copy(syncLoader=value) }
        }
    }

    private fun updateError(errorTitle:String, errorMsg: String) {
        viewModelScope.launch(Dispatchers.Main) {
            println(errorMsg)
            _settingUiState.update { it.copy(isError =true,errorMsg=errorMsg) }
        }
    }
}