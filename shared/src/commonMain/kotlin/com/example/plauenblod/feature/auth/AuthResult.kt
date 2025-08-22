package com.example.plauenblod.feature.auth

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val error: AuthError) : AuthResult()
}

sealed class AuthError(val message: String) {
    object InvalidEmail : AuthError("Diese E-Mail ist nicht registriert.")
    object InvalidEmailFormat : AuthError("Bitte gib eine gültige E-Mail-Adresse ein.")
    object WrongPassword : AuthError("Das Passwort ist falsch.")
    object WeakPassword : AuthError("Das Passwort ist zu schwach.")
    object EmailAlreadyInUse : AuthError("Diese E-Mail wird bereits verwendet.")
    object NetworkError : AuthError("Netzwerkfehler – bitte überprüfe deine Verbindung.")
    object TooManyRequests : AuthError("Zu viele Fehlversuche. Bitte warte einen Moment.")
    data class Unknown(val details: String? = null) : AuthError("Ein unbekannter Fehler ist aufgetreten.")
}