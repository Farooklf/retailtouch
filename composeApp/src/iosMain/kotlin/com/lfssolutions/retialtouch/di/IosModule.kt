package com.lfssolutions.retialtouch.di

import com.lfssolutions.retialtouch.dataBase.DatabaseDriverFactory
import com.lfssolutions.retialtouch.dataBase.IOSKeyValueStore
import com.lfssolutions.retialtouch.dataBase.KeyValueStore
import org.koin.dsl.module

val iosModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory() }
    single<KeyValueStore> { IOSKeyValueStore() }
}