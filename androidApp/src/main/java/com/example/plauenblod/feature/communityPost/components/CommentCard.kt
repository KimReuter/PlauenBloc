package com.example.plauenblod.feature.communityPost.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.plauenblod.feature.communityPost.model.PostComment
import org.w3c.dom.Comment


@Composable
fun CommentCard(
    comment: PostComment,
    currentUserId: String?,
    onAuthorClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isAuthor = comment.authorId == currentUserId
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        ConfirmDeleteDialog(
            message = "Willst du diesen Kommentar wirklich löschen?",
            onConfirm = {
                onDeleteClick()
                showConfirmDelete = false
            },
            onDismiss = { showConfirmDelete = false }
        )
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    comment.authorImageUrl?.let { url ->
                        Log.d("CommentCard", "authorImageUrl for comment ${comment.id} = ${comment.authorImageUrl}")
                        AsyncImage(
                            model = url,
                            contentDescription = "Profilbild",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .clickable { onAuthorClick(comment.authorId) },
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Column {

                        Text(
                            text = comment.authorName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onAuthorClick(comment.authorId) }
                        )

                        Text(
                            text = comment.content,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (isAuthor) {
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
                            DropdownMenuItem(
                                text = { Text("Bearbeiten") },
                                onClick = {
                                    expanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Löschen") },
                                onClick = {
                                    expanded = false
                                    showConfirmDelete = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}