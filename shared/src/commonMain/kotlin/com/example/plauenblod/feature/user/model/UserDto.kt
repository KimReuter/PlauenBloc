package com.example.plauenblod.feature.user.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String? = null,
    val userName: String? = null,
    val role: String? = "USER",
    val bio: String? = null,
    val totalPoints: Long? = 0,
    val completedRoutes: List<CompletedRoute> = emptyList(),
    val profileImageUrl: String? = null,
    val recentActivities: List<UserActivity> = emptyList()
)