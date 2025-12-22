package com.example.myapplication43.di

import com.example.myapplication43.data.repository.FirebaseMusicRepositoryImpl
import com.example.myapplication43.domain.repository.MusicRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.module


val dataModule = module {


// 1. Провайдим сам инстанс Firestore
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }


    single<MusicRepository> {
        // Теперь передаем и db, и storage
        FirebaseMusicRepositoryImpl(db = get(), storage = get())
    }
}