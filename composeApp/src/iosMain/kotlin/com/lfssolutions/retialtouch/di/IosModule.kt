package com.lfssolutions.retialtouch.di


import com.outsidesource.oskitkmp.storage.IKMPStorage
import com.outsidesource.oskitkmp.storage.IOSKMPStorage
import org.koin.dsl.bind
import org.koin.dsl.module

/*
val iosModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory() }

}*/

val iosModule = module {
    single { IOSKMPStorage() } bind IKMPStorage::class

}
