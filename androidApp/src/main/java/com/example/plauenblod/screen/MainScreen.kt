package com.example.plauenblod.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.plauenblod.component.navigation.TabItem
import kotlinx.serialization.Serializable

@Serializable
object AuthRoute

@Serializable
object DashboardRoute

@Serializable
object BoulderRoute

@Serializable
object CollectionRoute

@Serializable
object CommunityRoute

@Serializable
object UserRoute

@Serializable
data class BoulderDetailRoute(
    val routeId: String
)

@Composable
fun AppStart(
) {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableStateOf(TabItem.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                TabItem.entries.forEach { tabItem ->
                    NavigationBarItem(
                        selected = selectedTab == tabItem,
                        onClick = { selectedTab = tabItem },
                        icon = {
                            Icon(
                                imageVector = tabItem.tabIcon,
                                contentDescription = "TabItem",
                                tint = if (selectedTab == tabItem) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = selectedTab.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<AuthRoute> {
                AuthScreen(onLoginSuccess = { navController.navigate(DashboardRoute) })
            }

            composable<DashboardRoute> {
                HomeScreen()
            }

            composable<BoulderRoute> {
                RouteScreen(navController = navController)
            }

            composable<CollectionRoute> {
                SettingsScreen()
            }

            composable<CommunityRoute> {
                SettingsScreen()
            }

            composable<UserRoute> {
                UserScreen()
            }

            composable<BoulderDetailRoute> { backStackEntry ->
                val routeArgs = backStackEntry.toRoute<BoulderDetailRoute>()
                RouteDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    routeId = routeArgs.routeId
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    AppStart()
}