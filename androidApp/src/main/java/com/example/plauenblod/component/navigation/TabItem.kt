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
    val tabTitle: String,
    val tabIcon: ImageVector
) {
    HOME(HomeRoute, "Home", Icons.Default.Home),
    MAP(MapRoute, "Karte", Icons.Default.Place),
    LIST(ListRoute, "Listen", Icons.Default.List),
    COMMUNITY(CommunityRoute, "Community", Icons.Default.Person),
    SETTINGS(SettingsRoute, "Einstellungen", Icons.Default.Settings)
}