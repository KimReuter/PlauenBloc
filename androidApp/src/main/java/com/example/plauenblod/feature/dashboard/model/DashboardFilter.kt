package com.example.plauenblod.feature.dashboard.model

data class DashboardFilter(
    val sortBy: SortOption = SortOption.MOST_POINTS,
    val gender: Gender? = null,
    val ageGroup: AgeGroup? = null
)