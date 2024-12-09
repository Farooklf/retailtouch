package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.TransactionDetailsState
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.posInvoices.GetPosInvoiceForEditRequest
import com.lfssolutions.retialtouch.domain.model.products.PosPayment
import com.lfssolutions.retialtouch.utils.NumberFormatting
import com.lfssolutions.retialtouch.utils.getDeliveryType
import com.lfssolutions.retialtouch.utils.getStatusType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class TransactionDetailsViewModel : BaseViewModel(), KoinComponent {

    private val _screenState = MutableStateFlow(TransactionDetailsState())
    val screenState: StateFlow<TransactionDetailsState> = _screenState.asStateFlow()


    fun getPosInvoiceForEdit(id: Long) {
        viewModelScope.launch {
            try {
                networkRepository.getPosInvoiceForEdit(GetPosInvoiceForEditRequest(id=id)).collect{apiResponse->
                    observeResponseNew(apiResponse,
                        onLoading = {
                            updateLoader(true)
                        },
                        onSuccess = { apiData ->
                            if(apiData.success){
                              _screenState.update { it.copy(posInvoice = apiData.result?.posInvoice, posInvoiceDetail = apiData.result?.posInvoiceDetail) }
                                loadFilterData()
                                updateLoader(false)
                            }else{
                                loadFilterData()
                                updateLoader(false)
                            }
                        },
                        onError = { errorMsg ->
                            updateError(errorMsg,true)
                            updateLoader(false)
                        }
                    )
                }
            }catch (e:Exception){
                updateError(e.message?:"Unknown Error",true)
                updateLoader(false)
            }
        }
    }

    private fun loadFilterData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Combine both flows to load them in parallel
            combine(
                getDeliveryType(),
                getStatusType(),
                dataBaseRepository.getPaymentType(),
                preferences.getCurrencySymbol()

            ) { typeList, statusList,paymentList,currencySymbol->
                LoadData(typeList,statusList,paymentList,currencySymbol)
            }
                .onStart {
                    _screenState.update { it.copy(isLoading = true)}
                    delay(2000)
                }  // Show loader when starting
                .catch { th ->
                    println("exception : ${th.message}")
                    _screenState.update { it.copy(isLoading = false) }
                }   // Handle any errors and hide loader
                .collect { (typeList, statusList,paymentList,currencySymbol) ->
                    println("typeList : $typeList | statusList : $statusList | paymentList : $paymentList")
                    _screenState.update {  it.copy(
                        typeList=typeList,
                        statusList = statusList,
                        paymentModes = paymentList,
                        currencySymbol = currencySymbol,
                        isLoading = false
                    )
                    }
                }
        }
    }

    data class LoadData(
        val typeList: List<DeliveryType>,
        val statusList: List<StatusType>,
        val paymentList: List<PaymentMethod>,
        val currencySymbol:String
    )

    fun updatePaymentDialogState(value:Boolean){
        viewModelScope.launch {
            _screenState.update { it.copy(showPaymentModeDialog = value) }
        }
    }
    fun updateClickedPayment(posPayment: PosPayment){
        viewModelScope.launch {
            _screenState.update { it.copy(clickedPayment = posPayment) }
        }
    }
    fun updateSelectedPaymentMode(selectedPayment: PaymentMethod){
        viewModelScope.launch {
            _screenState.update {state->
                // Update the clicked payment's payment type ID
                val updatedClickedPayment = state.clickedPayment.copy(paymentTypeId = selectedPayment.id)

                // Update the posPayments list with the new payment mode
                val updatedPayments = state.posInvoice?.posPayments?.map { payment ->
                    if (payment.paymentTypeId == state.clickedPayment.paymentTypeId) {
                        payment.copy(paymentTypeId = selectedPayment.id) // Update the matched payment
                    } else {
                        payment // Keep the others unchanged
                    }
                }

                // Create an updated posInvoice with the modified payments
                val updatedInvoice = state.posInvoice?.copy(posPayments = updatedPayments)

                // Return the updated state
                state.copy(
                    clickedPayment = updatedClickedPayment,
                    posInvoice = updatedInvoice,
                    selectedPaymentMethod = selectedPayment
                )
            }
        }
    }

    fun fetchPaymentList(){
        viewModelScope.launch(Dispatchers.IO){
            dataBaseRepository.getPaymentType().collect { list->
                withContext(Dispatchers.Main) {
                    _screenState.update { it.copy(paymentModes = list)
                    }
                }
            }
        }
    }

    private fun updateLoader(value:Boolean){
        _screenState.update { it.copy(isLoading = value) }
    }

    private fun updateError(error: String, isError: Boolean){
        viewModelScope.launch {
            _screenState.update { state->state.copy(errorMessage = error, isError = isError) }
        }
    }

    fun updateType(type: DeliveryType){
        viewModelScope.launch {
            _screenState.update { state->
                val newPosValue=state.posInvoice?.copy(type = type.id)
                state.copy(deliveryType = type,
                type = type.id,
                isFilterApplied=true,
                    posInvoice = newPosValue
            )}
        }
    }

    fun updateStatus(status: StatusType){
        viewModelScope.launch {
            _screenState.update {state->
                val newPosValue=state.posInvoice?.copy(status = status.id)
                state.copy(statusType = status,
                    status = status.id,
                    isFilterApplied = true,
                    posInvoice = newPosValue
                )}
        }
    }

    fun buildPaymentMethodTile(posPayment: PosPayment) {

    }

    fun formatPriceForUI(amount: Double?) :String{
        return  "${_screenState.value.currencySymbol}${NumberFormatting().format(amount?:0.0,2)}"
    }
}