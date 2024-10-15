package com.lfssolutions.retialtouch.data.local


import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.utils.DateTime
import com.lfssolutions.retialtouch.utils.PrefKeys.CURRENCY_SYMBOL
import com.lfssolutions.retialtouch.utils.PrefKeys.CURRENT_TENANT_URL
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_CODE
import com.lfssolutions.retialtouch.utils.PrefKeys.IS_LOGGED_IN
import com.lfssolutions.retialtouch.utils.PrefKeys.LAST_SYNC_TS
import com.lfssolutions.retialtouch.utils.PrefKeys.LOCATION_ID
import com.lfssolutions.retialtouch.utils.PrefKeys.MEMBER_GROUP_SYNC
import com.lfssolutions.retialtouch.utils.PrefKeys.MEMBER_SYNC
import com.lfssolutions.retialtouch.utils.PrefKeys.RE_SYNC_TIMER
import com.lfssolutions.retialtouch.utils.PrefKeys.TENANT_ID
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN_TIME
import com.lfssolutions.retialtouch.utils.PrefKeys.USER_ID
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
class PreferencesImpl(
    settings: Settings
) :PreferencesRepository {


    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()

    @OptIn(ExperimentalSettingsApi::class)
    override suspend fun setBaseURL(result: String) {
        flowSettings.putString(
            key = CURRENT_TENANT_URL,
            value = result
        )
    }

    override suspend fun getBaseURL():Flow<String>{
        return flowSettings.getStringFlow(
            key = CURRENT_TENANT_URL,
            defaultValue = ""
        )
    }

    override suspend fun setToken(result: String) {
        flowSettings.putString(
            key = TOKEN,
            value = result
        )

        flowSettings.putLong(
            key = TOKEN_TIME,
            value = DateTime.getCurrentDateAndTimeInEpochMilliSeconds()
        )
    }

    override suspend fun getToken(): Flow<String> {
        return flowSettings.getStringFlow(
            key = TOKEN,
            defaultValue = ""
        )
    }

    override suspend fun getTokenTime(): Flow<Long> {
        return flowSettings.getLongFlow(
            key = TOKEN_TIME,
            defaultValue = 0
        )
    }

    override suspend fun setUserId(result: Long) {
        flowSettings.putLong(
            key = USER_ID,
            value = result
        )
    }

    override suspend fun getUserId(): Flow<Long> {
        return flowSettings.getLongFlow(
            key = USER_ID,
            defaultValue = 0
        )
    }

    override suspend fun setTenantId(result: Int) {
        flowSettings.putInt(
            key = TENANT_ID,
            value = result
        )
    }

    override fun getTenantId(): Flow<Int> {
        return flowSettings.getIntFlow(
            key = TENANT_ID,
            defaultValue = 0
        )
    }

    override suspend fun setLocationId(result: Int) {
        flowSettings.putInt(
            key = LOCATION_ID,
            value = result
        )
    }

    override fun getLocationId(): Flow<Int> {
        return flowSettings.getIntFlow(
            key = LOCATION_ID,
            defaultValue = 0
        )
    }

    override suspend fun setCurrencySymbol(result: String) {
        flowSettings.putString(
            key = CURRENCY_SYMBOL,
            value = result
        )
    }

    override  fun getCurrencySymbol(): Flow<String> {
        return flowSettings.getStringFlow(
            key = CURRENCY_SYMBOL,
            defaultValue = "$"
        )
    }

    override suspend fun setEmployeeCode(result: String) {
        flowSettings.putString(
            key = EMPLOYEE_CODE,
            value = result
        )
    }

    override fun getEmployeeCode(): Flow<String> {
        return flowSettings.getStringFlow(
            key = EMPLOYEE_CODE,
            defaultValue = ""
        )
    }

    override suspend fun setUserLoggedIn(result: Boolean) {
        flowSettings.putBoolean(
            key = IS_LOGGED_IN,
            value = result
        )
    }

    override  fun getUserLoggedIn(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(
            key = IS_LOGGED_IN,
            defaultValue = false
        )
    }

    override suspend fun setLastSyncTs(result: Long) {
        flowSettings.putLong(
            key = LAST_SYNC_TS,
            value = result
        )
    }

    override fun getLastSyncTs(): Flow<Long> {
        return flowSettings.getLongFlow(
            key = LAST_SYNC_TS,
            defaultValue =0
        )
    }

    override suspend fun setReSyncTimer(result: Int) {
        flowSettings.putInt(
            key = RE_SYNC_TIMER,
            value = result
        )
    }

    override fun getReSyncTime(): Flow<Int> {
        return flowSettings.getIntFlow(
            key = RE_SYNC_TIMER,
            defaultValue = 5
        )
    }

    override suspend fun setMemberSyncGrid(result: String) {
        flowSettings.putString(
            key = MEMBER_SYNC,
            value = result
        )
    }

    override fun getMemberSyncGrid(): Flow<String> {
        return flowSettings.getStringFlow(
            key = MEMBER_SYNC,
            defaultValue = ""
        )
    }

    override suspend fun setMemberGroupSyncGrid(result: String) {
        flowSettings.putString(
            key = MEMBER_GROUP_SYNC,
            value = result
        )

    }

    override fun getMemberGroupSyncGrid(): Flow<String> {
        return flowSettings.getStringFlow(
            key = MEMBER_GROUP_SYNC,
            defaultValue = ""
        )
    }


}