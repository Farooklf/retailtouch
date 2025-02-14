package com.hashmato.retailtouch.utils.sqldb

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.hashmato.retailtouch.retailTouchDB
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dbModule: Module = module {
    single<SqlDriver> {
        NativeSqliteDriver(retailTouchDB.Schema, "retailTouchDB")
    }
}