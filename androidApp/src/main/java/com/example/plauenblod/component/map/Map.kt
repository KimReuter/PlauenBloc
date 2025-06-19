package com.example.plauenblod.component.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavHostController
import com.example.plauenblod.android.R
import com.example.plauenblod.extension.toColor
import com.example.plauenblod.model.Route
import com.example.plauenblod.screen.BoulderDetailRoute

@Composable
fun FirstHallMapScreen(
    routes: List<Route>,
    navController: NavHostController
) {
    BoulderMap(
        imageResId = R.drawable.boulderhalle_grundriss_vordere_halle_kleiner,
        routes = routes,
        enableZoom = true,
        onTap = { },
        onRouteClick = { route ->
            navController.navigate(BoulderDetailRoute(route.id))
        }
    )
}


@Composable
fun SecondHallMapScreen(
    routes: List<Route>,
    navController: NavHostController
) {
    BoulderMap(
        imageResId = R.drawable.boulderhalle_grundriss_hinterehalle_kleiner,
        routes = routes,
        enableZoom = true,
        onTap = { },
        onRouteClick = { route ->
            navController.navigate(BoulderDetailRoute(route.id))
        }
    )
}

