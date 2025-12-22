package com.example.myapplication43.di

import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.domain.useCase.SearchTracksUseCase
import org.koin.dsl.module

val domainModule = module {


    factory { GetHomeTracksUseCase(repository = get()) }

    factory { SearchTracksUseCase(repository = get()) }

}