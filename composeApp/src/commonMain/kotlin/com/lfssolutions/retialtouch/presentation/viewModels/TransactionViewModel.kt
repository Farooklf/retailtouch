package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.MemberType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleTransactionState
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSaleDao
import com.lfssolutions.retialtouch.domain.model.products.CreatePOSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.utils.NumberFormatting
import com.lfssolutions.retialtouch.utils.getDeliveryType
import com.lfssolutions.retialtouch.utils.getStatusType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent



class TransactionViewModel : BaseViewModel(), KoinComponent {

    private val _screenState = MutableStateFlow(SaleTransactionState())
    val screenState: StateFlow<SaleTransactionState> = _screenState.asStateFlow()

    fun getAuthDetails(){
        viewModelScope.launch {
            authUser.collectLatest { authDetails->
                if(authDetails!=null){
                    val login=authDetails.loginDao
                    _screenState.update {
                        it.copy(
                            loginUser = login,
                            currencySymbol = login.currencySymbol?:"$"
                        )
                    }
                }
            }
        }
    }

    fun loadFilterData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Combine both flows to load them in parallel
            combine(
                dataBaseRepository.getMember(),
                getDeliveryType(),
                getStatusType()

            ) { memberList,typeList, statusList->
                LoadData(memberList,typeList,statusList)
            }
                .onStart {
                    _screenState.update { it.copy(isLoading = true)}
                }  // Show loader when starting
                .catch { th ->
                    println("exception : ${th.message}")
                    _screenState.update { it.copy(isLoading = false) }
                }   // Handle any errors and hide loader
                .collect { (memberList,typeList, statusList) ->
                    println("memberList : $memberList | typeList : $typeList | statusList : $statusList")
                    val member=memberList.mapIndexed {index,member->
                        if(index==0){
                            MemberType(
                                id = index,
                                memberId = 0,
                                name = "All"
                            )
                        }else{
                            MemberType(
                                id = index,
                                memberId = member.rowItem.id,
                                name = member.rowItem.name
                            )
                        }
                    }
                    _screenState.update {  it.copy(
                        typeList=typeList,
                        statusList = statusList,
                        memberList = member,
                        isLoading = false
                    )
                    }
                }
        }
    }

    data class LoadData(
        val memberList: List<MemberDao>,
        val typeList: List<DeliveryType>,
        val statusList: List<StatusType>
    )

    fun getSales(){
      viewModelScope.launch {
          dataBaseRepository.getSaleTransaction().collectLatest{sale->
              val sortedSales = sale.sortedByDescending { sale -> sale.creationDate }
              _screenState.update { it.copy(transactionSales = sortedSales) }
              println("SaleList : $sortedSales")
          }
      }
    }

    fun getPendingSales(){
        viewModelScope.launch {
            dataBaseRepository.getPosPendingSales().collectLatest{sale->
                val sortedSales = sale.sortedByDescending { sale -> sale.invoiceDate }
                _screenState.update { it.copy(pendingSales = sortedSales) }
                 println("PendingSaleList : $sortedSales")
            }
        }
    }


    fun updateLoader(value:Boolean){
        _screenState.update { it.copy(isLoading = value) }
    }

     fun updateTransactionLoading(value:Boolean){
        viewModelScope.launch {
            _screenState.update { it.copy(isSaleTransactionSync = value) }
        }
    }

    fun updatePendingLoading(value:Boolean){
        viewModelScope.launch {
            _screenState.update { it.copy(isSalePendingSync = value) }
        }
    }

    fun updatePendingSalePopupState(value:Boolean){
        viewModelScope.launch {
            _screenState.update { it.copy(showPendingSalePopup = value) }
        }
    }

    fun updatePOSSale(value: PosInvoice){
        viewModelScope.launch {
            _screenState.update { it.copy(posInvoice = value) }
        }
    }


    fun onSelectedType(type: DeliveryType){
        viewModelScope.launch {
            _screenState.update { it.copy(deliveryType = type, type = type.id,isTypeFilter=true)}
        }
    }

    fun onSelectedStatus(status: StatusType){
        viewModelScope.launch {
            _screenState.update { it.copy(statusType = status, status = status.id, isStatusFilter = true)}
        }
    }

    fun onSelectedMember(member: MemberType){
        viewModelScope.launch {
            _screenState.update { it.copy(member = member, memberId = member.memberId ,isMemberFilter=true)}
        }
    }

    fun updateStartDate(newVal: LocalDate){
        viewModelScope.launch {
            _screenState.update { state->state.copy(startDate = newVal.toString(), isFromDateFilter = true) }
        }
    }

    fun updateEndDate(newVal: LocalDate){
        viewModelScope.launch {
            _screenState.update { state->state.copy(endDate = newVal.toString(), isEndDateFilter = true) }
        }
    }

    fun updateDatePickerDialog(newValue:Boolean,isFromDateValue:Boolean){
        viewModelScope.launch {
            _screenState.update { state->state.copy(isDatePickerDialog = newValue, isFromDate = isFromDateValue) }
        }
    }
    fun formatPriceForUI(amount: Double?) :String{
        return  "${_screenState.value.currencySymbol}${NumberFormatting().format(amount?:0.0,2)}"
    }

    fun updateError(error: String,isError: Boolean){
        viewModelScope.launch {
            _screenState.update { state->state.copy(errorMessage = error, isError = isError) }
        }
    }

    fun syncTransaction(){
        viewModelScope.launch {
            updateTransactionLoading(true)
            updateLoader(true)
            updateSales()
        }
    }

    /*fun syncPendingSales() {
        try {
            viewModelScope.launch {
                updatePendingLoading(true)
                val state = screenState.value
                screenState.value.let { state->
                    state.pendingSales.forEach { pendingSale->
                        val posInvoice = deClassifyPendingRecord(
                            pendingSale,
                            state.loginUser,
                            state.loginUser.tenantId,
                        )
                        updatePOSSale(posInvoice)
                        networkRepository.createUpdatePosInvoice(CreatePOSInvoiceRequest(posInvoice = posInvoice)).collect { apiResponse->
                            observeResponseNew(apiResponse,
                                onLoading = {
                                    updateLoader(true)
                                },
                                onSuccess = { apiData ->
                                    if(apiData.success && apiData.result?.posInvoiceNo != null){
                                        viewModelScope.launch {
                                            dataBaseRepository.addUpdatePendingSales(
                                                PendingSaleDao(
                                                    posInvoice = posInvoice,
                                                    isDbUpdate = true,
                                                    isSynced = true
                                                ))
                                            updateLoader(false)
                                            updateSales()
                                        }
                                    }
                                },
                                onError = { errorMsg ->
                                    println(errorMsg)
                                    updateError("Error saving invoice \n ${errorMsg}",true)
                                    updateLoader(false)
                                    updatePendingLoading(false)
                                }
                            )
                        }
                    }
                }
            }
        }catch (ex:Exception){
            updateError("Error saving invoice \n ${ex.message}",true)
            updateLoader(false)
            updatePendingLoading(false)
        }
    }*/

   /* private fun deClassifyPendingRecord(data: PendingSale):PosInvoice{
        val state=screenState.value
        val posInvoice= PosInvoice(
            id = data.id,
            tenantId = state.loginUser.tenantId,
            employeeId = data.employeeId,
            locationId=state.loginUser.defaultLocationId?.toLong(),
            locationCode = state.loginUser.locationCode,
            terminalId = state.loginUser.defaultLocationId?.toLong(),
            terminalName = data.terminalName,
            isRetailWebRequest=data.isRetailWebRequest,
            invoiceNo = data.invoiceNo,
            invoiceDate= data.invoiceDate,
            invoiceTotal = data.invoiceTotal, //before Tax
            invoiceItemDiscount = data.invoiceItemDiscount,
            invoiceTotalValue= data.invoiceTotalValue,
            invoiceNetDiscountPerc= data.invoiceNetDiscountPerc,
            invoiceNetDiscount= data.invoiceNetDiscount,
            invoiceTotalAmount=data.invoiceTotalAmount,
            invoiceSubTotal= data.invoiceSubTotal,
            invoiceTax= data.globalTax,
            invoiceRoundingAmount = data.invoiceRoundingAmount,
            invoiceNetTotal= data.invoiceNetTotal,
            invoiceNetCost= data.invoiceNetCost,
            paid= data.paid, //netCost
            memberId = data.memberId,
            posInvoiceDetails = data.posInvoiceDetailRecord,
            posPayments = data.posPaymentConfigRecord,
            qty = data.qty,
            customerName = data.memberName,
            address1 = data.address1 ,
            address2 = data.address2 ,
        )

        return posInvoice
    }*/
}