package com.example.plauenblod.viewmodel

import com.example.plauenblod.data.auth.AuthRepository
import com.example.plauenblod.data.auth.AuthResult
import com.example.plauenblod.model.UserRole
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val coroutineScope: CoroutineScope
) {
    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
    val isInitialized: StateFlow<Boolean> = authRepository.isInitialized

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _userProfileImageUrl = MutableStateFlow<String?>(null)
    val userProfileImageUrl: StateFlow<String?> = _userProfileImageUrl


    fun signIn(email: String, password: String, onResult: (AuthResult) -> Unit) {
        coroutineScope.launch {
            val result = authRepository.signIn(email, password)
            if (result is AuthResult.Success) {
                val userId = Firebase.auth.currentUser?.uid
                userId?.let {
                    val role = authRepository.fetchUserRole(it)
                    _userRole.value = role
                }
            }
            onResult(result)
        }
    }

    fun loadUserRole() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        coroutineScope.launch {
            val role = authRepository.fetchUserRole(userId)
            _userRole.value = role
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
                if (result is AuthResult.Success) {
                    val user = Firebase.auth.currentUser
                    user?.updateProfile(displayName = userName)
                    _userName.value = userName
                    _userId.value = user?.uid
                }
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

    init {
        val user = Firebase.auth.currentUser
        _userId.value = user?.uid
        _userName.value = user?.displayName
    }

}