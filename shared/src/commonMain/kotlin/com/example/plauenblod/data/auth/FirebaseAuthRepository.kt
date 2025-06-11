package com.example.plauenblod.data.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthRepository {

    private val scope = MainScope()

    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isInitialized = MutableStateFlow(false)
    override val isInitialized: StateFlow<Boolean> = _isInitialized

    init {
        scope.launch {
            _isLoggedIn.value = auth.currentUser != null
            _isInitialized.value = true
        }
    }

    override suspend fun signUp(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
        auth.currentUser?.updateProfile(displayName = userName)
        _isLoggedIn.value = true
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
        _isLoggedIn.value = true
    }

    override fun signOut() {
        scope.launch {
            auth.signOut()
            _isLoggedIn.value = false
        }
    }
}