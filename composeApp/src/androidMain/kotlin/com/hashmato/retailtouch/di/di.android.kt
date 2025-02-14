package com.hashmato.retailtouch.di

import com.outsidesource.oskitkmp.storage.AndroidKMPStorage
import com.outsidesource.oskitkmp.storage.IKMPStorage
import org.koin.dsl.bind
import org.koin.dsl.module

val androidModule = module {
    single { AndroidKMPStorage(get()) } bind IKMPStorage::class
}