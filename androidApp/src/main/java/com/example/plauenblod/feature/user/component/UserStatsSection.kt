package com.example.plauenblod.feature.user.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.user.model.UserDto

@Composable
fun UserStatsSection(user: UserDto) {
    val totalRoutes = user.completedRoutes.size
    val totalAttempts = user.completedRoutes.sumOf { it.attempts ?: 0 }
    val avgAttempts = if (totalRoutes > 0) totalAttempts.toDouble() / totalRoutes else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            "Statistiken",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatBox(title = "Routen", value = totalRoutes.toString())
            StatBox(title = "Ø Versuche", value = String.format("%.1f", avgAttempts))
            StatBox(title = "Punkte", value = user.totalPoints?.toString() ?: "0")
        }
    }
}

