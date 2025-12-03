package com.example.myapplication43.app

import android.app.Application
import com.example.myapplication43.di.appModule
import com.example.myapplication43.di.dataModule
import com.example.myapplication43.di.domainModule
import com.example.myapplication43.di.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR,)
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule, serviceModule))
        }
    }

}