package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.MemberType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleTransactionState
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.utils.NumberFormatting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent



class SaleTransactionViewModel : BaseViewModel(), KoinComponent {

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
                    delay(2000)
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
            /*try {
                // Load data from multiple databases concurrently
                //val data1Deferred = async { getCurrencySymbol() }
                val data3Deferred = async { getDeliveryType() }
                val data4Deferred = async { getStatusType() }
                val data4Deferred = async {  dataBaseRepository.getMember()}

                // Await all results
                //val currencySymbol = data1Deferred.await()
                val deliveryType = data3Deferred.await()
                val statusType = data4Deferred.await()

                // Update your state directly with responses
                deliveryType.collectLatest { deliveryList ->
                    _screenState.update {
                        it.copy(typeList = deliveryList, type = deliveryList[0])
                    }
                }

                statusType.collectLatest { statusList ->
                    _screenState.update {
                        it.copy(statusList = statusList , status = statusList[0])
                    }
                }

                updateLoader(false)

            } catch (e: Exception) {
                // Handle errors, ensuring loading state is false
                updateLoader(false)
            }*/
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

    fun syncTransaction(){
        viewModelScope.launch {
            updateTransactionLoading(true)
            updateLoader(true)
            updateSales()
        }
    }

    fun syncPendingSales() {
        viewModelScope.launch {
            screenState.value.let { state->
                dataBaseRepository.getAllPendingSaleRecordsCount().collectLatest { pendingCount->
                    if(pendingCount>0){
                        updatePendingLoading(!state.isSalePendingSync)
                        dataBaseRepository.getPosPendingSales().collect{ response->
                            response.forEach { data->
                                val posInvoice= PosInvoice(
                                    id = data.id,
                                    tenantId = data.tenantId,
                                    employeeId = data.employeeId,
                                    locationId=data.locationId,
                                    locationCode = data.locationCode,
                                    terminalId = data.locationId,
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
                                    pendingInvoices = pendingCount
                                )
                                //executePosPayment(posInvoice)
                            }
                        }
                    }
                }
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

    private suspend fun getDeliveryType() : Flow<List<DeliveryType>> {
        return flow {
            val deliveryTypes = listOf(
                DeliveryType(id = 0, name = "All"),
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
                StatusType(id = 0, name = "All"),
                StatusType(id = 1, name = "Pending"),
                StatusType(id = 2, name = "Delivered"),
                StatusType(id = 3, name = "Self Connected"),
                StatusType(id = 4, name = "Returned")
            )
            emit(statusType)  // Emit the list as a Flow
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
            println("selectedDate $newVal")
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


}