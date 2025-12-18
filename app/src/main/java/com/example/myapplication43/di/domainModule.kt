package com.example.myapplication43.di

import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.domain.useCase.SearchTracksUseCase
import org.koin.dsl.module

val domainModule = module {
    // factory означает "Фабрика". Новый объект создается КАЖДЫЙ раз при запросе.
    // get() - это магия. Koin сам найдет нужный MusicRepository,
    // который мы объявили выше, и вставит его в конструктор.

    factory {
        GetHomeTracksUseCase(repository = get())
    }
    factory { SearchTracksUseCase(repository = get()) }

}