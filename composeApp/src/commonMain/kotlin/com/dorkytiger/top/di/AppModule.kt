package com.dorkytiger.top.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

val appModule = listOf(networkModule, viewModelModule, commonModule, platformModule,databaseModule)

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(appModule)
    }
}