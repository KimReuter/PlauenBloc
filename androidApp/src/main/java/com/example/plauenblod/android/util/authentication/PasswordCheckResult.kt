package com.example.plauenblod.android.util.authentication

data class PasswordCheckResult(
    val hasMinLength: Boolean,
    val hasUpperLowerCase: Boolean,
    val hasNumber: Boolean,
    val hasSpecialChar: Boolean
)

fun checkPasswordCriteria(password: String): PasswordCheckResult {
    return PasswordCheckResult(
        hasMinLength = password.length >= 8,
        hasUpperLowerCase = password.any { it.isUpperCase() } && password.any { it.isLowerCase() },
        hasNumber = password.any { it.isDigit() },
        hasSpecialChar = password.any { "!@#\$%^&*()_+-=[]|,./?><".contains(it) }
    )
}