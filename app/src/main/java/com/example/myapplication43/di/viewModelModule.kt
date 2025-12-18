package com.example.myapplication43.di


import com.example.myapplication43.presentation.viewmodel.AuthViewModel
import com.example.myapplication43.presentation.viewmodel.HomeViewModel
import com.example.myapplication43.presentation.viewmodel.PlayerViewModel
import com.example.myapplication43.presentation.viewmodel.ProfileViewModel
import com.example.myapplication43.presentation.viewmodel.UploadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    // Объявляем нашу ViewModel
    viewModel {
        PlayerViewModel(
            // Функция get() говорит Koin'у: "Найди этот объект в других модулях сам"
            // Koin найдет GetHomeTracksUseCase в DomainModule
            getHomeTracksUseCase = get(),

            // Koin найдет MusicControllerImpl в UiModule (который мы создали шагом ранее)
            musicController = get(),
            repository = get()
        )
    }
    viewModel { HomeViewModel(get(),get(),  get()) }

    viewModel { UploadViewModel(get()) }

    viewModel { AuthViewModel(get()) }

    viewModel { ProfileViewModel(get(),
        musicController = get()) }
}