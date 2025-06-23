package com.example.plauenblod.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.plauenblod.screen.CommunityRoute
import com.example.plauenblod.screen.HomeRoute
import com.example.plauenblod.screen.ListRoute
import com.example.plauenblod.screen.MapRoute
import com.example.plauenblod.screen.SettingsRoute

enum class TabItem(
    val route: Any,
    val tabIcon: ImageVector
) {
    HOME(HomeRoute, Icons.Default.Home),
    MAP(MapRoute, Icons.Default.Place),
    LIST(ListRoute, Icons.Default.List),
    COMMUNITY(CommunityRoute, Icons.Default.Person),
    SETTINGS(SettingsRoute, Icons.Default.Settings)
}