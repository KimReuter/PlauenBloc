package com.example.plauenblod.feature.auth.repository

import com.example.plauenblod.feature.auth.AuthError
import com.example.plauenblod.feature.auth.AuthResult
import com.example.plauenblod.feature.auth.UserRole
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
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

    override suspend fun signUp(userName: String, email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password)
            auth.currentUser?.updateProfile(displayName = userName)
            auth.currentUser?.uid?.let {
                addUserToFirestore(it, userName, UserRole.USER)
            }
            _isLoggedIn.value = true
            AuthResult.Success
        } catch (e: Exception) {
            val message = e.message?.lowercase() ?: ""
            println("Fehlermeldung beim Signup: ${e.message}")

            val error = when {
                "already in use" in message -> AuthError.EmailAlreadyInUse
                "weak password" in message -> AuthError.WeakPassword
                "invalid email" in message -> AuthError.InvalidEmailFormat
                else -> AuthError.Unknown(e.message)
            }
            AuthResult.Error(error)
        }
    }

    override suspend fun addUserToFirestore(userId: String, userName: String, role: UserRole) {
        val userData = mapOf(
            "uid" to userId,
            "userName" to userName,
            "role" to role.name,
            "totalPoints" to 0L
        )

        val userDocRef = Firebase.firestore.collection("users").document(userId)

        userDocRef.set(userData)
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password)
            _isLoggedIn.value = true
            AuthResult.Success
        } catch (e: Exception) {
            val message = e.message?.lowercase() ?: ""

            val error = when {
                "user-not-found" in message || "no user record" in message -> AuthError.InvalidEmail
                "invalid-email" in message -> AuthError.InvalidEmailFormat
                "wrong-password" in message || "invalid login credentials" in message -> AuthError.WrongPassword
                "network-request-failed" in message || "network error" in message -> AuthError.NetworkError
                "too-many-requests" in message -> AuthError.TooManyRequests
                else -> {
                    println("🔥 Auth Fehler nicht gemappt: ${e.message}")
                    AuthError.Unknown(e.message)
                }
            }

            AuthResult.Error(error)
        }
    }

    override suspend fun fetchUserRole(userId: String): UserRole? {
        return try {
            val document = Firebase.firestore.collection("users").document(userId).get()
            val roleString = document.get<String>("role")
            UserRole.valueOf(roleString ?: "USER")
        } catch (e: Exception) {
            println("⚠️ Fehler beim Abrufen der Rolle: ${e.message}")
            null
        }
    }

    override suspend fun sendPasswort(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Fehler beim Passwort-Zurücksetzen: ${e.message}")
            Result.failure(e)
        }
    }

    override fun signOut() {
        scope.launch {
            auth.signOut()
            _isLoggedIn.value = false
        }
    }
}