package com.example.myapplication43.di


import com.example.myapplication43.presentation.viewmodel.AuthViewModel
import com.example.myapplication43.presentation.viewmodel.HomeViewModel
import com.example.myapplication43.presentation.viewmodel.PlayerViewModel
import com.example.myapplication43.presentation.viewmodel.ProfileViewModel
import com.example.myapplication43.presentation.viewmodel.UploadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    // Объявляем вьюмодель
    viewModel { PlayerViewModel(getHomeTracksUseCase = get(), musicController = get(), repository = get()) }
    viewModel { HomeViewModel(get(),get(),  get()) }

    viewModel { UploadViewModel(get()) }

    viewModel { AuthViewModel(get()) }

    viewModel { ProfileViewModel(get(),
        musicController = get()) }
}