package com.example.plauenblod.feature.routeCollection.model

import kotlinx.datetime.Clock
import com.example.plauenblod.android.util.FirestoreInstant

data class RouteCollection(
    val id: String = "",
    val creatorId: String,
    val name: String,
    val description: String? = null,
    val isPublic: Boolean = true,
    val routeIds: List<String> = emptyList(),
    val order: List<String> = emptyList(),
    val routeCount: Int = 0,
    val likesCount: Int = 0,
    val createdAt: FirestoreInstant = FirestoreInstant.fromInstant(Clock.System.now()),
    val updatedAt: FirestoreInstant = FirestoreInstant.fromInstant(Clock.System.now())
) {
    @Suppress("unused")
    constructor() : this(
        id           = "",
        creatorId    = "",
        name         = "",
        description  = null,
        isPublic     = true,
        routeIds     = emptyList(),
        order        = emptyList(),
        routeCount   = 0,
        likesCount   = 0,
        createdAt    = FirestoreInstant(0, 0),
        updatedAt    = FirestoreInstant(0, 0)
    )
}