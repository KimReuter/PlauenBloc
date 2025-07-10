package com.example.plauenblod.component.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.plauenblod.screen.BoulderRoute
import com.example.plauenblod.screen.CollectionRoute
import com.example.plauenblod.screen.CommunityRoute
import com.example.plauenblod.screen.DashboardRoute
import com.example.plauenblod.screen.OwnProfileRoute

enum class TabItem(
    val route: Any,
    val tabIcon: ImageVector
) {
    HOME(DashboardRoute, Icons.Default.Home),
    BOULDER(BoulderRoute, Icons.Default.Place),
    COLLECTION(CollectionRoute, Icons.Default.List),
    COMMUNITY(CommunityRoute, Icons.Default.Chat),
    USER(OwnProfileRoute, Icons.Default.Person)
}
