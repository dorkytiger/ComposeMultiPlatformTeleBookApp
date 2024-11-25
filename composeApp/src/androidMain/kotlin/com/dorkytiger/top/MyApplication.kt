package com.dorkytiger.top

import android.app.Application
import com.dorkytiger.top.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(this@MyApplication)
        }
    }
}