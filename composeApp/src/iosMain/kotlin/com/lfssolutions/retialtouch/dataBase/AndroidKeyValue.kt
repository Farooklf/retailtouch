package com.lfssolutions.retialtouch.dataBase

import platform.Foundation.NSUserDefaults



class IOSKeyValueStore : KeyValueStore {

    override fun getString(key: String, defaultValue: String?): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key)
    }

    override fun putString(key: String, value: String) {
        // Implement iOS-specific code to store string in UserDefaults
        NSUserDefaults.standardUserDefaults.setObject(value, key)
    }

    override fun getInt(key: String, defaultValue: Int): Int? {
        return NSUserDefaults.standardUserDefaults.integerForKey(key).toInt()
    }

    override fun putInt(key: String, value: Int) {
        NSUserDefaults.standardUserDefaults.setInteger(value.toLong(), key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return NSUserDefaults.standardUserDefaults.boolForKey(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        NSUserDefaults.standardUserDefaults.setBool(value, key)
    }

    override fun clearAllData() {
        val defaults = NSUserDefaults.standardUserDefaults
        val dictionary = defaults.dictionaryRepresentation()
        for (key in dictionary.keys) {
            defaults.removeObjectForKey(key.toString())
        }
        defaults.synchronize()
    }
}