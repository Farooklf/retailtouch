package com.lfssolutions.retialtouch.di


import com.lfssolutions.retialtouch.dataBase.AndroidKeyValueStore
import com.lfssolutions.retialtouch.dataBase.DatabaseDriverFactory
import com.lfssolutions.retialtouch.dataBase.KeyValueStore
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val androidModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(androidApplication()) }
    single<KeyValueStore> { AndroidKeyValueStore(androidApplication()) }
}
