package com.example.plauenblod.feature.communityPost.components

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.plauenblod.android.util.toRelativeTimeString
import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment
import com.example.plauenblod.feature.communityPost.viewModel.PinboardViewModel
import org.koin.compose.koinInject

@Composable
fun PostCard(
    post: CommunityPost,
    currentUserId: String,
    isCommentFieldVisible: Boolean,
    commentText: String,
    onCommentFieldToggle: () -> Unit,
    onCommentTextChange: (String) -> Unit,
    onCommentSubmit: (String) -> Unit,
    editingPostId: String?,
    editingCommentId: String?,
    editingCommentText: String,
    onEditingCommentTextChange: (String) -> Unit,
    onCommentStartEdit: (postId: String, comment: PostComment) -> Unit,
    onCommentCancelEdit: () -> Unit,
    onCommentSaveEdit: (postId: String, comment: PostComment, newText: String) -> Unit,
    onCommentDelete: (postId: String, comment: PostComment) -> Unit,
    onPostAuthorClick: (String) -> Unit,
    onCommentAuthorClick: (String) -> Unit,
    onEditPost: (CommunityPost) -> Unit,
    onDeletePost: (CommunityPost) -> Unit,
    pinboardViewModel: PinboardViewModel = koinInject()
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onPostAuthorClick(post.authorId) }
                ) {
                    post.authorImageUrl?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Profilbild",
                            contentScale = ContentScale.Crop,
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
                                    onEditPost(post)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Löschen") },
                                onClick = {
                                    expanded = false
                                    onDeletePost(post)
                                }
                            )
                        }
                    }
                }
            }

            Text(text = post.content)

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EmojiReactionsBar(
                    reactions = post.reactions,
                    onReact = { emoji ->
                        pinboardViewModel.toggleReaction(post.id, emoji)
                    },
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onCommentFieldToggle) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Kommentar schreiben"
                    )
                }

                if (post.comments.isNotEmpty()) {
                    val count = post.comments.size
                    Text(
                        text = "$count ${if (count == 1) "Kommentar" else "Kommentare"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            post.comments.forEach { comment ->
                if (editingPostId == post.id && editingCommentId == comment.id) {
                    OutlinedTextField(
                        value = editingCommentText,
                        onValueChange = onEditingCommentTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Row {
                                IconButton(onClick = {
                                    onCommentSaveEdit(
                                        post.id,
                                        comment,
                                        editingCommentText
                                    )
                                }) {
                                    Icon(Icons.Default.Send, contentDescription = "Speichern")
                                }
                                IconButton(onClick = onCommentCancelEdit) {
                                    Icon(Icons.Default.Close, contentDescription = "Abbrechen")
                                }
                            }
                        }
                    )
                } else {
                    Divider()
                    CommentCard(
                        comment = comment,
                        currentUserId = currentUserId,
                        onAuthorClick = onCommentAuthorClick,
                        onEditClick = { onCommentStartEdit(post.id, comment) },
                        onDeleteClick = { onCommentDelete(post.id, comment) },
                    )
                }
            }

            if (isCommentFieldVisible) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = onCommentTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Dein Kommentar…") },
                    shape = RoundedCornerShape(12.dp),
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