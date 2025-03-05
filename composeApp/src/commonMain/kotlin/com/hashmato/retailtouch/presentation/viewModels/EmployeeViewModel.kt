package com.hashmato.retailtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.ApiUtils.observeResponseNew
import com.hashmato.retailtouch.domain.model.basic.BasicApiRequest
import com.hashmato.retailtouch.domain.model.employee.EmployeeUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.KoinComponent
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.employee_code_error
import retailtouch.composeapp.generated.resources.invalid_employee_error
import retailtouch.composeapp.generated.resources.invalid_pin_error
import retailtouch.composeapp.generated.resources.pin_error

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

    private fun updateEmpPinError(error: StringResource?) {
        viewModelScope.launch {
            _employeeScreenState.update { it.copy(pinError = error) }
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


    private fun validateInputs(): Map<String, StringResource?> {
        val errors = mutableMapOf<String, StringResource?>()

        if (_employeeScreenState.value.employeeCode.isBlank()) {
            errors["empCode"] = Res.string.employee_code_error
        }

        if (_employeeScreenState.value.pin.isBlank()) {
            errors["empPin"] = Res.string.pin_error
        }
        return errors
    }


    private fun performEmployeeLogin() {
        viewModelScope.launch {
            val employee=dataBaseRepository.getEmployeeByCode(employeeScreenState.value.employeeCode)
            if(employee!=null){
                employeeDoa.update { employee }
                if (employee.employeePassword == employeeScreenState.value.pin) {
                    setEmployeeCode(employee.employeeCode)
                    updatePOSEmployees(employee)
                    setPOSEmployee(employee)
                    getEmployeeRights()
                    updateEmployeeLogin(true)
                } else {
                    updateEmpPinError(Res.string.invalid_pin_error)
                }
            }else{
                updateEmpPinError(Res.string.invalid_employee_error)
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
}