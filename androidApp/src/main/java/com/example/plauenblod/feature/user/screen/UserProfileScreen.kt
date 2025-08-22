package com.example.plauenblod.feature.user.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.plauenblod.android.R
import com.example.plauenblod.feature.user.component.UserStatsSection
import com.example.plauenblod.feature.user.model.UserDto
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    user: UserDto,
    onStartChat: (UserDto) -> Unit,
    userViewmodel: UserViewModel = koinInject()
) {
    val imageUrl = user.profileImageUrl ?: "https://via.placeholder.com/150"
    var showAllRoutes by rememberSaveable { mutableStateOf(false) }
    val routesToDisplay = if (showAllRoutes) user.completedRoutes else user.completedRoutes.take(3)
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onStartChat(user) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Chat, "Chat starten")
            }
        }
    ) { padding ->

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Box {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profilbild",
                    placeholder = painterResource(R.drawable.placeholderprofileimage),
                    error = painterResource(R.drawable.placeholderprofileimage),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )

            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = user.userName ?: "Unbekannter Boulderer",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Persönliche Informationen",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            Spacer(Modifier.height(8.dp))

            Text(
                text = "🏆 ${user.totalPoints} Punkte",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Über mich",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = user.bio?.takeIf { it.isNotBlank() } ?: "Noch kein Text hinterlegt.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            UserStatsSection(user)

            Spacer(Modifier.height(16.dp))

            Text(
                "Abgeschlossene Routen",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )

            Box(
                Modifier
                    .height(2.dp)
                    .fillMaxWidth(0.4f)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(Modifier.height(12.dp))

            if (user.completedRoutes.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        routesToDisplay.forEach { route ->
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "🧩 ${route.routeName ?: "Unbekannt"}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "• ${route.attempts ?: "?"} Versuche, am ${route.date ?: "-"}\n• Schwierigkeit: ${route.difficulty ?: "?"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (user.completedRoutes.size > 3) {
                            Text(
                                text = if (showAllRoutes) "Weniger anzeigen ▲" else "Alle anzeigen ▼",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .align(Alignment.End)
                                    .clickable { showAllRoutes = !showAllRoutes }
                            )
                        }
                    }
                }
            } else {
                Text(
                    "Noch keine Routen abgeschlossen.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}