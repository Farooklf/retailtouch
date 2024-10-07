package com.lfssolutions.retialtouch.dataBase


import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.lfssolutions.retialtouch.retailTouchDB


// Android main
actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        retailTouchDB.Schema.create(driver)
        return driver
    }
}