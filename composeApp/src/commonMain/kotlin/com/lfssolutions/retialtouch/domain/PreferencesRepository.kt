package com.lfssolutions.retialtouch.domain


import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    suspend fun setBaseURL(result: String)
    suspend fun getBaseURL():Flow<String>

    suspend fun setToken(result: String)
    suspend fun getToken(): Flow<String>
    suspend fun getTokenTime(): Flow<Long>

    suspend fun setUserId(result: Long)
    suspend fun getUserId(): Flow<Long>

    suspend fun setTenantId(result: Int)
    fun getTenantId(): Flow<Int>

    suspend fun setLocationId(result: Int)
    fun getLocationId(): Flow<Int>

    suspend fun setCurrencySymbol(result: String)
    fun getCurrencySymbol():Flow<String>

    suspend fun setEmployeeCode(result: String)
    fun getEmployeeCode(): Flow<String>

    suspend fun setUserLoggedIn(result: Boolean)
    fun getUserLoggedIn(): Flow<Boolean>


}