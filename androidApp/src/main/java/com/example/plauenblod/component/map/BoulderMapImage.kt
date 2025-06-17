package com.example.plauenblod.component.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun BoulderMapImage(
    imageResId: Int,
    selectedPoint: Offset?,
    onTap: (Offset) -> Unit,
    difficultyColor: Color? = null,
    number: Int? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onTap(offset)
                }
            }
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Boulder Map",
            modifier = Modifier.fillMaxSize()
        )

        if (selectedPoint != null && difficultyColor != null && number != null) {
            Canvas(modifier = Modifier.offset {
                IntOffset(
                    (selectedPoint.x - 16).toInt(),
                    (selectedPoint.y - 16).toInt()
                )
            }.size(32.dp)
            ) {
                drawCircle(color = difficultyColor)

                val paint = android.graphics.Paint().apply {
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 24f
                    color = android.graphics.Color.BLACK
                    isFakeBoldText = true
                }

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        number.toString(),
                        size.width / 2,
                        size.height / 2 - ((paint.descent() + paint.ascent()) / 2),
                        android.graphics.Paint().apply {
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 24f
                            color = android.graphics.Color.BLACK
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}