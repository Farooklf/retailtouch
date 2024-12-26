package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.home.HomeUIState
import com.lfssolutions.retialtouch.domain.model.home.HomeScreenItem
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.AppStrings.cashier
import com.lfssolutions.retialtouch.utils.AppStrings.drawer
import com.lfssolutions.retialtouch.utils.AppStrings.logout
import com.lfssolutions.retialtouch.utils.AppStrings.members
import com.lfssolutions.retialtouch.utils.AppStrings.payout
import com.lfssolutions.retialtouch.utils.AppStrings.printer
import com.lfssolutions.retialtouch.utils.AppStrings.receipt
import com.lfssolutions.retialtouch.utils.AppStrings.settings
import com.lfssolutions.retialtouch.utils.AppStrings.settlement
import com.lfssolutions.retialtouch.utils.AppStrings.stock
import com.lfssolutions.retialtouch.utils.AppStrings.sync
import com.lfssolutions.retialtouch.utils.HomeItemId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    init {
        syncEveryThing()
    }

    fun initialiseEmpScreen(isSplash: Boolean) {
        viewModelScope.launch {
            _homeUIState.update { it.copy(isFromSplash = isSplash,isBlur=isSplash) }
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
                uiState.copy(homeItemList = updatedList,isSync=false)
            }
        }
    }

    fun onSyncClick(){

        viewModelScope.launch(Dispatchers.IO) {
            // Prepare all API calls in parallel using async
            val deferredResults = listOf(
                //Employee API
                async {
                    getEmployees()
                },
                //Employee Role API
                async {
                    getEmployeeRole()
                },

                //MenuCategory API
                async {
                    //syncMenu()
                }
            )

            // Await all tasks (this will wait for all the parallel jobs to complete)
            deferredResults.awaitAll()
            stopSyncRotation(false)
            println("apiCall is end")
        }
    }

    private fun stopSyncRotation(value:Boolean){
        viewModelScope.launch {
            _homeUIState.update { uiState ->
                val updatedList = uiState.homeItemList.map { item ->
                    item.copy(isSyncRotate = value) // Update the isSyncRotate flag based on the passed value
                }
                uiState.copy(homeItemList = updatedList, isSync = value)
            }
        }
    }
}