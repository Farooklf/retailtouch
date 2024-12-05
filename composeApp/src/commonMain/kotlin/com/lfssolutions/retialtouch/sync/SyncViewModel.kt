package com.lfssolutions.retialtouch.sync

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.SqlPreference
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllResponse
import com.lfssolutions.retialtouch.domain.model.sync.SyncItem
import com.lfssolutions.retialtouch.domain.model.sync.SyncResult
import com.lfssolutions.retialtouch.domain.repositories.NetworkRepository
import com.lfssolutions.retialtouch.utils.AppConstants.CATEGORY
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER_GROUP
import com.lfssolutions.retialtouch.utils.AppConstants.MENU
import com.lfssolutions.retialtouch.utils.AppConstants.PAYMENT_TYPE
import com.lfssolutions.retialtouch.utils.AppConstants.PRODUCT
import com.lfssolutions.retialtouch.utils.AppConstants.PROMOTION
import com.lfssolutions.retialtouch.utils.AppConstants.SYNC_CHANGES_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.DateTime.parseDateFromApi
import com.lfssolutions.retialtouch.utils.DateTime.parseDateFromApiString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.minutes

class SyncViewModel : ViewModel() , KoinComponent {

    private val networkRepository: NetworkRepository by inject()
    private val preferences: PreferencesRepository by inject()
    private val sqlPreference: SqlPreference by inject()

    private val _syncDataState = MutableStateFlow(SyncDataState())
    val syncDataState: StateFlow<SyncDataState> = _syncDataState.asStateFlow()

    private var syncJob: Job? = null  // To manage the periodic sync job
    var count =0
    private val syncFlags = mutableStateMapOf(
        MEMBER to false,
        MEMBER_GROUP to false,
        PRODUCT to false,
        CATEGORY to false,
        MENU to false,
        PROMOTION to false,
        PAYMENT_TYPE to false
    )

    init {
        // Read reSync time from storage and start the sync timer
        viewModelScope.launch {
            _syncDataState.update {
                it.copy(reSyncTime=readReSyncTimer())
            }
            startPeriodicSync()
        }
    }

    private suspend fun writeReSyncTimer(time:Int) {
        preferences.setReSyncTimer(time)
    }

    // Load from preferences
    private suspend fun readReSyncTimer(): Int {
        return preferences.getReSyncTime().first()
    }

    // Update reSync time and restart timer
    fun updateReSyncTime(time: Int) {
        if (time > 0) {
            viewModelScope.launch {
                writeReSyncTimer(time) // Persist new time to storage
                _syncDataState.update {it.copy(reSyncTime = time)}
                startPeriodicSync()  // Restart timer with new interval
            }
        }
    }

    // Start the periodic reSync timer with the given time
    private fun startPeriodicSync() {
        syncJob?.cancel()  // Cancel existing sync job, if any
        syncJob = viewModelScope.launch(Dispatchers.Default) {
            val timeInMinutes=_syncDataState.value.reSyncTime
            while (isActive) {
                reSyncItems(true)  // Perform the sync silently
                delay(timeInMinutes.minutes.inWholeMilliseconds)
            }
        }
    }

    private fun reSyncItems(silent :Boolean = false) {
        if (!silent) {
            updateSyncProgress(true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                launch { getUnSyncChanges() }.join()
                for ((key, value) in syncFlags) {
                    if (value) {
                        updateSyncStatus("Syncing $key Data")
                        when (key) {
                            MEMBER -> syncMember()
                            MEMBER_GROUP -> syncMember()
                            PRODUCT -> syncMember()
                            CATEGORY -> syncMember()
                            MENU -> syncMember()
                            PROMOTION -> syncMember()
                            PAYMENT_TYPE -> syncMember()
                        }
                        updateSyncCount() // Increment after each sync
                    }
                }
            }
            catch (e: Exception) {
                handleError(errorTitle = SYNC_CHANGES_ERROR_TITLE, errorMsg = e.message ?: "Unknown error")
            } finally {
                updateSyncProgress(false)
            }
        }
    }

