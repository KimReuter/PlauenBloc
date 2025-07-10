package com.example.plauenblod.feature.user.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class UserActivity(
    val userId: String,
    val type: ActivityType,
    val routeName: String? = null,
    val routeId: String? = null,
    val comment: String? = null,
    val stars: Int? = null,
    val attempts: Int? = null,
    val timeStamp: Instant = Clock.System.now()
)