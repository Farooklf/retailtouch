package com.lfssolutions.retialtouch.di


import com.lfssolutions.retialtouch.dataBase.DatabaseDriverFactory
import com.lfssolutions.retialtouch.dataBase.DesktopKeyValueStore
import com.lfssolutions.retialtouch.dataBase.KeyValueStore
import org.koin.dsl.module


val desktopModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory() }
    single<KeyValueStore> { DesktopKeyValueStore() }
}
