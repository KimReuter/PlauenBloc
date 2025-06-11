package com.example.plauenblod.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plauenblod.component.navigation.TabItem
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object MapRoute

@Serializable
object ListRoute

@Serializable
object CommunityRoute

@Serializable
object SettingsRoute

@Serializable
object AuthRoute


@Composable
fun AppStart() {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableStateOf(TabItem.HOME) }

    Scaffold (
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
                                contentDescription = "TabItem"
                            )
                        },
                        label = {
                            Text(text = tabItem.tabTitle)
                        }
                    )

                }
            }
        }
    ){ innerPadding ->

        NavHost(
            navController = navController,
            startDestination = selectedTab.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeRoute> {
                HomeScreen()
            }

            composable<AuthRoute> {
                AuthScreen(onLoginSuccess = { navController.navigate(HomeRoute) })
            }

            composable<SettingsRoute> {
                SettingsScreen()
            }

            composable<MapRoute> {
                MapScreen()
            }

            composable<ListRoute> {
                SettingsScreen()
            }

            composable<CommunityRoute> {
                SettingsScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    AppStart()
}