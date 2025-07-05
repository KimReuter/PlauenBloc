package com.example.plauenblod.feature.auth

enum class UserRole {
    USER,
    OPERATOR;

    companion object {
        fun fromString(role: String?): UserRole {
            return try {
                valueOf(role?.uppercase() ?: "")
            } catch (e: Exception) {
                USER
            }
        }
    }
}