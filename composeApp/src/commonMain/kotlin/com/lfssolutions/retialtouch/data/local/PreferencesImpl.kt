package com.lfssolutions.retialtouch.data.local


import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.utils.DateFormatter
import com.lfssolutions.retialtouch.utils.PrefKeys.CURRENCY_SYMBOL
import com.lfssolutions.retialtouch.utils.PrefKeys.CURRENT_TENANT_URL
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_CODE
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_STATE
import com.lfssolutions.retialtouch.utils.PrefKeys.IS_LOGGED_IN
import com.lfssolutions.retialtouch.utils.PrefKeys.LAST_SYNC_TS
import com.lfssolutions.retialtouch.utils.PrefKeys.LOCATION_ID
import com.lfssolutions.retialtouch.utils.PrefKeys.MEMBER_GROUP_SYNC
import com.lfssolutions.retialtouch.utils.PrefKeys.MEMBER_SYNC
import com.lfssolutions.retialtouch.utils.PrefKeys.NETWORK_CONFIG
import com.lfssolutions.retialtouch.utils.PrefKeys.NETWORK_CONFIG_DEFAULT_VALUE
import com.lfssolutions.retialtouch.utils.PrefKeys.NEXT_SALE_INVOICE_NUMBER
import com.lfssolutions.retialtouch.utils.PrefKeys.PRINTER_ENABLE
import com.lfssolutions.retialtouch.utils.PrefKeys.RE_SYNC_TIMER
import com.lfssolutions.retialtouch.utils.PrefKeys.SALE_INVOICE_LENGTH
import com.lfssolutions.retialtouch.utils.PrefKeys.SALE_INVOICE_PREFIX
import com.lfssolutions.retialtouch.utils.PrefKeys.TENANT_ID
import com.lfssolutions.retialtouch.utils.PrefKeys.TERMINAL_CODE
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN_TIME
import com.lfssolutions.retialtouch.utils.PrefKeys.USER_ID
import com.lfssolutions.retialtouch.utils.PrefKeys.USER_NAME
import com.lfssolutions.retialtouch.utils.PrefKeys.USER_PASSWORD
import com.lfssolutions.retialtouch.utils.PrefKeys.USER_TENANT_NAME
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

    override suspend fun setOverlayState(result: Boolean) {
        flowSettings.putBoolean(
            key = EMPLOYEE_STATE,
            value = result
        )
    }

    override suspend fun getOverlayState(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(
            key = EMPLOYEE_STATE,
            defaultValue = false
        )
    }


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

    override suspend fun setNextSalesInvoiceNumber(result: String) {
        flowSettings.putString(
            key = NEXT_SALE_INVOICE_NUMBER,
            value = result
        )
    }

    override suspend fun getNextSalesInvoiceNumber(): Flow<String> {
        return flowSettings.getStringFlow(
            key = NEXT_SALE_INVOICE_NUMBER,
            defaultValue = ""
        )
    }

    override suspend fun setSalesInvoicePrefix(result: String) {
        flowSettings.putString(
            key = SALE_INVOICE_PREFIX,
            value = result
        )
    }

    override suspend fun getSalesInvoicePrefix(): Flow<String> {
        return flowSettings.getStringFlow(
            key = SALE_INVOICE_PREFIX,
            defaultValue = ""
        )
    }

    override suspend fun setSalesInvoiceNoLength(result: Int) {
        flowSettings.putInt(
            key = SALE_INVOICE_LENGTH,
            value = result
        )
    }

    override suspend fun getSalesInvoiceNoLength(): Flow<Int> {
        return flowSettings.getIntFlow(
            key = SALE_INVOICE_LENGTH,
            defaultValue = 0
        )
    }

    override suspend fun setToken(result: String) {
        flowSettings.putString(
            key = TOKEN,
            value = result
        )

        flowSettings.putLong(
            key = TOKEN_TIME,
            value = DateFormatter().getCurrentDateAndTimeInEpochMilliSeconds()
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

    override suspend fun setUserName(result: String) {
        flowSettings.putString(
            key = USER_NAME,
            value = result
        )
    }

    override suspend fun getUserName(): Flow<String> {
        return flowSettings.getStringFlow(
            key = USER_NAME,
            defaultValue = ""
        )
    }

    override suspend fun setUserPass(result: String) {
        flowSettings.putString(
            key = USER_PASSWORD,
            value = result
        )
    }

    override suspend fun getUserPass(): Flow<String> {
        return flowSettings.getStringFlow(
            key = USER_PASSWORD,
            defaultValue = ""
        )
    }

    override suspend fun setTenancyName(result: String) {
        flowSettings.putString(
            key = USER_TENANT_NAME,
            value = result
        )
    }

    override suspend fun getTenancyName(): Flow<String> {
        return flowSettings.getStringFlow(
            key = USER_TENANT_NAME,
            defaultValue = ""
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

    override suspend fun setIsPrinterEnabled(result: Boolean) {
        flowSettings.putBoolean(
            key = PRINTER_ENABLE,
            value = result
        )
    }

    override fun getIsPrinterEnabled(): Flow<Boolean> {
        return flowSettings.getBooleanFlow(
            key = PRINTER_ENABLE,
            defaultValue = false
        )
    }

    override suspend fun setTerminalCode(result: String) {
        flowSettings.putString(
            key = TERMINAL_CODE,
            value = result
        )
    }

    override fun getTerminalCode(): Flow<String> {
        return flowSettings.getStringFlow(
            key = TERMINAL_CODE,
            defaultValue = ""
        )
    }

    override suspend fun setNetworkConfig(result: String) {
        flowSettings.putString(
            key = NETWORK_CONFIG,
            value = result
        )
    }

    override fun getNetworkConfig(): Flow<String> {
        return flowSettings.getStringFlow(
            key = NETWORK_CONFIG,
            defaultValue = NETWORK_CONFIG_DEFAULT_VALUE
        )
    }


}