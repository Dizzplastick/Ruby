package com.example.myapplication43.di

import com.example.myapplication43.data.repository.FirebaseMusicRepositoryImpl
import com.example.myapplication43.data.repository.MockMusicRepositoryImpl
import com.example.myapplication43.domain.repository.MusicRepository
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

// Это переменная, в которой хранится "инструкция" для Koin
val dataModule = module {

    // single означает "Одиночка". Koin создаст этот объект ОДИН раз
    // и будет отдавать его всем, кто попросит.
    // В скобках <...> мы указываем ИНТЕРФЕЙС (что просят).
    // В фигурных скобках { ... } мы создаем РЕАЛИЗАЦИЮ (что дать).
// 1. Провайдим сам инстанс Firestore
    single { FirebaseFirestore.getInstance() }

    // 2. ПОДМЕНА! Используем Firebase реализацию
    // Было: single<MusicRepository> { MockMusicRepositoryImpl() }

    // Стало:
    single<MusicRepository> {
        FirebaseMusicRepositoryImpl(db = get())
    }
}