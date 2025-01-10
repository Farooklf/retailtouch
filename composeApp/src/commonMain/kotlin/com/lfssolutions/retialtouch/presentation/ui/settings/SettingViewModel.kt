package com.lfssolutions.retialtouch.presentation.ui.settings


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import com.lfssolutions.retialtouch.theme.Language
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ROLE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppLanguage
import com.lfssolutions.retialtouch.utils.changeLang
import com.lfssolutions.retialtouch.utils.DateTimeUtils.formatDateForUI
import com.lfssolutions.retialtouch.utils.DateTimeUtils.formatMillisecondsToDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
            val language = try {
                AppLanguage.valueOf(getAppLanguage())
            } catch (err: Throwable) {
                AppLanguage.English
            }
            _settingUiState.update { state->
                state.copy(
                    serverUrl = getCurrentServer(),
                    tenant = rtUser.tenantName,
                    user = rtUser.userName,
                    networkConfig = getNetworkConfig(),
                    gridViewOption = getGridViewCount(),
                    mergeCartItems = getMergeCartItems(),
                    roundOffOption = getRoundOffOption(),
                    paymentConfirmPopup = getPaymentConfirmPopup(),
                    fastPaymode = getFastPaymentMode(),
                    posEmployees = posEmployees,
                    selectedLanguage= language
                )}
        }
        _readStats()
    }

     fun _readStats(){
        viewModelScope.launch {
            val inventoryCount = getInventoryUniqueCount()
            val categoryCount = getCategoriesCount()
            val menuItemsCount = getMenuItemsCount()
            val barcodeCount = getBarcodesCount()
            val pendingSaleCount = getPendingSaleCount()
            val lastSyncTime= formatDateForUI(formatMillisecondsToDateTime(getLastSyncTs()))
            val reSyncTime=getReSyncTimer()

            _settingUiState.update { state -> state.copy(statesInventory=inventoryCount, statsMenuCategories = categoryCount, statsMenuItems = menuItemsCount, statsBarcodes = barcodeCount, statsUnSyncedSales = pendingSaleCount,statsLastSyncTs=lastSyncTime, reSyncTime = reSyncTime)
              }
        }
    }

    fun updateSelectLanguageDialogVisibility(value: Boolean) {
        _settingUiState.update { state -> state.copy(showSelectLanguageDialog = value) }
    }

    fun updateNetworkConfigDialogVisibility(value: Boolean) {
        _settingUiState.update { state -> state.copy(showNetworkConfigDialog = value) }
    }

    fun updateSyncTimerDialogVisibility(value: Boolean) {
        _settingUiState.update { state -> state.copy(showSyncTimerDialog = value) }
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

    fun changeLanguage(value: AppLanguage) {
        viewModelScope.launch {
            changeAppLanguage(value)
            val updatedLang = when (value) {
                AppLanguage.English -> {
                    Language.English.isoFormat
                }
                AppLanguage.Arabic -> {
                    Language.Arabic.isoFormat
                }
                AppLanguage.French-> {
                    Language.French.isoFormat
                }
            }
            changeLang(updatedLang)
            _settingUiState.update { it.copy(selectedLanguage = value) }
        }
    }

    fun updateReSyncTime(updatedValue: String) {
        viewModelScope.launch {
             //setReSyncTimer(updatedValue.toInt())
            _settingUiState.update { state -> state.copy(reSyncTime = updatedValue.toInt(), showSyncTimerDialog = false) }
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