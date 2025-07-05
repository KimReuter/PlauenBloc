package com.example.plauenblod.feature.user.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String? = null,
    val userName: String? = null,
    val role: String? = "USER",
    val totalPoints: Long? = 0,
    val completedRoutes: List<CompletedRoute> = emptyList(),
    val profileImage: String? = null
)