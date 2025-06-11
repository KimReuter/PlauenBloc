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
    val isInitialized: StateFlow<Boolean> = authRepository.isInitialized

    fun signIn(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        coroutineScope.launch {
            try {
                authRepository.signIn(email, password)
                onResult(Result.success(Unit))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun signUp(userName: String, email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        if (!isValidPassword(password)) {
            onResult(Result.failure(IllegalArgumentException("Passwort ist zu schwach")))
            return
        }

        coroutineScope.launch {
            try {
                authRepository.signUp(userName, email, password)
                onResult(Result.success(Unit))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun isValidPassword(password: String): Boolean {
        val minLength = 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        return password.length >= minLength && hasUppercase && hasDigit && hasSpecial
    }

    fun signOut() {
        authRepository.signOut()
    }
}