package com.example.myapplication43.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.models.User
import com.example.myapplication43.domain.repository.MusicRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    fun registerUser(email: String, pass: String, username: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener

                // Создаем объект пользователя
                val newUser = User(
                    id = uid,
                    email = email,
                    username = username,
                    avatarUrl = "" // Пока пустое, потом сделаем загрузку аватарки
                )

                // Сохраняем в базу через репозиторий
                viewModelScope.launch {
                    repository.saveUser(newUser)
                    onSuccess()
                }
            }
            .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
    }

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.localizedMessage ?: "Error") }
    }
}