package com.example.plauenblod.extension

import androidx.compose.ui.graphics.Color
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty

fun Difficulty.toColor(): Color = when (this) {
    Difficulty.PINK -> Color(0xFFFFC0CB)
    Difficulty.WHITE -> Color(0xFFFFFFFF)
    Difficulty.YELLOW -> Color(0xFFFFFF00)
    Difficulty.BLUE -> Color(0xFF2196F3)
    Difficulty.GREEN -> Color(0xFF4CAF50)
    Difficulty.RED -> Color(0xFFF44336)
    Difficulty.BROWN -> Color(0xFF795548)
}