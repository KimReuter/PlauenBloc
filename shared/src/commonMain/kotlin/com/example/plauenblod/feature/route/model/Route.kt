package com.example.plauenblod.feature.route.model

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import com.example.plauenblod.feature.route.model.routeProperty.Sector

data class Route(
    val id: String,
    val name: String,
    val hall: HallSection,
    val sector: Sector,
    val holdColor: HoldColor,
    val difficulty: Difficulty,
    val number: Int,
    val description: String,
    val setter: String,
    val x: Float,
    val y: Float,
    val points: Int
)