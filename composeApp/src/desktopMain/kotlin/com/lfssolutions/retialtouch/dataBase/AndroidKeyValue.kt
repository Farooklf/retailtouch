package com.lfssolutions.retialtouch.dataBase

import java.util.prefs.Preferences

class DesktopKeyValueStore(private val preferences: Preferences) : KeyValueStore {

    constructor() : this(Preferences.userRoot().node("retailTouchPrefs"))

    override fun getString(key: String, defaultValue: String?): String? {
        return preferences.get(key, defaultValue)
    }

    override fun putString(key: String, value: String) {
        preferences.put(key, value)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return preferences.get(key, defaultValue.toString()).toInt()
    }

    override fun putInt(key: String, value: Int) {
        preferences.put(key, value.toString())
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.get(key, defaultValue.toString()).toBoolean()
    }

    override fun putBoolean(key: String, value: Boolean) {
        preferences.put(key, value.toString())
    }

    override fun clearAllData() {
        preferences.clear()
    }
}