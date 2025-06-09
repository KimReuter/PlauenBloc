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

    override fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        scope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                _isLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    override fun signIn(email: String, password: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        scope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                _isLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    override fun signOut() {
        scope.launch {
            auth.signOut()
            _isLoggedIn.value = false
        }
    }
}