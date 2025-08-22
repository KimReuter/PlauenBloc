package com.example.plauenblod.feature.user.model

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import kotlinx.serialization.Serializable

@Serializable
data class CompletedRoute (
    val routeId: String? = null,
    val routeName: String? = null,
    val number: Int? = null,
    val attempts: Int? = null,
    val difficulty: Difficulty? = null,
    val date: String? = null
)