package com.lfssolutions.retialtouch.utils.sqldb

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.lfssolutions.retialtouch.retailTouchDB
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dbModule: Module = module{
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    retailTouchDB.Schema.create(driver)
}
