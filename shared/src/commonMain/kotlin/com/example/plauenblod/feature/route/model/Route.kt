package com.example.plauenblod.feature.route.model

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import com.example.plauenblod.feature.route.model.routeProperty.Sector

data class Route(
    val id: String = "",
    val name: String = "",
    val hall: HallSection = HallSection.FRONT,
    val sector: Sector = Sector.SONNENPLATTE,
    val holdColor: HoldColor = HoldColor.YELLOW,
    val difficulty: Difficulty = Difficulty.GREEN,
    val number: Int = 0,
    val description: String = "",
    val setter: String = "",
    val x: Float = 0f,
    val y: Float = 0f,
    val points: Int = 0
)