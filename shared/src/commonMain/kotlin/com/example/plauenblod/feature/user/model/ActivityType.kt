package com.example.plauenblod.feature.user.model

import kotlinx.serialization.Serializable

@Serializable
enum class ActivityType {
    COMPLETED_ROUTE,
    COMMENTED,
    RATED,
    CREATED_ROUTE
}