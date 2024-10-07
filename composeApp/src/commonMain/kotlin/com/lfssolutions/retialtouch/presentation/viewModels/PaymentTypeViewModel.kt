package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeUIState
import com.lfssolutions.retialtouch.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PaymentTypeViewModel : BaseViewModel(), KoinComponent {

    private val _screenUIState = MutableStateFlow(PaymentTypeUIState())
    val screenUIState: StateFlow<PaymentTypeUIState> = _screenUIState.asStateFlow()

    fun loadDataFromDatabases(memberId: Int, totalAmount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading to true at the start
            updateLoader(true)
            delay(2000)
            try {
                // Load data from multiple databases concurrently
                val data1Deferred = async { getCurrencySymbol() }
                val data2Deferred = async { databaseRepository.getAllPaymentType()}
                val data3Deferred = async { getDeliveryType() }
                val data4Deferred = async { getStatusType() }

                // Await all results
                val currencySymbol = data1Deferred.await()
                val paymentTypeDao = data2Deferred.await()
                val deliveryType = data3Deferred.await()
                val statusType = data4Deferred.await()

                // Update your state directly with responses
                _screenUIState.update { currentState->
                    currentState.copy(
                        currencySymbol = currencySymbol
                    )
                }

                paymentTypeDao.collectLatest { paymentList ->
                    val transformedProductList = paymentList.map { itemDao ->
                        // Access the rowItem and map it to the desired format
                        itemDao.rowItem
                    }
                    _screenUIState.update {
                        it.copy(paymentList = transformedProductList)
                    }
                }

                deliveryType.collectLatest { deliveryList ->
                    _screenUIState.update {
                        it.copy(deliveryTypeList = deliveryList, selectedDeliveryType = deliveryList[0])
                    }
                }

                statusType.collectLatest { statusList ->
                    _screenUIState.update {
                        it.copy(statusTypeList = statusList , selectedStatusType = statusList[0])
                    }
                }

                _screenUIState.update {
                    it.copy(memberId = memberId , totalAmount = totalAmount, remainingAmount = totalAmount)
                }

                updateLoader(false)

            } catch (e: Exception) {
                // Handle errors, ensuring loading state is false
                updateLoader(false)
            }
        }
    }

    private fun updateLoader(value:Boolean){
        viewModelScope.launch {
            _screenUIState.update { it.copy(isLoading = value) }
        }
    }

    private suspend fun getDeliveryType() : Flow<List<DeliveryType>> {
        return flow {
            val deliveryTypes = listOf(
                DeliveryType(id = 1, name = "Delivery"),
                DeliveryType(id = 2, name = "Self Collection"),
                DeliveryType(id = 3, name = "Rental Delivery"),
                DeliveryType(id = 4, name = "Rental Collection")
            )
            emit(deliveryTypes)  // Emit the list as a Flow
        }
    }

    private suspend fun getStatusType() : Flow<List<StatusType>> {
        return flow {
            val statusType = listOf(
                StatusType(id = 1, name = "Pending"),
                StatusType(id = 2, name = "Delivered"),
                StatusType(id = 3, name = "Self Connected"),
                StatusType(id = 4, name = "Returned")
            )
            emit(statusType)  // Emit the list as a Flow
        }
    }


    fun updateDeliveryType(value:DeliveryType){
        viewModelScope.launch {
            _screenUIState.update { it.copy(selectedDeliveryType = value) }
        }
    }

    fun updateStatusType(value:StatusType){
        viewModelScope.launch {
            _screenUIState.update { it.copy(selectedStatusType = value) }
        }
    }

    fun updateRemark(value:String){
        viewModelScope.launch {
            _screenUIState.update { it.copy(remark = value) }
        }
    }

    fun updatePaymentById(item: PaymentTypeItem ) {
        println("selectedPayment : $item")
        viewModelScope.launch {
            _screenUIState.update { currentState ->
                val updatedProductList = currentState.paymentList.map { payment ->
                    if (payment.id == item.id){
                        payment.copy(isSelected=true,isShowPaidAmount=true,paidAmount="${currentState.currencySymbol} ${currentState.remainingAmount}")
                    }
                    else payment.copy(isSelected = false, isShowPaidAmount = false, paidAmount = "")
                }
                currentState.copy(paymentList = updatedProductList, remainingAmount = 0.0,selectedPayment = item)
            }
        }
    }

    fun omPaymentIconClick(item: PaymentTypeItem){
        viewModelScope.launch {
            _screenUIState.update { it.copy(isShowCalculator = true, selectedPayment = item) }
        }
    }

    fun dismissNumberPadDialog(){
        viewModelScope.launch {
            _screenUIState.update { it.copy(isShowCalculator = false) }
        }
    }

    fun updateEnterAmountValue(value:String){
        viewModelScope.launch {
            _screenUIState.update {
                // Convert input value to a number (e.g., Double for decimal numbers)
                val numericValue = value.toDoubleOrNull()
                var errorMessage: String? = null
                if (numericValue == null) {
                    // Handle invalid input (non-numeric values)
                    errorMessage = "Invalid input. Please enter a valid number."
                }else{
                    when {
                        numericValue < it.minValue -> {
                            errorMessage = "Value must be at least ${it.minValue}"
                        }
                        numericValue > it.totalAmount -> {
                            errorMessage = "Value must not exceed ${it.totalAmount}"
                        }
                    }
                }

                it.copy(inputAmount = value, inputDiscountError =errorMessage )
            }
        }
    }


    fun onNumberPadClick(symbol: String) {
        with(_screenUIState.value){
            when (symbol) {
                "." -> if (!inputAmount.contains('.')) {
                    updateEnterAmountValue(("$inputAmount."))
                }
                "x" -> if (inputAmount.isNotEmpty()) {
                    val newDiscount = inputAmount.dropLast(1)
                    updateEnterAmountValue(newDiscount)
                }
                else -> {
                    val updatedDiscount = (inputAmount + symbol)
                    updateEnterAmountValue(updatedDiscount)
                }
            }
        }
    }

    fun onNumberPadApplyClick(){
        with(_screenUIState.value){
            if(inputAmount.isNotEmpty()){
                val updatedRemaining = totalAmount-inputAmount.toDouble()
                updatedRemaining(updatedRemaining)
            }
        }
    }

    private fun updatedRemaining(updatedRemaining: Double) {
        viewModelScope.launch {
            _screenUIState.update {currentState->
                val updatedProductList = currentState.paymentList.map { payment ->
                    if (payment.id == currentState.selectedPayment.id){
                        payment.copy(paidAmount="${currentState.currencySymbol} ${currentState.inputAmount}")
                    }
                    else payment.copy(isSelected = false, isShowPaidAmount = false, paidAmount = "")
                }
                currentState.copy(remainingAmount = updatedRemaining, paymentList = updatedProductList,isShowCalculator = false)
            }
        }
    }



}