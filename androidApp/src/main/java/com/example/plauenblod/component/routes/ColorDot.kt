package com.example.plauenblod.component.routes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ColorDot(color: Color, size: Dp = 12.dp, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .padding(end = 8.dp)
            .size(size)
    ) {
        drawCircle(color = color)
    }
}