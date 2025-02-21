package com.hashmato.retailtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.model.home.HomeUIState
import com.hashmato.retailtouch.domain.model.home.HomeScreenItem
import com.hashmato.retailtouch.domain.model.login.AuthenticateDao
import com.hashmato.retailtouch.utils.AppIcons
import com.hashmato.retailtouch.utils.AppStrings.cashier
import com.hashmato.retailtouch.utils.AppStrings.drawer
import com.hashmato.retailtouch.utils.AppStrings.logout
import com.hashmato.retailtouch.utils.AppStrings.members
import com.hashmato.retailtouch.utils.AppStrings.payout
import com.hashmato.retailtouch.utils.AppStrings.printer
import com.hashmato.retailtouch.utils.AppStrings.receipt
import com.hashmato.retailtouch.utils.AppStrings.settings
import com.hashmato.retailtouch.utils.AppStrings.settlement
import com.hashmato.retailtouch.utils.AppStrings.stock
import com.hashmato.retailtouch.utils.AppStrings.sync
import com.hashmato.retailtouch.utils.HomeItemId
import com.hashmato.retailtouch.utils.PrinterType
import com.hashmato.retailtouch.utils.printer.PrinterServiceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class HomeViewModel : BaseViewModel(), KoinComponent {

    private val _homeUIState = MutableStateFlow(HomeUIState())
    val homeUIState: StateFlow<HomeUIState> = _homeUIState.asStateFlow()

    fun initialiseEmpScreen(isSplash: Boolean) {
        viewModelScope.launch {
            _homeUIState.update { it.copy(isFromSplash = isSplash,isBlur=isSplash) }
        }
    }

    init {
        viewModelScope.launch {
            _homeUIState.update { state->
                state.copy(isSyncEveryThing = isCallCompleteSync())
            }
        }
    }

     fun prepareHomeData() {
        viewModelScope.launch {
            // Wait for the authentication DAO to initialize
            prepareHomeScreenItems()
            updateHomeState()
        }
    }

    private fun updateHomeState(){
        viewModelScope.launch {
            authUser.collectLatest{user->
                _homeUIState.update { it.copy(authUser = user?: AuthenticateDao()) }
            }
        }
    }

    private fun prepareHomeScreenItems() {
        val iconItems = listOf(
            HomeScreenItem(
                homeItemId = HomeItemId.CASHIER_ID,
                icon = AppIcons.cashierIcon,
                labelResId = cashier
            ),
            HomeScreenItem(
                HomeItemId.MEMBER_ID,
                AppIcons.membershipIcon,
                labelResId = members
            ),
            HomeScreenItem(
                HomeItemId.STOCK_ID,
                AppIcons.stockIcon,
                labelResId = stock
            ),
            HomeScreenItem(
                HomeItemId.RECEIPT_ID,
                AppIcons.receiptIcon,
                labelResId = receipt
            ),
            HomeScreenItem(
                HomeItemId.SYNC_ID,
                AppIcons.syncIcon,
                labelResId = sync
            ),
            HomeScreenItem(
                HomeItemId.SETTLEMENT_ID,
                AppIcons.settlementIcon,
                labelResId = settlement
            ),
            HomeScreenItem(
                HomeItemId.PAYOUT_ID,
                AppIcons.payoutIcon,
                labelResId = payout
            ),
            HomeScreenItem(
                HomeItemId.PRINTER_ID,
                AppIcons.printerIcon,
                labelResId = printer
            ),
            HomeScreenItem(
                HomeItemId.DRAWER_ID,
                AppIcons.drawerIcon,
                labelResId = drawer
            ),
            HomeScreenItem(
                HomeItemId.SETTING_ID,
                AppIcons.settingIcon,
                labelResId = settings
            ),
            HomeScreenItem(
                HomeItemId.LOGOUT_ID,
                AppIcons.logoutIcon,
                labelResId = logout
            )

        )
       viewModelScope.launch {
           _homeUIState.update {
               it.copy(homeItemList = iconItems)
           }
       }
    }

    //open cash drawer module

    fun openDrawerModule(){
        viewModelScope.launch{
            dataBaseRepository.getPrinter().collect { printer ->
                if(printer!=null){
                    PrinterServiceProvider().openCashDrawer(
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
                        textToPrint = ""
                    )
                }
                else{
                    //Show Message that your device is not connected
                    _homeUIState.update { it.copy(isError = true,errorMsg = "add printer setting") }
                }
            }
        }
    }

    fun resetError(){
        viewModelScope.launch {
            _homeUIState.update { it.copy(isError = false, errorMsg = "") }
        }
    }

    // Called when employee logs in
    fun onEmployeeLoggedIn() {
        viewModelScope.launch {
            _homeUIState.update {
                it.copy(hasEmployeeLoggedIn = true, isFromSplash = false,isBlur=false)
            }
        }
    }

    fun updateEmployeeStatus() {
        viewModelScope.launch {
            _homeUIState.update {
                it.copy(hasEmployeeLoggedIn = false, isFromSplash = true,isBlur=true)
            }
        }
    }

    fun updateSyncRotation(id:Int){
        viewModelScope.launch {
            _homeUIState.update { uiState->
                val updatedList = uiState.homeItemList.map { item ->
                    if (item.homeItemId == id)
                        item.copy(isSyncRotate = true)
                    else
                        item
                }
                uiState.copy(homeItemList = updatedList)
            }
        }
    }


    fun updateExitFormDialogState(value:Boolean){
        viewModelScope.launch {
            _homeUIState.update { uiState ->
                uiState.copy(showExitConfirmationDialog = value)
            }
        }
    }

    fun stopSyncRotation(value:Boolean){
        viewModelScope.launch {
            _homeUIState.update { uiState ->
                val updatedList = uiState.homeItemList.map { item ->
                    item.copy(isSyncRotate = value) // Update the isSyncRotate flag based on the passed value
                }
                uiState.copy(homeItemList = updatedList)
            }
        }
    }

    fun updateSyncEverythingState(value:Boolean){
        viewModelScope.launch {
            _homeUIState.update { uiState ->
                uiState.copy(isSyncEveryThing = value)
            }
        }
    }
}