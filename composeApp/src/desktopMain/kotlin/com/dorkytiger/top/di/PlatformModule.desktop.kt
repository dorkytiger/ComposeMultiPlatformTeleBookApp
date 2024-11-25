package com.dorkytiger.top.di

import com.dorkytiger.top.db.getAppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single { getAppDatabase() }
}