package com.example.plauenblod.feature.auth.repository

import com.example.plauenblod.feature.auth.AuthResult
import com.example.plauenblod.feature.auth.UserRole
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val isLoggedIn: StateFlow<Boolean>
    val isInitialized: StateFlow<Boolean>

    suspend fun signUp(userName: String, email: String, password: String): AuthResult
    suspend fun addUserToFirestore(userId: String, userName: String, role: UserRole = UserRole.USER)
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun fetchUserRole(userId: String): UserRole?
    suspend fun sendPasswort(email: String): Result<Unit>
    fun signOut()
}