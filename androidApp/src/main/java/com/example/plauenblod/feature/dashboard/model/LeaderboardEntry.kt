package com.example.plauenblod.feature.dashboard.model

data class LeaderboardEntry(
    val userId: String,
    val userName: String,
    val profileImageUrl: String?,
    val points: Long,
    val routeCount: Int,
    val rank: Int
)