package com.example.plauenblod.viewmodel

import com.example.plauenblod.data.auth.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val coroutineScope: CoroutineScope
) {
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn

    fun signIn(email: String, password: String, onError: (Throwable) -> Unit = {}) {
        coroutineScope.launch {
            authRepository.signIn(
                email = email,
                password = password,
                onSuccess = { /* Logging oder Navigation */ },
                onError = onError
            )
        }
    }

    fun signUp(email: String, password: String, onError: (Throwable) -> Unit = {}) {
        coroutineScope.launch {
            authRepository.signUp(
                email = email,
                password = password,
                onSuccess = { /* direkt einloggen */},
                onError = onError
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}