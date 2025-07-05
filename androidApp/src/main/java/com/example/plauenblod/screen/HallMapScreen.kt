package com.example.plauenblod.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.plauenblod.android.R
import com.example.plauenblod.component.map.BoulderMap
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.Route

@Composable
fun HallMapScreen(
    hall: HallSection,
    routes: List<Route>,
    refreshKey: Int,
    navController: NavHostController,
    onRouteLongClick: (Route) -> Unit
) {
    when (hall) {
        HallSection.FRONT ->
            BoulderMap(
                imageResId = R.drawable.boulderhalle_grundriss_vordere_halle_kleiner,
                routes = routes,
                enableZoom = true,
                onTap = { },
                onRouteClick = { route ->
                    navController.navigate(BoulderDetailRoute(route.id))
                },
                onRouteLongClick = onRouteLongClick
            )
        HallSection.BACK ->
            BoulderMap(
                imageResId = R.drawable.boulderhalle_grundriss_hinterehalle_kleiner,
                routes = routes,
                enableZoom = true,
                onTap = { },
                onRouteClick = { route ->
                    navController.navigate(BoulderDetailRoute(route.id))
                },
                onRouteLongClick = onRouteLongClick
            )
    }
}