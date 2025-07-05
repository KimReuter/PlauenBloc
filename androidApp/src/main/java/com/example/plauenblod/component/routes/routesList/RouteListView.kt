package com.example.plauenblod.component.routes.routesList

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plauenblod.extension.displayName
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.model.routeProperty.Sector

@Composable
fun RouteListView(
    groupedRoutes: Map<Sector, List<Route>>,
    onRouteClick: (Route) -> Unit,
    onLongRouteClick: (Route) -> Unit,
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        groupedRoutes.forEach { (sector, routesInSector) ->
            item {
                Text(
                    text = sector.displayName(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            items(routesInSector) { route ->
                RouteListItem(
                    route = route,
                    onRouteClick = { onRouteClick(route) },
                    onLongClick = { onLongRouteClick(route) }
                )
            }
        }
    }
}