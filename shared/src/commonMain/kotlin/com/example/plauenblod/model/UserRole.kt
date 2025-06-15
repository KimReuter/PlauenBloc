package com.example.plauenblod.model

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