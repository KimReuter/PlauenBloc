package com.example.plauenblod.feature.route.model.util

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor

fun calculatePoints(difficulty: Difficulty, isFlash: Boolean = false): Int {
    val basePoints = when (difficulty) {
        Difficulty.WHITE -> 10
        Difficulty.YELLOW -> 20
        Difficulty.BLUE -> 30
        Difficulty.GREEN -> 40
        Difficulty.RED -> 50
        Difficulty.BROWN -> 60
        Difficulty.PINK -> 0
    }
    return if (isFlash && basePoints > 0) basePoints + 2 else basePoints
}