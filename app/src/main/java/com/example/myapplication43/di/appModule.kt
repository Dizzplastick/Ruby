package com.example.myapplication43.di

import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.presentation.M
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<MainViewModel>{
        MainViewModel(
            
        )
    }
}