package com.lfssolutions.retialtouch.dataBase

import android.content.Context
import com.lfssolutions.retialtouch.dataBase.KeyValueStore

class AndroidKeyValueStore(private val context: Context) : KeyValueStore {
    private val sharedPreferences =
        context.getSharedPreferences("retailTouchPrefs", Context.MODE_PRIVATE)

    override fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int? {
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
    override fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

}