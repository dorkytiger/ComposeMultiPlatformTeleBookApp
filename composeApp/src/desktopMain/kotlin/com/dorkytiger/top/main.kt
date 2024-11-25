package com.dorkytiger.top

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dorkytiger.top.di.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "TeleBookApp",
    ) {
        App()
    }
}