package com.lfssolutions.retialtouch.di


import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.lfssolutions.retialtouch.dataBase.AndroidKeyValueStore
import com.lfssolutions.retialtouch.dataBase.DatabaseDriverFactory
import com.lfssolutions.retialtouch.dataBase.KeyValueStore
import com.lfssolutions.retialtouch.retailTouchDB
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(androidApplication()) }
    single<KeyValueStore> { AndroidKeyValueStore(androidApplication()) }
}
