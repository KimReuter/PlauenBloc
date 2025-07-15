package com.example.plauenblod.feature.map.component

import android.graphics.Paint
import android.graphics.Paint.Align
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.plauenblod.extension.toColor
import com.example.plauenblod.feature.route.model.routeProperty.RelativePosition
import com.example.plauenblod.feature.route.model.Route

@Composable
fun BoulderMap(
    imageResId: Int,
    modifier: Modifier = Modifier,
    routes: List<Route> = emptyList(),
    selectedPoint: RelativePosition? = null,
    onTap: ((RelativePosition) -> Unit)? = null,
    enableZoom: Boolean = false,
    difficultyColor: Color? = null,
    number: Int? = null,
    onRouteClick: ((Route) -> Unit)? = null,
    onRouteLongClick: (Route) -> Unit
    ) {

    val density = LocalDensity.current
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var layoutSize by remember { mutableStateOf(Offset.Zero) }
    val markerRadius = with(density) { 16.dp.toPx() }
    val imageBitmap = ImageBitmap.imageResource(id = imageResId)

    Box(
        modifier = modifier
            .aspectRatio(414f / 551f)
            .onGloballyPositioned {
                layoutSize = Offset(
                    it.size.width.toFloat(),
                    it.size.height.toFloat()
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(routes) {
                    detectTapGestures (
                        onLongPress = { tapOffset ->
                            val longPressedRoute = routes.firstOrNull { route ->
                                val center = Offset(route.x * layoutSize.x, route.y * layoutSize.y)
                                val distance = (tapOffset - center).getDistance()
                                distance <= markerRadius
                            }

                            if (longPressedRoute != null) {
                                onRouteLongClick(longPressedRoute)
                            }
                        },
                        onTap = { tapOffset ->
                            val tappedRoute = routes.firstOrNull { route ->
                                val center = Offset(route.x * layoutSize.x, route.y * layoutSize.y)
                                val distance = (tapOffset - center).getDistance()
                                distance <= markerRadius
                            }

                            if (tappedRoute != null) {
                                onRouteClick?.invoke(tappedRoute)
                            } else {

                                val relativeX = (tapOffset.x / layoutSize.x).coerceIn(0f, 1f)
                                val relativeY = (tapOffset.y / layoutSize.y).coerceIn(0f, 1f)
                                onTap?.invoke(RelativePosition(relativeX, relativeY))
                            }
                        }
                    )
                }
                .graphicsLayer(
                    scaleX = if (enableZoom) scale else 1f,
                    scaleY = if (enableZoom) scale else 1f,
                    translationX = if (enableZoom) offset.x else 0f,
                    translationY = if (enableZoom) offset.y else 0f
                )
        ) {
            drawImage(
                image = imageBitmap,
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )

            routes.forEach { route ->
                val x = route.x * size.width
                val y = route.y * size.height

                drawCircle(
                    color = route.difficulty.toColor(),
                    radius = markerRadius,
                    center = Offset(x, y)
                )

                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 28f
                        textAlign = Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText(route.number.toString(), x, y + 10f, paint)
                }
            }

            if (selectedPoint != null) {
                val x = selectedPoint.x * size.width
                val y = selectedPoint.y * size.height

                drawCircle(
                    color = difficultyColor ?: Color.Gray,
                    radius = markerRadius,
                    center = Offset(x, y)
                )

                number?.let {
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 28f
                            textAlign = Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(
                            it.toString(),
                            x,
                            y - (paint.descent() + paint.ascent()) / 2,
                            paint)
                    }
                }
            }
        }
    }
}
