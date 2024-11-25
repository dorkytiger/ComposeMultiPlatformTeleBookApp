package com.dorkytiger.top.di

import com.dorkytiger.top.util.HTMLUtils
import org.koin.dsl.module

val commonModule = module {
    single {
        HTMLUtils(get())
    }
}