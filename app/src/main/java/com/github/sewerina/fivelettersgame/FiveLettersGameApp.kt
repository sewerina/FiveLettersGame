package com.github.sewerina.fivelettersgame

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FiveLettersGameApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FiveLettersGameApp)
            modules(appModule)
        }
    }
}