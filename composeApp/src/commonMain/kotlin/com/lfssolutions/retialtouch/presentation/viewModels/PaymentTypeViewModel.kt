package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeUIState
import com.lfssolutions.retialtouch.domain.model.productWithTax.CreatePOSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosInvoice
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosInvoiceDetail
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosPayment
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.utils.DateTime.getCurrentDate
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class PaymentTypeViewModel : BaseViewModel(), KoinComponent {

    private val _screenUIState = MutableStateFlow(PaymentTypeUIState())
    val screenUIState: StateFlow<PaymentTypeUIState> = _screenUIState.asStateFlow()

    fun fetchPaymentList(){
        viewModelScope.launch(Dispatchers.IO){
            dataBaseRepository.getPaymentType().collectLatest { list->
                withContext(Dispatchers.Main) {
                    _screenUIState.update { it.copy(paymentList = list)
                    }
                }
            }
        }
    }

    fun loadDataFromDatabases(posUIState: PosUIState) {
        viewModelScope.launch(Dispatchers.IO) {
            // Set loading to true at the start
            updateLoader(true)
            //delay(2000)
            try {
                // Load data from multiple databases concurrently
                val data1Deferred = async { getCurrencySymbol() }
                //val data2Deferred = async { sqlPreference.getAllPaymentType()}
                //val data3Deferred = async { getDeliveryType() }
                //val data4Deferred = async { getStatusType() }

                // Await all results
                val currencySymbol = data1Deferred.await()
                //val paymentTypeDao = data2Deferred.await()
                //val deliveryType = data3Deferred.await()
                //val statusType = data4Deferred.await()

                // Update your state directly with responses
                _screenUIState.update { currentState->
                    currentState.copy(
                        currencySymbol = currencySymbol
                    )
                }

                /*paymentTypeDao.collectLatest { paymentList ->
                    val transformedProductList = paymentList.map { itemDao ->
                        // Access the rowItem and map it to the desired format
                        itemDao.rowItem
                    }
                    _screenUIState.update {
                        it.copy(paymentList = transformedProductList)
                    }
                }*/

                /*deliveryType.collectLatest { deliveryList ->
                    _screenUIState.update {
                        it.copy(deliveryTypeList = deliveryList, selectedDeliveryType = deliveryList[0])
                    }
                }

                statusType.collectLatest { statusList ->
                    _screenUIState.update {
                        it.copy(statusTypeList = statusList , selectedStatusType = statusList[0])
                    }
                }*/

                _screenUIState.update {
                    it.copy(posUIState = posUIState, remainingBalance = posUIState.grandTotal, grandTotal = posUIState.grandTotal)
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

        viewModelScope.launch {
            _screenUIState.update { currentState ->
                val updatedProductList = currentState.paymentList.map { payment ->
                    if (payment.id == item.id){
                        payment.copy(isSelected=true,/*isShowPaidAmount=true,paidAmount="${currentState.currencySymbol} ${currentState.remainingAmount}"*/)
                    }
                    else payment
                }
                currentState.copy(paymentList = updatedProductList, /*remainingAmount = 0.0,*/selectedPayment = item, showPaymentCollectorDialog = true)
            }
        }
    }

    fun omPaymentIconClick(item: PaymentTypeItem){
        viewModelScope.launch {
            _screenUIState.update { it.copy(showPaymentCollectorDialog = true, selectedPayment = item) }
        }
    }

    fun dismissNumberPadDialog(){
        viewModelScope.launch {
            _screenUIState.update { it.copy(showPaymentCollectorDialog = false) }
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
                        numericValue > it.grandTotal -> {
                            errorMessage = "Value must not exceed ${it.grandTotal}"
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
                val updatedRemaining = grandTotal-inputAmount.toDouble()
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
                currentState.copy(remainingBalance = updatedRemaining, paymentList = updatedProductList,showPaymentCollectorDialog = false)
            }
        }
    }



    fun updateDeletePaymentModeDialog(value: Boolean) {
        _screenUIState.update { state -> state.copy(showDeletePaymentModeDialog = value) }
    }
    fun updateSelectedPaymentToDeleteId(selectedPaymentToDelete: Int) {
        _screenUIState.update { state -> state.copy(selectedPaymentToDelete = selectedPaymentToDelete) }
    }

    fun deletePayment() {
        val state=screenUIState.value
        val deletedPayment = state.createdPayments.find { it.paymentTypeId == state.selectedPaymentToDelete }
        if (deletedPayment != null) {
            _screenUIState.update {
                it.copy(
                    createdPayments = it.createdPayments.toMutableList().apply { remove(deletedPayment) },
                    paymentTotal = ((it.paymentTotal - deletedPayment.amount)).roundTo(state.roundToDecimal),
                    remainingBalance = ((it.grandTotal - (it.paymentTotal - deletedPayment.amount))).roundTo(state.roundToDecimal)
                )
            }
        }
    }

    fun applyPaymentValue(){
        val state=screenUIState.value
        if(state.inputAmount.isNotEmpty()){
            val payment=state.inputAmount.toDouble()
            val appliedPayment = PosPayment(
                paymentTypeId = state.selectedPayment.id,
                name = state.paymentList.find { it.id == state.selectedPayment.id }?.name ?: "",
                amount = payment
            )
            _screenUIState.update {
                it.copy(
                    paymentTotal = ((it.paymentTotal + payment)).roundTo(),
                    remainingBalance = ((it.grandTotal - (it.paymentTotal + payment))).roundTo(),
                    createdPayments = it.createdPayments.apply { add(appliedPayment) },
                    isPaid = true,
                    showPaymentCollectorDialog = false
                )
            }
            //if (state.remainingBalance <= 0.0) updatePaymentSuccessDialogVisibility(true)
        }
    }

    fun onTenderClick(){
        viewModelScope.launch(Dispatchers.IO) {
            val posState=screenUIState.value.posUIState
            val scannedList = posState.shoppingCart
          if(scannedList.isNotEmpty()){
              updateLoader(true)
              createStockList(scannedList)
              val employee=dataBaseRepository.getEmployeeByCode(preferences.getEmployeeCode().first())
              val employeeId=employee?.employeeId
              authenticationDao.collectLatest { authUser->
                  authUser?.loginDao?.let {
                      val posInvoice=PosInvoice(
                          invoiceDate=getCurrentDate(),
                          locationId = it.defaultLocationId?:0,
                          locationCode= it.locationCode?:"",
                          tenantId = it.tenantId?:0,
                          terminalId = it.defaultLocationId?:0,
                          terminalName = "RetailTouch",
                          employeeId = employeeId,
                          invoiceTotal = posState.invoiceSubTotal, //before Tax
                          invoiceTotalAmount= posState.grandTotal,
                          invoiceTotalValue = posState.grandTotal,
                          invoiceSubTotal=posState.grandTotal,
                          invoiceNetDiscountPerc = posState.invoiceNetDiscountPerc,
                          invoiceNetDiscount = posState.invoiceNetDiscount,
                          invoiceItemDiscount= posState.itemsDiscount,
                          invoiceTax = posState.invoiceTax,
                          invoiceNetTotal = posState.grandTotal,
                          invoiceNetCost = posState.grandTotal,
                          paid = screenUIState.value.paymentTotal,
                          memberId = posState.selectedMemberId,
                          posPayments = screenUIState.value.posPayments,
                          posInvoiceDetails = screenUIState.value.posInvoiceDetails
                      )
                      createOrUpdatePosInvoice(CreatePOSInvoiceRequest(
                          posInvoice =posInvoice
                      ))
                  }
              }
          }
        }
    }

    private fun createStockList(list: List<ProductTaxItem>) {
        val posInvoiceDetails: MutableList<PosInvoiceDetail> = mutableListOf()
        list.forEach { item->
           val appliedStock = PosInvoiceDetail(
                productId = item.id,
                inventoryCode=item.inventoryCode?:"",
                inventoryName=item.name?:"",
                qty=item.qtyOnHand,
                price=item.price?:0.0,
                total=item.originalSubTotal,
                itemDiscountPerc=item.itemDiscountPerc,
                itemDiscount=item.itemDiscount,
                totalValue=item.originalSubTotal,
                netDiscount=item.itemDiscount,
                totalAmount=item.originalSubTotal,
                subTotal=item.cartTotal?:0.0,
                tax=item.taxValue?:0.0,
                netTotal=item.originalSubTotal,
                averageCost =item.originalSubTotal,
                netCost=item.originalSubTotal,
                taxPercentage=item.taxPercentage?.toInt()?:0,

            )
            posInvoiceDetails.toMutableList().add(appliedStock)
        }
        _screenUIState.update {
            it.copy(
                posInvoiceDetails = posInvoiceDetails
            )
        }
    }

    private suspend fun createOrUpdatePosInvoice(createPOSInvoiceRequest: CreatePOSInvoiceRequest) {
        try {
            println("calling api : ${count++}")
            networkRepository.createUpdatePosInvoice(createPOSInvoiceRequest).collectLatest { apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {

                    },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            resetScreenState()
                        }
                        updateLoader(false)
                    },
                    onError = {
                            errorMsg ->
                        _screenUIState.update { it.copy(errorMsg=errorMsg) }
                        updateLoader(false)
                    }
                )
            }
        }catch (e: Exception){
            val errorMsg="${e.message}"
            _screenUIState.update { it.copy(errorMsg=errorMsg) }
            updateLoader(false)
        }
    }

    private fun resetScreenState() {
        viewModelScope.launch {
            dataBaseRepository.clearScannedProduct()
            _screenUIState.update { it.copy(createdPayments = it.createdPayments.toMutableList().apply { clear() }, isPaid = false, posUIState = PosUIState(), isPaymentClose = true) }
        }
    }
}