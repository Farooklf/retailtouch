package com.hashmato.retailtouch.presentation.ui.payout


import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.ApiUtils.observeResponseNew
import com.hashmato.retailtouch.domain.model.payout.CreateExpensesRequest
import com.hashmato.retailtouch.domain.model.payout.PayOutIn
import com.hashmato.retailtouch.presentation.viewModels.BaseViewModel
import com.hashmato.retailtouch.utils.DateTimeUtils.getCurrentLocalDateTime
import com.hashmato.retailtouch.utils.getAppName
import com.hashmato.retailtouch.utils.getRandomString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.koin.core.component.KoinComponent
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.amount_error
import retailtouch.composeapp.generated.resources.desc_error
import retailtouch.composeapp.generated.resources.pay_to_error

class PayoutViewModel : BaseViewModel(), KoinComponent {

    private val _payoutUiState = MutableStateFlow(PayoutUIState())
    val payoutUiState: StateFlow<PayoutUIState> = _payoutUiState.asStateFlow()


    fun updateDescription(updatedValue:String){
        viewModelScope.launch {
            _payoutUiState.update { state->
                state.copy(expenseDescription = updatedValue)
            }
        }
    }

    fun updatePayTo(updatedValue:String){
        viewModelScope.launch {
            _payoutUiState.update { state->
                state.copy(expensePayTo = updatedValue)
            }
        }
    }

    fun updateAmount(updatedValue:String){
        viewModelScope.launch {
            _payoutUiState.update { state->
                state.copy(expenseAmount = updatedValue)
            }
        }
    }

    fun resetError(){
        viewModelScope.launch {
            _payoutUiState.update { it.copy(isError = false, errorMsg = "") }
        }
    }


    fun onSubmitClick(){
        val state=payoutUiState.value
        if(state.expenseDescription.isEmpty()) {
            updateDescriptionError(Res.string.desc_error)
            return
        }

        if(state.expensePayTo.isEmpty()) {
            updatePayToError(Res.string.pay_to_error)
            return
        }

        if(state.expenseAmount.isEmpty()) {
            updateAmountError(Res.string.amount_error)
            return
        }

        submitExpenseForm(state.expenseDescription,state.expensePayTo,state.expenseAmount.toDouble())
    }


    private fun updateDescriptionError(error: StringResource?) {
        viewModelScope.launch {
            _payoutUiState.update { it.copy(errorDescription = error) }
        }
    }

    private fun updatePayToError(error: StringResource?) {
        viewModelScope.launch {
            _payoutUiState.update { it.copy(errorPayTo = error) }
        }
    }

    private fun updateAmountError(error: StringResource?) {
        viewModelScope.launch {
            _payoutUiState.update { it.copy(errorPayTo = error) }
        }
    }

    private fun resetTextFiled(){
        viewModelScope.launch {
            _payoutUiState.update { it.copy(expenseDescription = "", expensePayTo = "", expenseAmount = "") }
        }
    }

    private fun updateSyncLoader(value:Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _payoutUiState.update { it.copy(isSyncLoader =value) }
        }
    }

    private fun updateError(errorMsg: String) {
        viewModelScope.launch(Dispatchers.Main) {
            println(errorMsg)
            _payoutUiState.update { it.copy(isError =true,errorMsg=errorMsg) }
        }
    }



    private fun submitExpenseForm(expenseDescription: String, expensePayTo: String, amount: Double) {
        viewModelScope.launch {
            try {
                val location=getDefaultLocation()
                val payoutInNo = "${location.name} ${getRandomString(5)}"
                networkRepository.createOrUpdatePayoutIn(
                    CreateExpensesRequest(
                    payOutIn = PayOutIn(
                    type= "E",
                    locationId= location.locationId,
                    terminal= getAppName(),
                    terminalName=getAppName() ,
                    description= expenseDescription.trim(),
                    amount=amount,
                    payTo= expensePayTo.trim(),
                    createdDateTime= getCurrentLocalDateTime(),
                    transactionDate= getCurrentLocalDateTime(),
                    payOutInNo=payoutInNo))
                ).collect { apiResponse ->
                    observeResponseNew(apiResponse,
                        onLoading = {
                            updateSyncLoader(true)
                        },
                        onSuccess = { apiData ->
                            if (apiData.success) {
                                resetTextFiled()
                                updateSyncLoader(false)
                            } else {
                                updateSyncLoader(false)
                            }
                        },
                        onError = { errorMsg ->
                            updateError(errorMsg)
                            updateSyncLoader(false)
                        }
                    )
                }
            }catch (e: Exception){
                updateError("Unable to Save Expense")
                updateSyncLoader(false)
            }
        }
    }
}