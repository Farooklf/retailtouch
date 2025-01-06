package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.employee.EmployeeUIState
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class EmployeeViewModel : BaseViewModel(), KoinComponent {

    private val _employeeScreenState = MutableStateFlow(EmployeeUIState())
    val employeeScreenState: StateFlow<EmployeeUIState> = _employeeScreenState.asStateFlow()

    fun updateEmployeeCode(urlInput: String) {
        viewModelScope.launch{
            _employeeScreenState.update { it.copy(employeeCode = urlInput) }
        }
    }

    fun updateEmployeePin(urlInput: String) {
        viewModelScope.launch {
            _employeeScreenState.update { it.copy(pin = urlInput) }
        }

    }

    private fun updateEmployeeLogin(loginValue: Boolean) {
        viewModelScope.launch {
            _employeeScreenState.update { it.copy(isEmployeeLoginSuccess = loginValue) }
        }
    }

    private fun updateEmpCodeError(error: String?) {
        _employeeScreenState.update { it.copy(employeeCodeError = error) }
    }

    private fun updateEmpPinError(error: String?) {
        viewModelScope.launch {
            _employeeScreenState.update { it.copy(pinError = error) }
        }
    }

    private fun updateLogout(logoutValue: Boolean) {
        viewModelScope.launch {
            _employeeScreenState.update { it.copy(isLogoutFromServer = logoutValue) }
        }
    }

    fun onClick() {
        val errors = validateInputs()
        if (errors.isEmpty()) {
            performEmployeeLogin()
        } else {
            _employeeScreenState.value = _employeeScreenState.value.copy(
                employeeCodeError = errors["empCode"],
                pinError = errors["empPin"]
            )
        }
    }


    private fun validateInputs(): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()

        if (_employeeScreenState.value.employeeCode.isBlank()) {
            errors["empCode"] = "employee code is required"
        }

        if (_employeeScreenState.value.pin.isBlank()) {
            errors["empPin"] = "pin is required"
        }
        return errors
    }


    private fun performEmployeeLogin() {
        viewModelScope.launch {
            val employee=dataBaseRepository.getEmployeeByCode(employeeScreenState.value.employeeCode)
            if(employee!=null){
                employeeDoa.update { employee }
                if (employee.employeePassword == employeeScreenState.value.pin) {
                    updatePOSEmployees(employee)
                    setPOSEmployee(employee)
                    getEmployeeRights()
                    updateEmployeeLogin(true)
                } else {
                    updateEmpPinError("PIN is not correct")
                }
            }else{
                updateEmpPinError("It seems you are not a valid user")
            }
        }
    }

    private fun getEmployeeRights(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //updateLoaderMsg("Syncing Employee Rights")
                networkRepository.getEmployeeRights(BasicApiRequest(
                    tenantId = getTenantId(),
                    name = employeeDoa.value?.employeeName
                )).collectLatest {apiResponse->
                    observeResponseNew(apiResponse,
                        onLoading = {

                        },
                        onSuccess = { apiData ->
                            if(apiData.success){
                                viewModelScope.launch {
                                    dataBaseRepository.insertEmpRights(apiData)
                                }
                            }
                        },
                        onError = { errorMsg ->
                            //handleApiError(EMPLOYEE_ERROR_TITLE,errorMsg)
                        }
                    )
                }
            }catch (e: Exception){
                val error="${e.message}"
                //handleApiError(EMPLOYEE_ERROR_TITLE,error)
            }
        }
    }


    fun logoutFromThisServer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start all three operations concurrently
                val jobs = listOf(
                    async { resetStates() },
                    async { emptyDataBase() },
                    async { emptyLocalPref() }
                )
                // Wait for all jobs to complete
                jobs.awaitAll()

                // After all operations complete, update logout status
                updateLogout(true)
            } catch (e: Exception) {
                // Handle exceptions if needed (e.g., logging or user feedback)
                e.printStackTrace()
            }
        }
    }

}