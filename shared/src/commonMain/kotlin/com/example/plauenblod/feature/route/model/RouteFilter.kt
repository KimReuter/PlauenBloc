package com.example.plauenblod.feature.route.model

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import com.example.plauenblod.feature.route.model.routeProperty.Sector

data class RouteFilter(
    val hall: HallSection? = null,
    val sector: Sector? = null,
    val number: Int? = null,
    val holdColor: HoldColor? = null,
    val difficulty: Difficulty? = null,
    val routeSetter: String? = null
)