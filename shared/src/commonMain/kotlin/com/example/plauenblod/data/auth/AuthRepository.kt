package com.example.plauenblod.data.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit)
    fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit)
    fun signOut()
}