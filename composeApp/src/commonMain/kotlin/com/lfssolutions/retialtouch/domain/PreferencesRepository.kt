package com.lfssolutions.retialtouch.domain


import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    suspend fun setOverlayState(result: Boolean)
    suspend fun getOverlayState():Flow<Boolean>

    suspend fun setBaseURL(result: String)
    suspend fun getBaseURL():Flow<String>

    suspend fun setNextSalesInvoiceNumber(result: String)
    suspend fun getNextSalesInvoiceNumber():Flow<String>

    suspend fun setSalesInvoicePrefix(result: String)
    suspend fun getSalesInvoicePrefix():Flow<String>

    suspend fun setSalesInvoiceNoLength(result: Int)
    suspend fun getSalesInvoiceNoLength():Flow<Int>

    suspend fun setToken(result: String)
    suspend fun getToken(): Flow<String>
    suspend fun getTokenTime(): Flow<Long>

    suspend fun setUserId(result: Long)
    suspend fun getUserId(): Flow<Long>

    suspend fun setTenantId(result: Int)
    fun getTenantId(): Flow<Int>

    suspend fun setUserName(result: String)
    suspend fun getUserName(): Flow<String>

    suspend fun setUserPass(result: String)
    suspend fun getUserPass(): Flow<String>

    suspend fun setLocation(result: String)
    suspend fun getLocation(): Flow<String>

    suspend fun setTenancyName(result: String)
    suspend fun getTenancyName(): Flow<String>

    suspend fun setLocationId(result: Int)
    fun getLocationId(): Flow<Int>

    suspend fun setCurrencySymbol(result: String)
    fun getCurrencySymbol():Flow<String>

    suspend fun setEmployeeCode(result: String)
    fun getEmployeeCode(): Flow<String>

    suspend fun setUserLoggedIn(result: Boolean)
    fun getUserLoggedIn(): Flow<Boolean>

    suspend fun setLastSyncTs(result: Long)
    fun getLastSyncTs(): Flow<Long>

    suspend fun setReSyncTimer(result: Int)
    fun getReSyncTime(): Flow<Int>

    suspend fun setMemberSyncGrid(result: String)
    fun getMemberSyncGrid(): Flow<String>

    suspend fun setMemberGroupSyncGrid(result: String)
    fun getMemberGroupSyncGrid(): Flow<String>

    suspend fun setProductsSyncGrid(result: String)
    fun getProductsSyncGrid(): Flow<String>

    suspend fun setCategorySyncGrid(result: String)
    fun getCategorySyncGrid(): Flow<String>

    suspend fun setStockSyncGrid(result: String)
    fun getStockSyncGrid(): Flow<String>

    suspend fun setPromotionsSyncGrid(result: String)
    fun getPromotionsSyncGrid(): Flow<String>

    suspend fun setPaymentTypeSyncGrid(result: String)
    fun getPaymentTypeSyncGrid(): Flow<String>

    suspend fun setIsPrinterEnabled(result: Boolean)
    fun getIsPrinterEnabled(): Flow<Boolean>

    suspend fun setTerminalCode(result: String)
    fun getTerminalCode(): Flow<String>
}