package com.example.plauenblod.feature.routeCollection.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.chat.component.ShareDialog
import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import com.example.plauenblod.feature.user.model.UserDto

@Composable
fun RouteCollectionCard(
    collection: RouteCollection,
    currentUserId: String?,
    allUsers: List<UserDto>,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: (message: String, recipientId: String, collectionId: String) -> Unit,
    onLike: (collectionId: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isOwner = currentUserId == collection.creatorId
    var expandedDescription by remember { mutableStateOf(false) }
    val description = collection.description ?: "Keine Beschreibung"
    val charThreshold = 100
    var showShareDialog by remember { mutableStateOf(false) }

    if (showShareDialog) {
        ShareDialog(
            itemTypeName = "Sammlung",
            itemId = collection.id,
            allUsers = allUsers,
            defaultMessage = "Schau dir mal diese Sammlung an!",
            onDismiss = { showShareDialog = false },
            onSend = { message, recipientId, itemId ->
                onShare(message, recipientId, itemId)
                showShareDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = collection.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (expandedDescription || description.length <= charThreshold)
                            description
                        else
                            description.take(charThreshold) + "…",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (description.length > charThreshold) {
                        TextButton(
                            onClick = { expandedDescription = !expandedDescription },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(if (expandedDescription) "weniger" else "mehr")
                        }
                    }
                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mehr Optionen"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (isOwner) {
                            DropdownMenuItem(
                                text = { Text("Bearbeiten") },
                                onClick = {
                                    expanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Löschen") },
                                onClick = {
                                    expanded = false
                                    onDelete()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Teilen") },
                            onClick = {
                                expanded = false
                                showShareDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Routen: ${collection.routeCount}")
                IconButton(onClick = { onLike(collection.id) }) {
                    Icon(
                        imageVector = if (collection.likedBy.contains(currentUserId))
                            Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(text = "${collection.likesCount}")
            }
        }
    }
}