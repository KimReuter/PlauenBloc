package com.example.plauenblod.model

import com.example.plauenblod.model.routeProperty.Difficulty
import com.example.plauenblod.model.routeProperty.HallSection
import com.example.plauenblod.model.routeProperty.HoldColor
import com.example.plauenblod.model.routeProperty.Sector

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
    val y: Float
)