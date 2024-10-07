package com.lfssolutions.retialtouch.dataBase

import app.cash.sqldelight.db.SqlDriver

// iOS main
expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}