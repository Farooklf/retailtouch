package com.lfssolutions.retialtouch

import androidx.compose.ui.window.ComposeUIViewController
import com.lfssolutions.retialtouch.di.appModule
import com.lfssolutions.retialtouch.di.iosModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule() + iosModule)
    }
    App()
}