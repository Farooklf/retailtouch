package com.lfssolutions.retialtouch.dataBase

import app.cash.sqldelight.db.SqlDriver


expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}