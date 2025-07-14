package com.example.plauenblod.feature.user.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.plauenblod.feature.user.component.UserStatsSection
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import com.example.plauenblod.android.R
import com.example.plauenblod.extension.toUserFriendlyName
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.imageUpload.model.ImageUploadState
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnProfileScreen(
    userViewmodel: UserViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    onImageUpload: (Uri) -> Unit,
    onLogout: () -> Unit
) {
    val userState by userViewmodel.userState.collectAsState()
    val currentUser = userState ?: return
    val userId by authViewModel.userId.collectAsState()
    val imageUrl = currentUser.profileImageUrl ?: "https://via.placeholder.com/150"
    var isEditingName by rememberSaveable { mutableStateOf(false) }
    var nameText by rememberSaveable { mutableStateOf(currentUser.userName ?: "") }
    var isEditingBio by rememberSaveable { mutableStateOf(false) }
    var bioText by rememberSaveable { mutableStateOf(currentUser.bio ?: "") }
    var showAllRoutes by rememberSaveable { mutableStateOf(false) }
    val routesToDisplay = if (showAllRoutes) currentUser.completedRoutes else currentUser.completedRoutes.take(3)
    val scrollState = rememberScrollState()
    val uploadState = userViewmodel.uploadState
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("ProfileImage", "📷 URI empfangen: $uri")
            selectedImageUri = uri

            val uid = currentUser.uid
            if (uid != null) {
                Log.d("ProfileImage", "✅ UID gefunden: $uid – Upload wird gestartet")
                userViewmodel.uploadProfileImage(uid, uri)
            } else {
                Log.d("ProfileImage", "❌ UID ist null – Upload wird abgebrochen")
            }
        } else {
            Log.d("ProfileImage", "❌ Keine URI empfangen – Auswahl evtl. abgebrochen?")
        }
    }

    LaunchedEffect(userId) {
        userId?.let { userViewmodel.loadUser(it) }
    }

    LaunchedEffect(uploadState) {
        if (uploadState is ImageUploadState.Success) {
            Log.d("ProfileImage", "🎉 Upload war erfolgreich – lade User neu")
            userId?.let { userViewmodel.loadUser(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") }
            )
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
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
                IconButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Bild hochladen",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isEditingName) {
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Dein Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        currentUser.uid?.let { uid ->
                            userViewmodel.updateUserInfo(uid = uid, newName = nameText, newBio = currentUser.bio ?: "")
                        }
                        isEditingName = false
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Speichern")
                    }
                } else {
                    Text(
                        text = nameText.ifBlank { "Unbekannter Boulderer" },
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { isEditingName = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Persönliche Informationen",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            Spacer(Modifier.height(8.dp))

            Text(
                text = "🏆 ${currentUser.totalPoints} Punkte",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Über mich",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (isEditingBio) {
                    OutlinedTextField(
                        value = bioText,
                        onValueChange = { bioText = it },
                        label = { Text("Über mich") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            currentUser.uid?.let { uid ->
                                userViewmodel.updateUserInfo(uid = uid, newName = currentUser.userName ?: "", newBio = bioText)
                            }
                            isEditingBio = false
                        }) {
                            Icon(Icons.Default.Check, contentDescription = "Speichern")
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = bioText.ifBlank { "Noch kein Text hinterlegt." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { isEditingBio = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Bio bearbeiten")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            UserStatsSection(currentUser)

            Spacer(Modifier.height(16.dp))

            Text(
                "Abgeschlossene Routen",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            if (currentUser.completedRoutes.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
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
                                    text = "• ${route.attempts ?: "?"} Versuche, am ${route.date ?: "-"}\n• Schwierigkeit: ${route.difficulty?.toUserFriendlyName() ?: "?"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (currentUser.completedRoutes.size > 3) {
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