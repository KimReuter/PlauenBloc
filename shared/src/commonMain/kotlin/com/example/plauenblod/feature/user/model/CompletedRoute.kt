package com.example.plauenblod.feature.user.model

import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import kotlinx.serialization.Serializable

@Serializable
data class CompletedRoute (
    val routeId: String? = null,
    val routeName: String? = null,
    val attempts: Int? = null,
    val difficulty: HoldColor? = null,
    val date: String? = null
)