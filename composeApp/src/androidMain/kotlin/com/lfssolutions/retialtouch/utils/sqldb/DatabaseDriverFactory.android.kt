package com.lfssolutions.retialtouch.utils.sqldb

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.lfssolutions.retialtouch.retailTouchDB
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dbModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(retailTouchDB.Schema, androidContext(), "retailTouchDB")
    }
}