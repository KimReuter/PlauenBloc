package com.example.plauenblod.extension

import androidx.compose.ui.graphics.Color
import com.example.plauenblod.model.HoldColor

fun HoldColor.toColor(): Color = when (this) {
    HoldColor.PINK -> Color(0xFFFFC0CB)
    HoldColor.WHITE -> Color(0xFFFFFFFF)
    HoldColor.YELLOW -> Color(0xFFFFFF00)
    HoldColor.BLUE -> Color(0xFF2196F3)
    HoldColor.GREEN -> Color(0xFF4CAF50)
    HoldColor.RED -> Color(0xFFF44336)
    HoldColor.BROWN -> Color(0xFF795548)
    HoldColor.PURPLE -> Color(0xFF9C27B0)
    HoldColor.BLACK -> Color(0xFF000000)
    HoldColor.TURQUOISE -> Color(0xFF40E0D0)
    HoldColor.GREY -> Color(0xFF9E9E9E)
}