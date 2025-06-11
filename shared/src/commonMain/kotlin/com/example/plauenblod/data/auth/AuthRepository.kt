package com.example.plauenblod.data.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    val isInitialized: StateFlow<Boolean>

    suspend fun signUp(userName: String, email: String, password: String)
    suspend fun signIn(email: String, password: String)
    fun signOut()
}