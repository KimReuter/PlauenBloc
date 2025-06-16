package com.example.plauenblod.component.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun BoulderMapImage(
    imageResId: Int,
    selectedPoint: Offset?,
    onTap: (Offset) -> Unit
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

        selectedPoint?.let { point ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(point.x.toInt() - 12, point.y.toInt() - 12) }
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
            )
        }
    }
}