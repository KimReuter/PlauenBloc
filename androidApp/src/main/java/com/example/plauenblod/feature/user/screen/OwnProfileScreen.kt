package com.example.plauenblod.feature.user.screen

import com.example.plauenblod.feature.user.component.ProfileHeader
import com.example.plauenblod.feature.user.component.UserStatsSection
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.user.model.UserDto

@Composable
fun OwnProfileScreen(
    user: UserDto,
    allRoutes: List<Route>,
    onEditProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileHeader(user)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onEditProfileClick) {
            Text("Profil bearbeiten")
        }

        Spacer(modifier = Modifier.height(32.dp))

        UserStatsSection(user)

    }
}