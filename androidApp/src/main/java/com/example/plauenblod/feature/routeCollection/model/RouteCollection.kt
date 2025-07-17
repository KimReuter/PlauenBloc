package com.example.plauenblod.feature.routeCollection.model

import kotlinx.datetime.Instant
import kotlinx.datetime.Clock

data class RouteCollection(
    val id: String = "",
    val creatorId: String,
    val name: String,
    val description: String? = null,
    val isPublic: Boolean = true,
    val tags: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val routeIds: List<String> = emptyList(),
    val order: List<String> = emptyList(),
    val routeCount: Int = 0,
    val likesCount: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now()
)