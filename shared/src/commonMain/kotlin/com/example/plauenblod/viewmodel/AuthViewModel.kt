package com.example.plauenblod.viewmodel

import com.example.plauenblod.data.auth.AuthRepository
import com.example.plauenblod.data.auth.AuthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val coroutineScope: CoroutineScope
) {
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val isInitialized: StateFlow<Boolean> = authRepository.isInitialized

    fun signIn(email: String, password: String, onResult: (AuthResult) -> Unit) {
        coroutineScope.launch {
            val result = authRepository.signIn(email, password)
            onResult(result)
        }
    }

    fun sendPasswordReset(email: String, onResult: (Result<Unit>) -> Unit) {
        coroutineScope.launch {
            val result = authRepository.sendPasswort(email)
            onResult(result)
        }
    }

    fun signUp(userName: String, email: String, password: String, onResult: (AuthResult) -> Unit) {
        if (isValidPassword(password)) {
            coroutineScope.launch {
                val result = authRepository.signUp(userName, email, password)
                onResult(result)
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