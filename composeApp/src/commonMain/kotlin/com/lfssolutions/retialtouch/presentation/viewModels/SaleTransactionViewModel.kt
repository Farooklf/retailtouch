package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.MemberType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleTransactionState
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.utils.DateTime.parseDateStringToMillis
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
import org.koin.core.component.KoinComponent


class   SaleTransactionViewModel : BaseViewModel(), KoinComponent {

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
          dataBaseRepository.getSaleTransaction().collect{sale->
              _screenState.update { it.copy(saleTransaction = sale) }
              println("SaleList : $sale")
              filterSales()
          }
      }
    }

    fun filterSales() {
        viewModelScope.launch {
            _screenState.update { state->
                // Parse start date and end date if they exist
                //var filteredSales = state.saleTransaction

                val startMillis = parseDateStringToMillis(state.startDate)
                val endMillis = parseDateStringToMillis(state.endDate)

                /*if (startMillis != null) {
                    filteredSales = filteredSales.filter { sale ->
                        !sale.date.isNullOrEmpty() && parseDateStringToMillis(sale.date)!! > startMillis - 86400000 // Subtract one day in milliseconds
                    }
                }

                if (endMillis != null) {
                    filteredSales = filteredSales.filter { sale ->
                        !sale.date.isNullOrEmpty() && parseDateStringToMillis(sale.date)!! < endMillis + 86400000 // Subtract one day in milliseconds
                    }
                }*/

                // Filter the sales based on the date range and other conditions
                val filteredSales = state.saleTransaction.filter { sale ->
                    val saleMillis = parseDateStringToMillis(sale.date)

                    // Apply any condition with OR logic
                    val isDateValid = saleMillis != null && saleMillis in (startMillis ?: Long.MIN_VALUE)..(endMillis ?: Long.MAX_VALUE)
                    val isMemberValid = sale.memberId == state.member.memberId
                    val isTypeValid = sale.type == state.type.id
                    val isStatusValid = sale.status == state.status.id

                    // Include the sale if any one of the conditions is met
                    isDateValid || isMemberValid || isTypeValid || isStatusValid
                }

                // If filtered list is empty, return the entire sales list
                val resultSales = filteredSales.ifEmpty {
                    state.saleTransaction  // return all sales if no filter conditions match
                }
                val sortedSales = resultSales.sortedByDescending { sale -> parseDateStringToMillis(sale.date) }
                println("sortedSales : $sortedSales")
                // Sort the filtered list by date (descending order)
                state.copy(saleTransaction = sortedSales)
            }
        }
    }



    private fun updateLoader(value:Boolean){
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = value) }
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
            _screenState.update { it.copy(type = type)}
        }
    }

    fun onSelectedStatus(status: StatusType){
        viewModelScope.launch {
            _screenState.update { it.copy(status = status)}
        }
    }

    fun onSelectedMember(member: MemberType){
        viewModelScope.launch {
            _screenState.update { it.copy(member = member)}
        }
    }

    fun updateDatePickerDialog(newValue:Boolean,isFromDateValue:Boolean){
        _screenState.update { state->state.copy(isDatePickerDialog = newValue, isFromDate = isFromDateValue) }
    }
}