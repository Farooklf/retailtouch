package com.hashmato.retailtouch

import androidx.compose.ui.window.ComposeUIViewController
import com.hashmato.retailtouch.ComposeApp.RootContent
import com.hashmato.retailtouch.di.appModule
import com.hashmato.retailtouch.di.iosModule
import com.hashmato.retailtouch.utils.ConnectivityObserver
import com.hashmato.retailtouch.utils.sqldb.dbModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(dbModule + appModule() + iosModule)
    }
    // Start observing network changes
    ConnectivityObserver.startObserving()
    RootContent()
}