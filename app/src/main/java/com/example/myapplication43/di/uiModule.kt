package com.example.myapplication43.di


import com.example.myapplication43.presentation.player.MusicControllerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module



val uiModule = module {
    // Single - так как контроллер должен жить, пока живет приложение
    single { MusicControllerImpl(androidContext()) }
}

