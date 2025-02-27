package com.hashmato.retailtouch.presentation.viewModels



import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.ApiUtils.observeResponseNew
import com.hashmato.retailtouch.domain.model.ApiLoaderStateResponse
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSale
import com.hashmato.retailtouch.domain.model.settlement.CreateEditPOSSettlement
import com.hashmato.retailtouch.domain.model.settlement.CreateEditPOSSettlementRequest
import com.hashmato.retailtouch.domain.model.settlement.GetPOSPaymentSummaryRequest
import com.hashmato.retailtouch.domain.model.settlement.PosLocation
import com.hashmato.retailtouch.domain.model.settlement.PosPaymentTypeSummary
import com.hashmato.retailtouch.domain.model.settlement.PosSettlement
import com.hashmato.retailtouch.domain.model.settlement.PosSettlementDetail
import com.hashmato.retailtouch.domain.model.settlement.PosSettlementItem
import com.hashmato.retailtouch.domain.model.settlement.SettlementUIState
import com.hashmato.retailtouch.utils.DateFormatter
import com.hashmato.retailtouch.utils.DateTimeUtils.formatLocalDateWithoutTimeForPrint
import com.hashmato.retailtouch.utils.DateTimeUtils.getCurrentLocalDateTime
import com.hashmato.retailtouch.utils.DateTimeUtils.getEndLocalDateTime
import com.hashmato.retailtouch.utils.DateTimeUtils.getStartLocalDateTime
import com.hashmato.retailtouch.utils.POSInvoiceDefaultTemplate
import com.hashmato.retailtouch.utils.PrinterType
import com.hashmato.retailtouch.utils.TemplateType
import com.hashmato.retailtouch.utils.posSettlementDefaultTemplate
import com.hashmato.retailtouch.utils.printer.PrinterServiceProvider
import com.hashmato.retailtouch.utils.roundTwoDecimalPlaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
                val paymentTypes = async { dataBaseRepository.getPaymentMode() }.await()
                val location = async { dataBaseRepository.getSelectedLocation() }.await()
                val authUser = async { dataBaseRepository.getAuthUser() }.await()
                val pendingSales = async { dataBaseRepository.getPosPendingSales() }.await()
                paymentTypes.collect { payment ->
                    val localList = payment.map { element ->
                        PosPaymentTypeSummary(
                            paymentTypeId = element.id,
                            paymentType = element.name,
                            amount = 0.0
                        )
                    }
                    //println("localList: $localList")
                    _settlementState.update { state -> state.copy(localSettlement = localList) }
                }

                authUser.collect { authDao ->
                    //println("Auth: ${authDao.loginDao}")
                    _settlementState.update { state -> state.copy(tenantId = authDao.tenantId) }
                }

                pendingSales.collectLatest { pendingSale ->
                    //println("pendingSale: $pendingSale")
                    _settlementState.update { state ->
                        state.copy(
                            pendingSales = pendingSale,
                            isLoading = false
                        )
                    }
                }

                location.collect { location ->
                    //println("location: $location")
                    _settlementState.update { state ->
                        state.copy(
                            location = location ?: Location(),
                            currencyCode = preferences.getCurrencySymbol().first(),
                            isLoading = false
                        )
                    }

                    getPosPaymentSummary()
                }

                val salePendingCount = getPendingSaleCount()
                _settlementState.update { it.copy(pendingSaleCount = salePendingCount) }

            } catch (e: Exception) {
                // Handle any other exceptions and set loading to false
                println("UnexpectedError: ${e.message}")
                updateError(error = e.message ?: "Unknown Error", isError = true)
                updateLoader(false)
            }
        }
    }



    private fun getPendingSales() {
        viewModelScope.launch {
            dataBaseRepository.getPosPendingSales().collectLatest { sale ->
                //println("sale:$sale")
                _settlementState.update { it.copy(pendingSales = sale) }
            }
        }
    }

    private fun getPosPaymentSummary() {
        viewModelScope.launch {
            try {
                val location=settlementState.value.location
                networkRepository.getPosPaymentSummary(
                    GetPOSPaymentSummaryRequest(
                        locations = listOf(
                            PosLocation(
                                id = location.locationId.toString(),
                                name = location.name,
                                code = location.code
                            )
                        ),
                        startDate = DateFormatter().formatDateWithTimeForApi(getStartLocalDateTime()),
                        endDate = DateFormatter().formatDateWithTimeForApi(getEndLocalDateTime())
                    )
                ).collect { apiResponse ->
                    observeResponseNew(apiResponse,
                        onLoading = {
                            updateLoader(true)
                        },
                        onSuccess = { apiData ->
                            if (apiData.success) {
                                val stats =
                                    apiData.result?.firstOrNull { it.locationId == location?.locationId }
                                stats?.itemDates?.firstOrNull()?.let { itemDate ->
                                    val floatMoney = itemDate.floatMoney ?: 0.0

                                    if (floatMoney != 0.0) {
                                        itemDate.items?.forEach { item ->
                                            if (item.paymentType.equals(
                                                    "CASH",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                item.amount = item.amount?.minus(floatMoney)
                                            }
                                        }
                                    }
                                    println("itemDate : $itemDate")
                                    _settlementState.update { it.copy(remoteSettlement = stats.itemDates.first()) }
                                }
                                updateLoader(false)
                            } else {
                                updateLoader(false)
                            }
                        },
                        onError = { errorMsg ->
                            updateError(errorMsg, true)
                            updateLoader(false)
                        }
                    )
                }
            } catch (ex: Exception) {
                updateError(ex.message ?: "fetching data failed", true)
                updateLoader(false)
            }
        }
    }

    private fun updateLoader(value: Boolean) {
        _settlementState.update { it.copy(isLoading = value) }
    }

    fun updateError(error: String, isError: Boolean) {
        _settlementState.update { it.copy(isError = isError, errorDesc = error) }
    }

    private fun updateSettlementCall(value: Boolean) {
        _settlementState.update { it.copy(callPosSettlement = value) }
    }

    fun updatePendingSalesMessage(value: Boolean) {
        _settlementState.update { it.copy(showPendingSalesMessage = value) }
    }

    fun updatePendingSalesDialog(value: Boolean) {
        _settlementState.update { it.copy(showPendingSales = value) }
    }

    fun updateSettlementSuccessStatus(isShowDialog: Boolean) {
        _settlementState.update { it.copy(showSuccessDialog = isShowDialog) }
    }

    fun updateAmount(enteredAmount: String, payment: PosPaymentTypeSummary) {
        runCatching {
            val state = settlementState.value
            val amount = enteredAmount.toDouble()
            val updatedPayments = state.localSettlement.map {
                if (it.paymentTypeId == payment.paymentTypeId) {
                    it.copy(amount = amount, enteredAmount = enteredAmount)
                } else {
                    it
                }
            }
            _settlementState.update { state ->
                state.copy(
                    localSettlement = updatedPayments,
                    enteredAmount = amount
                )
            }
        }
    }

    fun updateSyncProgress(value: Boolean) {
        _settlementState.update { it.copy(isSync = value) }
    }

    fun syncPendingSales() {
        try {
            viewModelScope.launch {
                updateLoader(true)
                val state = settlementState.value
                val pendingSaleCount = state.pendingSales.count()
                println("pendingSaleCount :$pendingSaleCount")
                // Launch all calls concurrently
                val jobs = state.pendingSales.map { pendingSale ->
                    async {
                        val posInvoice = deClassifyPendingRecord(
                            pendingSale,
                            state.location,
                            state.tenantId
                        )
                        createUpdatePosInvoice(posInvoice, pendingSaleCount, pendingSale.id)
                    }
                }

                // Wait for all API calls to finish
                jobs.awaitAll()
                // Perform actions after all calls finish
                listenSaleProcessState(state)
            }
        } catch (ex: Exception) {
            updateError("Error saving invoice \n ${ex.message}", true)
            updateLoader(false)
        }
    }

    private suspend fun listenSaleProcessState(state: SettlementUIState) {
        posSaleApiState.collect { mApiStateResponse ->
            when (mApiStateResponse) {
                is ApiLoaderStateResponse.Loader -> {
                    updateLoader(true)
                    updateSyncProgress(true)
                }

                is ApiLoaderStateResponse.Error -> {
                    updateLoader(false)
                    updateSyncProgress(false)
                    //updateError(errorMessage,true)
                }

                is ApiLoaderStateResponse.Success -> {
                    val salePendingCount=getPendingSaleCount()
                    _settlementState.update { it.copy(pendingSaleCount = salePendingCount) }
                    getPendingSales()
                    if (state.callPosSettlement) {
                        executePosSettlement()
                    } else {
                        getPosPaymentSummary()
                        updateSyncProgress(false)
                        updateLoader(false)
                    }
                }

            }
        }
    }

    fun submitPOSSettlement() {
        viewModelScope.launch(Dispatchers.IO) {
            val state = settlementState.value
            if (state.pendingSaleCount > 0) {
                updatePendingSalesMessage(true)
                updateSettlementCall(true)
            } else {
                updateSettlementCall(false)
                updateLoader(true)
                executePosSettlement()
            }
        }
    }

    private suspend fun executePosSettlement() {
        try {
            val posSettlement=prepareSettlementData()
            networkRepository.createOrUpdatePosSettlement(
                CreateEditPOSSettlementRequest(posSettlement = posSettlement)
            ).collect { apiResponse ->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateLoader(true)
                    },
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            deleteSyncData()
                            updateSettlementSuccessStatus(true)
                            updateLoader(false)
                        } else {
                            val errorMsg="Settlement data not submitted."
                            updateError(errorMsg, true)
                            updateLoader(false)
                        }
                    },
                    onError = { errorMsg ->
                        updateError(errorMsg, true)
                        updateLoader(false)
                    }
                )
            }
        } catch (exception: Exception) {
            updateError(exception.message ?: "submit data failed", true)
            updateLoader(false)
        }
    }

    private fun prepareSettlementData() : CreateEditPOSSettlement{
        val state = settlementState.value
        var shortageOrEx = 0.0
        var localTotal = 0.0
        /*state.localSettlement.forEach{pt->
            pt.amount?.let { localAmt ->
                localTotal+=localAmt
                val remoteAmt = state.remoteSettlement?.items
                    ?.find { it.paymentTypeId == pt.paymentTypeId }
                    ?.amount ?: 0.0

                shortageOrEx += (localAmt - remoteAmt)
            }
       }*/

        val posSettlementDetails = state.localSettlement.map { pt ->
            val amount = state.remoteSettlement?.items
                ?.find { it.paymentTypeId == pt.paymentTypeId }?.amount ?: 0.0
            localTotal += pt.amount ?: 0.0
            shortageOrEx += ((pt.amount ?: (0.0 - amount)))
            PosSettlementDetail(
                paymentTypeId = pt.paymentTypeId,
                paymentTypeName = pt.paymentType,
                manualTotal = pt.amount,
                computerTotal = amount,
                shortageOrExcess = pt.amount?.let { localAmt -> localAmt - amount }
            )
        }


        val posSettlement = CreateEditPOSSettlement(
            settlementDate = getCurrentLocalDateTime(),
            locationId = state.location.locationId,
            locationName = state.location.name,
            terminalName = "RetailWeb",
            shift = 1,
            cashOut = 0.0,
            cashIn = 0.0,
            computerTotal = state.remoteSettlement?.itemTotal,
            floatAmount = state.remoteSettlement?.floatMoney,
            manualTotal = localTotal,
            netManualTotal = localTotal,
            shortageOrExcess = shortageOrEx,
            posSettlementDetails = posSettlementDetails
        )

        return posSettlement
    }

    private fun deleteSyncData() {
        viewModelScope.launch {
            _settlementState.update { currentState ->
                val updatedSales= currentState.pendingSales.filter { sale->
                    dataBaseRepository.updateSalesAsSynced(id = sale.id)
                    dataBaseRepository.removeSalesById(id = sale.id)
                    false
                }
                currentState.copy(
                    pendingSales = updatedSales
                )
            }
        }
    }

    fun deletePendingSaleItem(saleItem: PendingSale) {
        viewModelScope.launch {
            _settlementState.update { currentState ->
                dataBaseRepository.removeSalesById(id = saleItem.id)
                val updatedSales = currentState.pendingSales.toMutableList()
                updatedSales.remove(saleItem)
                currentState.copy(
                    pendingSales = updatedSales
                )
            }
        }
    }

    fun connectAndPrintTemplate(){
       viewModelScope.launch {
         try {
             updateLoader(true)
             val posSettlement = prepareSettlementPrintData()
             println("posSettlement :$posSettlement")
             connectAndPrintTemplate(posSettlement)

         }catch (ex:Exception){
             updateError(ex.message?:"print settlement failed",true)
             updateLoader(false)
         }
       }
   }


  private fun prepareSettlementPrintData() : PosSettlement{
      val state = settlementState.value
      val localTotalAmount=state.localSettlement.sumOf { it.amount?:0.0 }

      val posSettlementDetails = state.localSettlement.map { pt ->
          val serverSettlement = state.remoteSettlement?.items
              ?.firstOrNull { it.paymentTypeId == pt.paymentTypeId }?: PosPaymentTypeSummary(paymentType = pt.paymentType, paymentTypeId = pt.paymentTypeId, amount = 0.0)
          val localAmount=pt.amount?:0.0
          val serverAmount=serverSettlement.amount?:0.0
          PosSettlementItem(
              paymentTypeId = serverSettlement.paymentTypeId?:0,
              paymentType = serverSettlement.paymentType?:"",
              localAmount =  roundTwoDecimalPlaces(localAmount),
              serverAmount = roundTwoDecimalPlaces(serverAmount),
              diff = roundTwoDecimalPlaces(localAmount-serverAmount)
          )
      }

      val posSettlement = PosSettlement(
          date = formatLocalDateWithoutTimeForPrint(getCurrentLocalDateTime()),
          cashOut = roundTwoDecimalPlaces(state.remoteSettlement?.cashIn?:0.0),
          cashIn = roundTwoDecimalPlaces(state.remoteSettlement?.cashOut?:0.0),
          floatMoney = roundTwoDecimalPlaces(state.remoteSettlement?.floatMoney?:0.0),
          amount = roundTwoDecimalPlaces(state.remoteSettlement?.amount?:0.0),
          itemTotal = roundTwoDecimalPlaces(state.remoteSettlement?.itemTotal?:0.0),
          netTotal = roundTwoDecimalPlaces(state.remoteSettlement?.netTotal?:0.0),
          subTotal = roundTwoDecimalPlaces(state.remoteSettlement?.subTotal?:0.0),
          localTotal = roundTwoDecimalPlaces(localTotalAmount),
          diff = roundTwoDecimalPlaces(localTotalAmount-(state.remoteSettlement?.itemTotal?:0.0)),
          posSettlementDetails = posSettlementDetails
      )
      return posSettlement
  }

  private suspend fun connectAndPrintTemplate(posSettlement: PosSettlement) {
      var template= posSettlementDefaultTemplate
      sqlRepository.getPrintTemplateByType(type = TemplateType.PosSettlement.toValue()).collect{ mPrintReceiptTemplate->
          mPrintReceiptTemplate?.let{
              template = it.template
              println("template $template")
          }
      }
      dataBaseRepository.getPrinter().collect { printer ->
          if(printer!=null){
              //println("printer_paperSize ${printer.paperSize}")
              val finalTextToPrint = PrinterServiceProvider().getPrintTextForReceiptTemplate(
                  ticket=posSettlement,
                  currencyCode = _settlementState.value.currencyCode,
                  template = template,
                  printers = printer
              )
              //println("finalTextToPrint :\n $finalTextToPrint")

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
              updateLoader(false)
          }else{
              //Show Message that your device is not connected
              updateError(error = "add printer setting", isError = true)
              updateLoader(false)
          }
      }
    }

}