package com.example.plauenblod.feature.route.component.routesList

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.extension.toUserFriendlyName
import com.example.plauenblod.feature.route.model.Route

@Composable
fun RouteListItem(
    route: Route,
    onRouteClick: (Route) -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onRouteClick(route) },
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        DifficultyCircle(difficulty = route.difficulty, number = route.number)

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
        ) {
            Text(
                text = route.name,
                style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Grifffarbe: ${route.holdColor.toUserFriendlyName()}",
                style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Schrauber: ${route.setter}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary)
        }
    }
}