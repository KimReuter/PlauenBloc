package com.example.plauenblod.screen

import android.graphics.Paint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.plauenblod.android.R
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    userViewModel: UserViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject()
) {
    val userId = authViewModel.userId.collectAsState().value
    val userState by userViewModel.userState.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isLoading by userViewModel.isLoading.collectAsState()
    val imageUrl = userViewModel.userProfileImageUrl ?: "https://via.placeholder.com/150"

    LaunchedEffect(userId) {
        userId?.let { userViewModel.loadUser(userId) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        if (errorMessage != null) {
            LaunchedEffect(errorMessage) {
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage!!)
                    userViewModel.clearError()
                }
            }
        }

        if (isLoading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            userState?.let { user ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier

                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Profilbild",
                            placeholder = painterResource(R.drawable.placeholderprofileimage),
                            contentScale = ContentScale.Fit,
                            error = painterResource(R.drawable.placeholderprofileimage),
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Profilbild ändern",
                                tint = MaterialTheme.colorScheme.primary
                                )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Persönliche Informationen",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = user.userName ?: "Unbekannter Boulderer",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            IconButton(
                                onClick = { }
                            ) {
                                Icon(Icons.Default.Edit, "Name ändern")
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "🏆 ${user.totalPoints} Punkte",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Abgeschlossene Routen Überschrift
                    Text(
                        "Abgeschlossene Routen",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .then(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )
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
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                user.completedRoutes.forEach { route ->
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
            } ?: Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Keine Daten geladen")
            }
        }
    }
}