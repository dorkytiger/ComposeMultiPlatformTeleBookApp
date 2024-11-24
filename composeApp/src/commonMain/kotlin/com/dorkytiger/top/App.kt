package com.dorkytiger.top

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dorkytiger.top.di.networkModule
import com.dorkytiger.top.di.viewModel
import com.dorkytiger.top.persistence.screen.book.BookScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        printLogger()
        modules(listOf(networkModule, viewModel))
    }) {
        MaterialTheme {
            BookScreen()
        }
    }

}