    private suspend fun getUnSyncChanges(){
        try {
            networkRepository.syncAllApis(getBasicRequest()).collectLatest { apiResponse->
                observeSyncChanges(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleError(SYNC_CHANGES_ERROR_TITLE,error)
        }
    }

    private fun observeSyncChanges(apiResponse: RequestState<SyncAllResponse>) {
        println("sync changes insertion")
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    updateSyncerGuid(apiData.result)
                    getReSyncableItems()
                }
            },
            onError = {
                    errorMsg ->
                handleError(SYNC_CHANGES_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun getReSyncableItems() {
        viewModelScope.launch(Dispatchers.IO) {
            _syncDataState.value.syncerGuid.items.forEach { element ->
                when (element.name.uppercase()) {
                    "MEMBER" ->{
                        updateSyncStatus("Syncing Member")
                        syncFlags["MEMBER"] = getMemberSyncGrid() != element.syncerGuid
                    }
                    "MEMBERGROUP" -> {
                        updateSyncStatus("Syncing Member Group")
                        syncFlags["MEMBERGROUP"] = getMemberGroupSyncGrid() != element.syncerGuid
                    }
                    "PRODUCT" -> {
                        updateSyncStatus("Syncing Inventory")
                        syncFlags["PRODUCT"] = getProductsSyncGrid() != element.syncerGuid
                    }
                    "CATEGORY" -> {
                        updateSyncStatus("Syncing Categories")
                        syncFlags["CATEGORY"] = getCategorySyncGrid() != element.syncerGuid
                    }
                    "MENU" -> {
                        updateSyncStatus("Syncing Menu")
                        syncFlags["MENU"] = getMenuSyncGrid() != element.syncerGuid
                    }
                    "PROMOTION" -> {
                        updateSyncStatus("Syncing Promotions")
                        syncFlags["PROMOTION"] = getPromotionsSyncGrid() != element.syncerGuid
                    }
                    "PAYMENTTYPE" -> {
                        updateSyncStatus("Syncing Payment Type")
                        syncFlags["PAYMENTTYPE"] = getPaymentTypeSyncGrid() != element.syncerGuid
                    }
                }
            }
        }
    }


    private suspend fun syncMember(){
        try {
            networkRepository.getMembers(getBasicTenantRequest()).collectLatest {apiResponse->
                observeMembers(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleError(MEMBER_ERROR_TITLE,error)
        }
    }

    private fun observeMembers(apiResponse: RequestState<MemberResponse>) {
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    insertMembers(apiData)
                    updateSyncGrid(MEMBER)
                }
            },
            onError = {
                    errorMsg ->
                handleError(MEMBER_ERROR_TITLE,errorMsg)
            }
        )
    }


    private fun insertMembers(
        response: MemberResponse
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    sqlPreference.deleteMembers()//clear exist members
                    response.result?.items?.forEach {item->
                        //val updatedItem= item.copy(joinDate = item.joinDate.parseDateFromApiString())
                        val dao= MemberDao(
                            memberId = item.id.toLong(),
                            rowItem = item.copy(joinDate = item.joinDate.parseDateFromApi()),
                        )
                        sqlPreference.insertMembers(dao)
                    }
                }
            }catch (ex:Exception){

            }
        }
    }

    private fun updateSyncGrid(name: String): SyncItem {
        with(_syncDataState.value){
            return syncerGuid.items.firstOrNull{ it.name.uppercase() == name }
                ?: SyncItem(name = name, syncerGuid = "", id = 0)
        }
    }

    private fun updateSyncerGuid(mSyncResult: SyncResult) {
        _syncDataState.update { it.copy(syncerGuid = mSyncResult) }
    }

    private fun updateSyncProgress(syncStatus: Boolean) {
        _syncDataState.update { it.copy(syncInProgress = syncStatus) }
    }

    private fun updateSyncStatus(syncStatus: String) {
        _syncDataState.update { it.copy(syncProgressStatus = syncStatus) }
    }

    // Increment the sync count
    private fun updateSyncCount() {
        _syncDataState.update { it.copy(syncCount = it.syncCount + 1) }
    }

    // Handle any sync errors
    private fun handleError(errorTitle: String, errorMsg: String) {
        _syncDataState.update { it.copy(syncError = true, syncErrorInfo = "$errorTitle \n $errorMsg") }
    }


    private suspend fun getBasicRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first(),
        locationId = preferences.getLocationId().first(),
    )

    fun getBasicRequest(id:Int) = BasicApiRequest(
        id =id
    )

    private suspend fun getBasicTenantRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first()
    )

    private suspend fun getMemberSyncGrid() : String{
        return preferences.getMemberSyncGrid().first()
    }

    private suspend fun getMemberGroupSyncGrid() : String{
        return preferences.getMemberGroupSyncGrid().first()
    }

    private suspend fun getProductsSyncGrid() : String{
        return preferences.getMemberGroupSyncGrid().first()
    }

    private suspend fun getCategorySyncGrid() : String{
        return preferences.getMemberGroupSyncGrid().first()
    }

    private suspend fun getMenuSyncGrid() : String{
        return preferences.getMemberGroupSyncGrid().first()
    }

    private suspend fun getPromotionsSyncGrid() : String{
        return preferences.getMemberGroupSyncGrid().first()
    }

    private suspend fun getPaymentTypeSyncGrid() : String{
        return preferences.getMemberGroupSyncGrid().first()
    }


    private fun stopPeriodicSync() {
        syncJob?.cancel()  // Stop the job
    }

    override fun onCleared() {
        super.onCleared()
        stopPeriodicSync()  // Cancel sync job when ViewModel is cleared
    }
}