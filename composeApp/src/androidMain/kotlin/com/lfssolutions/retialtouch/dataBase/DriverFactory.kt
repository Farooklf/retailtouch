package com.lfssolutions.retialtouch.dataBase

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.lfssolutions.retialtouch.retailTouchDB


// Android main
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun create(): SqlDriver =
        AndroidSqliteDriver(retailTouchDB.Schema, context, "retailTouch.db")
}