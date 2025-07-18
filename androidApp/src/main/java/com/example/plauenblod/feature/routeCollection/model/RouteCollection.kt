package com.example.plauenblod.feature.routeCollection.model

import kotlinx.datetime.Clock
import com.example.plauenblod.android.util.FirestoreInstant
import com.google.firebase.firestore.PropertyName

data class RouteCollection(
    val id: String = "",
    val creatorId: String,
    val name: String,
    val description: String? = null,

    @get:PropertyName("public")
    @set:PropertyName("public")
    var `public`: Boolean = true,

    val routeIds: List<String> = emptyList(),
    val doneRouteIds: List<String> = emptyList(),
    val order: List<String> = emptyList(),
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
        `public`     = true,
        routeIds     = emptyList(),
        order        = emptyList(),
        likesCount   = 0,
        createdAt    = FirestoreInstant(0, 0),
        updatedAt    = FirestoreInstant(0, 0)
    )

    val routeCount: Int
        get() = routeIds.size
}