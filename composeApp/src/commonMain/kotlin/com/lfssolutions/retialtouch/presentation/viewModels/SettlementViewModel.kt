package com.lfssolutions.retialtouch.presentation.viewModels



import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.model.settlement.GetPOSPaymentSummaryRequest
import com.lfssolutions.retialtouch.domain.model.settlement.PosLocation
import com.lfssolutions.retialtouch.domain.model.settlement.PosPaymentTypeSummary
import com.lfssolutions.retialtouch.domain.model.settlement.SettlementUIState
import com.lfssolutions.retialtouch.utils.DateFormatter
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getEndLocalDateTime
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getStartLocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class SettlementViewModel : BaseViewModel(), KoinComponent {

    private val _settlementState = MutableStateFlow(SettlementUIState())
    val settlementState: StateFlow<SettlementUIState> = _settlementState.asStateFlow()


    fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            updateLoader(true)
            try {
                val paymentTypes = async { dataBaseRepository.getPaymentMode()}.await()
                val location = location.value
                println("location: $location")
                paymentTypes.collect{payment->
                    val localList = payment.map {element->
                        PosPaymentTypeSummary(paymentTypeId = element.id, paymentType = element.name, amount = 0.0)
                    }
                    println("localList: $localList")
                    _settlementState.update { state->state.copy(localSettlement = localList, isLoading = false) }
                }
            } catch (e: Exception) {
                // Handle any other exceptions and set loading to false
                println("UnexpectedError: ${e.message}")
                updateError(error = e.message?:"Unknown Error", isError = true)
                updateLoader(false)
            }
        }
    }

    fun prepareSettlement(){
        viewModelScope.launch(Dispatchers.IO) {
            updateLoader(true)
            //val localList:MutableList<PosPaymentTypeSummary> = mutableListOf()
            dataBaseRepository.getPaymentType().collect{payment->
              val localList = payment.map {element->
                  PosPaymentTypeSummary(paymentTypeId = element.id, paymentType = element.name, amount = 0.0)
              }
                updateLocalSettlement(localList)
            }
        }
    }

    fun getPosPaymentSummary(){
        viewModelScope.launch {
            try {
                val state = settlementState.value
                val location = location.value
              networkRepository.getPosPaymentSummary(
                  GetPOSPaymentSummaryRequest(
                      locations = listOf(PosLocation(id =location?.locationId.toString(), name = location?.name, code = location?.code)),
                      startDate = DateFormatter().formatDateWithTimeForApi(getStartLocalDateTime()),
                      endDate = DateFormatter().formatDateWithTimeForApi(getEndLocalDateTime())
                  )
              ).collect{apiResponse->
                  observeResponseNew(apiResponse,
                      onLoading = {
                          updateLoader(true)
                      },
                      onSuccess = { apiData ->
                          if(apiData.success){
                                val stats=apiData.result?.firstOrNull{ it.locationId == location?.locationId }
                                stats?.itemDates?.firstOrNull()?.let { itemDate ->
                                  val floatMoney = itemDate.floatMoney ?: 0.0

                                  if (floatMoney != 0.0) {
                                      itemDate.items?.forEach { item ->
                                          if (item.paymentType.equals("cash", ignoreCase = true)) {
                                              item.amount = item.amount?.minus(floatMoney)
                                          }
                                      }
                                  }
                                 println("itemDate : $itemDate")
                              }
                              _settlementState.update { it.copy(remoteSettlement=stats?.itemDates?.first()) }

                               println("itemDate : ${stats?.itemDates?.first()}")
                              updateLoader(false)
                          }else{
                              updateLoader(false)
                          }
                      },
                      onError = { errorMsg ->
                          updateError(errorMsg,true)
                          updateLoader(false)
                      }
                  )
              }
            }catch (ex:Exception){
                updateError(ex.message?:"fetching data failed",true)
                updateLoader(false)
            }
        }
    }

    private fun updateLoader(value:Boolean){
        _settlementState.update { it.copy(isLoading = value) }
    }

    fun updateError(error:String,isError:Boolean){
        _settlementState.update { it.copy(isError = isError, errorDesc = error) }
    }

    private fun updateLocalSettlement(localList: List<PosPaymentTypeSummary>) {
        _settlementState.update { it.copy(localSettlement = localList) }
    }

    fun updateAmount(enteredAmount: String,payment:PosPaymentTypeSummary) {
        runCatching {
            val state=settlementState.value
            val amount = enteredAmount.toDouble()
            val updatedPayments = state.localSettlement.map {
                if (it.paymentTypeId == payment.paymentTypeId) {
                    it.copy(amount= amount, enteredAmount = amount)
                } else {
                    it
                }
            }
            _settlementState.update { state -> state.copy(localSettlement = updatedPayments) }
        }
    }

}