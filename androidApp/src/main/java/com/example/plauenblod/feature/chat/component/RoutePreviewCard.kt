package com.example.plauenblod.feature.chat.component

import androidx.compose.runtime.Composable
import com.example.plauenblod.feature.route.model.Route

@Composable
fun RoutePreviewCard(
    route: Route,
    sharedMessage: String?,
    onRouteClick: () -> Unit
) {
    SharedPreviewCard(
        title = route.name,
        details = listOf(
            "Schwierigkeit: ${route.difficulty}",
            "Erstellt von: ${route.setter}"
        ),
        stats = emptyList(),
        sharedMessage = sharedMessage,
        onClick = onRouteClick
    )
}