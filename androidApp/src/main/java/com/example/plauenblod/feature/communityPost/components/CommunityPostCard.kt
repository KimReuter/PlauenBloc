package com.example.plauenblod.feature.communityPost.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.plauenblod.android.util.toRelativeTimeString
import com.example.plauenblod.feature.communityPost.model.CommunityPost

@Composable
fun PostCard(
    post: CommunityPost,
    currentUserId: String,
    isCommentFieldVisible: Boolean,
    onCommentFieldToggle: () -> Unit,
    onCommentSubmit: (String) -> Unit,
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    onEditClick: (CommunityPost) -> Unit,
    onDeleteClick: (CommunityPost) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    post.authorImageUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Profilbild",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Column {
                        Text(text = post.authorName, fontWeight = FontWeight.Bold)
                        Text(
                            text = post.timestamp.toRelativeTimeString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                if (post.authorId == currentUserId) {
                    var expanded by remember { mutableStateOf(false) }

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Mehr Optionen")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Bearbeiten") },
                                onClick = {
                                    expanded = false
                                    onEditClick(post)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Löschen") },
                                onClick = {
                                    expanded = false
                                    onDeleteClick(post)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = post.content)

            Spacer(modifier = Modifier.height(8.dp))

            post.comments.forEach { comment ->
                CommentCard(
                    comment = comment,
                    currentUserId = currentUserId,
                    onEditClick = {  },
                    onDeleteClick = { }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(onClick = onCommentFieldToggle) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kommentieren")
            }

            if (isCommentFieldVisible) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onCommentTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Dein Kommentar…") },
                    trailingIcon = {
                        IconButton(onClick = {
                            onCommentSubmit(commentText)
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Kommentar abschicken")
                        }
                    }
                )
            }
        }
    }
}