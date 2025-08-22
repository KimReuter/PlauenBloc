package com.example.plauenblod.feature.map.component

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun BoulderMarker(x: Float, y: Float, number: Int, color: Color) {
    Canvas(
        modifier = Modifier
            .offset { IntOffset((x - 16).toInt(), (y - 16).toInt()) }
            .size(32.dp)
    ) {
        drawCircle(color = color)

        val paint = Paint().apply {
            textAlign = Paint.Align.CENTER
            textSize = 24f
            this.color = Color.Black.toArgb()
            isFakeBoldText = true
        }

        drawContext.canvas.nativeCanvas.drawText(
            number.toString(),
            size.width / 2,
            size.height / 2 - ((paint.descent() + paint.ascent()) / 2),
            paint
        )
    }
}