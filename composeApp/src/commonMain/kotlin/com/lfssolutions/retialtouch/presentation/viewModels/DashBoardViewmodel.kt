package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponse
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.dashBoard.DashBoardUIState
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceResponse
import com.lfssolutions.retialtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionResponse
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


class DashBoardViewmodel() : BaseViewModel(), KoinComponent {

    // Set up UI state using product
    private val _dashBoardUIState= MutableStateFlow(DashBoardUIState())
    val dashBoardUIState: StateFlow<DashBoardUIState> = _dashBoardUIState.asStateFlow()



    // Called when employee logs in
    fun onEmployeeLoggedIn() {
        viewModelScope.launch {
            _dashBoardUIState.update {
                it.copy(hasEmployeeLoggedIn = true, isShowEmployeeScreen = false, isDashBoardBlur = false)
            }
        }
    }

    // Reset the employee login state (called when app restarts)
    fun resetLoginState() {
        viewModelScope.launch {
            _dashBoardUIState.update {
                it.copy(isShowEmployeeScreen = true, isDashBoardBlur = true, hasEmployeeLoggedIn = false)
            }
        }
    }


    fun updateEmployeeScreen(isDashBlur: Boolean, isEmployeeScreen:Boolean) {
        viewModelScope.launch {
            _dashBoardUIState.update { it.copy(isShowEmployeeScreen = isEmployeeScreen, isDashBoardBlur = isDashBlur) }
        }
    }


    private fun getNextPOSSaleInvoice(){
        if(!isNEXTPOSSaleDbInserted.value){
            viewModelScope.launch {
                println("next pos api")
                val response=networkRepository.getNextPOSSaleInvoice(getBasicRequest())
                observeNextPOSSaleInvoice(response)
            }
        }
    }

    private fun getPOSInvoice(){
        if(!isPOSInvoiceDbInserted.value){
            viewModelScope.launch {
                println("pos invoice api")
                val apiResponse=networkRepository.getPOSInvoice(
                    POSInvoiceRequest(
                        locationId =  getLocationId(),
                        skipCount =  0,
                        maxResultCount = 1,
                        sorting = "Id",
                    )
                )
                observePOSInvoice(apiResponse)
            }
        }
    }



    private fun getProductLocation(){
        if(!isProductLocationDbInserted.value){
            viewModelScope.launch {
                println("product location api")
                val apiResponse=networkRepository.getProductLocation(getBasicRequest())
                observeProductLocation(apiResponse)
            }
        }
    }

    private fun getPaymentType(){
        if(!isPaymentTypeDbInserted.value){
            viewModelScope.launch {
                println("payment type api")
                val apiResponse=  networkRepository.getPaymentTypes(getBasicRequest())
                observePaymentType(apiResponse)
            }
        }
    }


    private fun getPromotions(){
        if(!isPromotionDbInserted.value){
            viewModelScope.launch {
                println("promotion api")
                val apiResponse=  networkRepository.getPromotions(PromotionRequest(tenantId = getTenantId()))
                observePromotions(apiResponse)
            }
        }
    }

    private fun getProductBarCode(){
        if(!isBarCodeDbInserted.value){
            viewModelScope.launch {
                println("barcode api")
                val apiResponse=  networkRepository.getProductBarCode(getBasicRequest())
                observeProductBarCode(apiResponse)
            }
        }
    }

    private fun syncAllApis(){
        if(!isSyncDbInserted.value){
            viewModelScope.launch {
                println("sync api")
                val apiResponse=  networkRepository.syncAllApis(getBasicRequest())
                observeSync(apiResponse)
            }
        }
    }



    private suspend fun observeNextPOSSaleInvoice(apiResponse: Flow<RequestState<NextPOSSaleInvoiceNoResponse>>)
    {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _nextPOSSaleResponse.update { apiData }
                    insertNextPosSale(apiData)

                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    private suspend fun observePOSInvoice(apiResponse: Flow<RequestState<POSInvoiceResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _posInvoiceResponse.update { apiData }
                    insertPosInvoice(apiData)
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    private suspend fun observePaymentType(apiResponse: Flow<RequestState<PaymentTypeResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _paymentTypeResponseResponse.update { apiData }
                    insertPaymentType(apiData)
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    private suspend fun observeProductWithTax(apiResponse: Flow<RequestState<ProductWithTaxByLocationResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _productTaxResponse.update { apiData }
                    insertProductWithTax(apiData)
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    private suspend fun observeProductLocation(apiResponse: Flow<RequestState<ProductLocationResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _productLocationResponse.update { apiData }
                    insertProductLocation(apiData)
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    suspend fun observeMembers(apiResponse: Flow<RequestState<MemberResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _membersResponse.update { apiData }
                    insertMembers(apiData)
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    suspend fun observeMemberGroup(apiResponse: Flow<RequestState<MemberGroupResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _memberGroupResponse.update { apiData }
                    insertMemberGroup(apiData)
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    suspend fun observePromotions(apiResponse: Flow<RequestState<PromotionResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _promotionResponse.update { apiData }
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    suspend fun observeProductBarCode(apiResponse: Flow<RequestState<ProductBarCodeResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _productBarCodeGroupResponse.update { apiData }
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

    private suspend fun observeSync(apiResponse: Flow<RequestState<SyncAllResponse>>) {
        observeResponse(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    _syncResponse.update { apiData }
                    insertSyncAll(apiData)
                }
            },
            onError = {
                    errorMsg -> println("FETCHING FAILED $errorMsg")
            }
        )
    }

}