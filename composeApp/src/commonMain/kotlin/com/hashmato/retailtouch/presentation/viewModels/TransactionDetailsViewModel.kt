package com.hashmato.retailtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.ApiUtils.observeResponseNew
import com.hashmato.retailtouch.domain.model.dropdown.DeliveryType
import com.hashmato.retailtouch.domain.model.dropdown.StatusType
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleTransactionDetailsState
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.paymentType.PaymentMethod
import com.hashmato.retailtouch.domain.model.posInvoices.GetPosInvoiceForEditRequest
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSaleDao
import com.hashmato.retailtouch.domain.model.products.CreatePOSInvoiceRequest
import com.hashmato.retailtouch.domain.model.products.POSInvoicePrint
import com.hashmato.retailtouch.domain.model.products.PosInvoice
import com.hashmato.retailtouch.domain.model.products.PosInvoicePrintDetails
import com.hashmato.retailtouch.domain.model.products.PosPayment
import com.hashmato.retailtouch.utils.NumberFormatter
import com.hashmato.retailtouch.utils.POSInvoiceDefaultTemplate
import com.hashmato.retailtouch.utils.PrinterType
import com.hashmato.retailtouch.utils.TemplateType
import com.hashmato.retailtouch.utils.getDeliveryType
import com.hashmato.retailtouch.utils.getStatusType
import com.hashmato.retailtouch.utils.printer.PrinterServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class TransactionDetailsViewModel : BaseViewModel(), KoinComponent {

    private val _screenState = MutableStateFlow(SaleTransactionDetailsState())
    val screenState: StateFlow<SaleTransactionDetailsState> = _screenState.asStateFlow()


    fun getPosInvoiceForEdit(saleRecord: SaleRecord) {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(saleRecord=saleRecord) }
                networkRepository.getPosInvoiceForEdit(GetPosInvoiceForEditRequest(id=saleRecord.id?:0)).collect{apiResponse->
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
                preferences.getCurrencySymbol(),
                dataBaseRepository.getSelectedLocation(),

            ) { typeList, statusList,paymentList,currencySymbol,location->
                LoadData(typeList,statusList,paymentList,currencySymbol,location)
            }
                .onStart {
                    _screenState.update { it.copy(isLoading = true)}
                    //delay(2000)
                }  // Show loader when starting
                .catch { th ->
                    println("exception : ${th.message}")
                    _screenState.update { it.copy(isLoading = false) }
                }   // Handle any errors and hide loader
                .collect { (typeList, statusList,paymentList,currencySymbol,location) ->
                    println("typeList : $typeList | statusList : $statusList | paymentList : $paymentList | location : $location ")
                    _screenState.update {  it.copy(
                        typeList=typeList,
                        statusList = statusList,
                        paymentModes = paymentList,
                        currencySymbol = currencySymbol,
                        location=location,
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
        val currencySymbol:String,
        val location : Location?,
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
                val updatedInvoice = updatedPayments?.let { state.posInvoice.copy(posPayments = it) }

                // Return the updated state
                state.copy(
                    clickedPayment = updatedClickedPayment,
                    posInvoice = updatedInvoice,
                    selectedPaymentMethod = selectedPayment,
                    isFilterApplied = true
                )
            }
        }
    }


    fun updateLoader(value:Boolean){
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

    fun formatPriceForUI(amount: Double?) :String{
        return  "${_screenState.value.currencySymbol}${NumberFormatter().format(amount?:0.0,2)}"
    }

    fun rePrintAndCloseReceipt(){
        viewModelScope.launch {
            try {
                //_screenState.update { it.copy(isLoading = true) }
                val location=_screenState.value.location
                val member=_screenState.value.saleRecord.memberName?:""
                screenState.value.posInvoice?.let { state->
                    val newPosInvoice=  state.copy(
                        qty = state.qty,
                        customerName = member,
                        address1 = location?.address1?:"",
                        address2 = location?.address2?:"",
                    )
                    connectAndPrintTemplate(newPosInvoice)
                }

            }catch (ex:Exception){
                updateError(isError = true, error = "add printer setting first")
            }
        }
    }

    private suspend fun connectAndPrintTemplate(posInvoice: PosInvoice) {
        val currency=screenState.value.currencySymbol
        val posInvoicePrint= POSInvoicePrint(
            invoiceNo = posInvoice.invoiceNo?:"",
            invoiceDate = posInvoice.invoiceDate?:"",
            customerName = posInvoice.customerName,
            address1 = posInvoice.address1,
            address2 = posInvoice.address2,
            qty = posInvoice.qty,
            invoiceSubTotal = posInvoice.invoiceSubTotal,
            invoiceItemDiscount = posInvoice.invoiceItemDiscount,
            invoiceNetDiscount = posInvoice.invoiceNetDiscount,
            invoiceTax = posInvoice.invoiceTax,
            invoiceNetTotal = posInvoice.invoiceNetTotal,
            posInvoiceDetails = posInvoice.posInvoiceDetails.map {posDetails->
                PosInvoicePrintDetails(
                    posInvoiceId = posDetails.posInvoiceId,
                    inventoryName = posDetails.inventoryName,
                    inventoryCode = posDetails.inventoryCode,
                    productId = posDetails.productId,
                    qty = posDetails.qty.toInt(),
                    price = posDetails.price,
                    itemDiscount = if(posDetails.itemDiscount>0.0) "(Disc. -$currency${posDetails.itemDiscount})" else "",
                    itemDiscountPerc = posDetails.itemDiscountPerc,
                    netDiscount = posDetails.netDiscount,
                    netCost=posDetails.netCost,
                    netTotal = posDetails.netTotal,
                    subTotal = posDetails.subTotal)
            },
            posPayments = posInvoice.posPayments,
        )
        var template=POSInvoiceDefaultTemplate
        sqlRepository.getPrintTemplateByType(type = TemplateType.POSInvoice.toValue()).collect{ mPrintReceiptTemplate->
            mPrintReceiptTemplate?.let{
                template = it.template
                println("template $template")
            }
        }
        dataBaseRepository.getPrinter().collect { printer ->
            if(printer!=null){
                val finalTextToPrint = PrinterServiceProvider().getPrintTextForReceiptTemplate(posInvoicePrint,currency, template,printer)
                // println("finalTextToPrint :$finalTextToPrint")
                PrinterServiceProvider().connectPrinterAndPrint(
                    printers = printer,
                    printerType = when (printer.printerType) {
                        1L -> {
                            PrinterType.Ethernet
                        }
                        2L -> {
                            PrinterType.USB
                        }
                        3L -> {
                            PrinterType.Bluetooth
                        }
                        else -> {
                            PrinterType.Bluetooth
                        }
                    },
                    textToPrint = finalTextToPrint
                )
            }else{
                //Show Message that your device is not connected
                updateError(isError = true, error = "add printer setting first")
            }
        }
    }

    fun saveAndCloseReceipt() {
        viewModelScope.launch {
            try {
             screenState.value.apply {
                 posInvoice?.let {
                     val newPosInvoice= posInvoice.copy(terminalId = preferences.getLocationId().first().toLong())
                     networkRepository.createUpdatePosInvoice(CreatePOSInvoiceRequest(posInvoice = newPosInvoice)).collect{apiResponse->
                         observeResponseNew(apiResponse,
                             onLoading = {
                                 updateLoader(true)
                             },
                             onSuccess = { apiData ->
                                 if(apiData.success && !apiData.result?.posInvoiceNo.isNullOrEmpty()){
                                     viewModelScope.launch {
                                         dataBaseRepository.addUpdatePendingSales(
                                             PendingSaleDao(
                                                 posInvoice = posInvoice,
                                                 isDbUpdate = true,
                                                 isSynced = true
                                             )
                                         )
                                         updateSales()
                                     }
                                 }
                             },
                             onError = { errorMsg ->
                                 println(errorMsg)
                                 updateError("Error saving invoice \n $errorMsg",true)
                                 updateLoader(false)
                             })
                     }
                 }
             }
            }catch (ex:Exception){
                updateError("Error saving invoice \n ${ex.message}",true)
                updateLoader(false)
            }
        }
    }
}