package com.example.plauenblod.component.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.example.plauenblod.android.R

@Composable
fun FirstHallMapScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        ZoomableImage(
            resourceId = R.drawable.boulderhalle_grundriss_vordere_halle,
            contentDescription = "Vordere Halle"
        )

        // Hier kommen später die Boulder-Punkte als Overlay drauf
    }
}

@Composable
fun SecondHallMapScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        ZoomableImage(
            resourceId = R.drawable.boulderhalle_grundriss_hintere_halle,
            contentDescription = "Hintere Halle"
        )

        // Hier kommen später die Boulder-Punkte als Overlay drauf
    }
}

@Composable
fun ZoomableImage(resourceId: Int, contentDescription: String) {
    var scale by remember { mutableStateOf(1.4f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var layoutSize by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                layoutSize = Offset(
                    layoutCoordinates.size.width.toFloat(),
                    layoutCoordinates.size.height.toFloat()
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1.4f, 5f)

                    val maxX = (scale - 1f) * layoutSize.x / 2
                    val maxY = (scale - 1f) * layoutSize.y / 2

                    val newX = (offset.x + pan.x).coerceIn(-maxX, maxX)
                    val newY = (offset.y + pan.y).coerceIn(-maxY, maxY)

                    offset = Offset(newX, newY)
                }
            }
    ) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = contentDescription,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .fillMaxSize()
        )
    }
}