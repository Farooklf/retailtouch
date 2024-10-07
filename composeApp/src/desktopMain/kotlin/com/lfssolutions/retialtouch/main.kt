package com.lfssolutions.retialtouch

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.lfssolutions.retialtouch.di.appModule
import com.lfssolutions.retialtouch.di.desktopModule
import org.koin.core.context.GlobalContext.startKoin
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "retailTouch",
        state = rememberWindowState(width = 1280.dp, height = 720.dp),
    ) {
        window.minimumSize = Dimension(800, 600)
        startKoin {
            modules(appModule() + desktopModule)
        }
        App()
    }
}