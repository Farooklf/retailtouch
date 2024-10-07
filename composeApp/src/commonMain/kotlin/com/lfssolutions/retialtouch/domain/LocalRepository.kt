package com.lfssolutions.retialtouch.domain

import com.lfssolutions.retialtouch.dataBase.KeyValueStore
import com.lfssolutions.retialtouch.utils.PrefKeys.CURRENT_TENANT_URL
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_CODE
import com.lfssolutions.retialtouch.utils.PrefKeys.IS_LOGGED_IN
import com.lfssolutions.retialtouch.utils.PrefKeys.TENANT_ID
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN_TIME
import com.lfssolutions.retialtouch.utils.PrefKeys.USER_ID
import com.lfssolutions.retialtouch.utils.DateTime

class LocalRepository(
    private val keyValueStore: KeyValueStore
) {

    fun setBaseURL(result: String) {
        setString(CURRENT_TENANT_URL, result)
    }
    fun getBaseURL() : String {
        return getString(CURRENT_TENANT_URL)
    }

    fun setToken(result: String) {
        setString(TOKEN, result)
        setString(
            TOKEN_TIME,
            DateTime.getCurrentDateAndTimeInEpochMilliSeconds().toString()
        )
    }

    fun getToken() : String {
        return getString(TOKEN)
    }

    fun getTokenTime() : String {
        return getString(TOKEN_TIME)
    }

    fun setUserId(result: Int) {
        setInt(USER_ID, result)
    }

    fun getUserId() :Int {
        return getInt(USER_ID)
    }

    fun setTenantId(result: Int) {
        setInt(TENANT_ID, result)
    }

    fun getTenantId() :Int{
        return getInt(TENANT_ID)
    }

    fun setUserLoggedIn(result: Boolean) {
        setBoolean(IS_LOGGED_IN, result)
    }

    fun getUserLoggedIn() :Boolean{
        return getBoolean(IS_LOGGED_IN)
    }



    fun setEmployeeCode(result: String) {
        setString(EMPLOYEE_CODE, result)
    }
    fun getEmployeeCode() : String {
        return getString(EMPLOYEE_CODE)
    }



    private fun setString(key: String, value: String) {
        keyValueStore.putString(key, value)
    }

    private fun getString(key: String): String {
        return keyValueStore.getString(key, "") ?: ""
    }

    private fun setInt(key: String, value: Int) {
        keyValueStore.putInt(key, value)
    }

    private fun getInt(key: String): Int {
        return keyValueStore.getInt(key, 0) ?: 0
    }

    private fun setBoolean(key: String, value: Boolean) {
        keyValueStore.putBoolean(key, value)
    }

    private fun getBoolean(key: String): Boolean {
        return keyValueStore.getBoolean(key, false) ?: false
    }
}