package com.hashmato.retailtouch.di

import android.content.Context
import com.outsidesource.oskitkmp.storage.AndroidKMPStorage
import com.outsidesource.oskitkmp.storage.IKMPStorage
import org.koin.dsl.bind
import org.koin.dsl.module

val androidModule = module {
    single { AndroidKMPStorage(get()) } bind IKMPStorage::class
    single<Context> { get<android.app.Application>() } // Provide application context safely
}