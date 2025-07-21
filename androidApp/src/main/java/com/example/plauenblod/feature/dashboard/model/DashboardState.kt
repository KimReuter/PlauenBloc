package com.example.plauenblod.feature.dashboard.model

data class DashboardState(
    val news: List<NewsPost> = emptyList(),
    val metrics: List<Metric> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val filter: DashboardFilter = DashboardFilter(),
    val isLoading: Boolean = true,
    val error: String? = null
)