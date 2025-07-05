package com.example.plauenblod.feature.routeReview.model

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RouteReview (
    val id: String = "",
    val routeId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userProfileImageUrl: String? = "",
    val stars: Int = 0,
    val comment: String = "",
    val completed: Boolean = false,
    val completionDate: String? = null,
    val attempts: Int = 1,
    val perceivedDifficulty: Difficulty? = null,
    val timeStamp: Instant = Clock.System.now()
)