package com.lfssolutions.retialtouch

import androidx.compose.ui.window.ComposeUIViewController
import com.lfssolutions.retialtouch.di.appModule
import com.lfssolutions.retialtouch.di.iosModule
import com.lfssolutions.retialtouch.utils.sqldb.dbModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(dbModule + appModule() + iosModule)
    }
    App()
}