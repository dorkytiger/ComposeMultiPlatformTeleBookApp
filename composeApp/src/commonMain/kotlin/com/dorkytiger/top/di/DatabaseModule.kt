package com.dorkytiger.top.di

import com.dorkytiger.top.db.getAppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module


val databaseModule: Module = module {
    single { getAppDatabase(get()) }
}