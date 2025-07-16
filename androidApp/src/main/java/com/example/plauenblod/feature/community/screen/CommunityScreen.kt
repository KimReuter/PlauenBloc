package com.example.plauenblod.feature.community.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plauenblod.android.util.FirestoreInstant
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.chat.component.ChatListItem
import com.example.plauenblod.feature.chat.component.UserListItem
import com.example.plauenblod.feature.chat.viewmodel.ChatViewModel
import com.example.plauenblod.feature.communityPost.components.CommentCard
import com.example.plauenblod.feature.communityPost.components.ConfirmDeleteDialog
import com.example.plauenblod.feature.communityPost.components.CreatePostSheet
import com.example.plauenblod.feature.communityPost.components.EditCommentSheet
import com.example.plauenblod.feature.communityPost.components.EditPostSheet
import com.example.plauenblod.feature.communityPost.components.PostCard
import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment
import com.example.plauenblod.feature.communityPost.viewModel.PinboardViewModel
import com.example.plauenblod.feature.user.model.UserDto
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import com.example.plauenblod.screen.ChatRoute
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CommunityScreen(
    userViewModel: UserViewModel = koinInject(),
    chatViewModel: ChatViewModel = koinInject(),
    pinBoardViewModel: PinboardViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    onUserClick: (UserDto) -> Unit,
    navController: NavController
) {
    val showSearchField = remember { mutableStateOf(false) }
    val allUsers by userViewModel.filteredUsers.collectAsState()
    val searchQuery by userViewModel.searchQuery.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val currentUserId by authViewModel.userId.collectAsState()
    val chatList by chatViewModel.chatList.collectAsState()
    val (selectedTab, setSelectedTab) = remember { mutableStateOf("Chats") }
    val isEnabled = true
    val isChatsSelected = selectedTab == "Chats"
    val isUsersSelected = selectedTab == "User"
    val isPinboardSelected = selectedTab == "Pinnwand"
    val pinboardPosts by pinBoardViewModel.posts.collectAsState()
    val postToEdit = remember { mutableStateOf<CommunityPost?>(null) }
    var postToDelete = remember { mutableStateOf<CommunityPost?>(null) }
    val commentToEdit = remember { mutableStateOf<PostComment?>(null) }
    var editingPost by remember { mutableStateOf<CommunityPost?>(null) }
    var expandedPostIdForComment by remember { mutableStateOf<String?>(null) }
    var commentText by remember { mutableStateOf("") }
    var commentToDelete = remember { mutableStateOf<Pair<String, PostComment>?>(null) }
    var showCreateSheet by remember { mutableStateOf(false) }
    var newPostContent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
        while (currentUserId == null) {
            delay(100)
        }
        Log.d("CommunityScreen", "👤 UserId verfügbar: $currentUserId → Lade Daten...")

        currentUserId?.let { uid ->
            chatViewModel.loadChatsForCurrentUser(uid)
            userViewModel.loadAllUsers()
        }
    }

    LaunchedEffect(currentUserId) {
        currentUserId?.let { uid ->
            userViewModel.loadUser(uid)
            pinBoardViewModel.loadPosts()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            TopAppBar(
                title = { Text("Community") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = {
                        showSearchField.value = !showSearchField.value
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Suche anzeigen"
                        )
                    }
                },
            )
            AnimatedVisibility(visible = showSearchField.value) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { userViewModel.updateSearchQuery(it) },
                    label = { Text("Nutzer:in suchen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { userViewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Suche zurücksetzen"
                                )
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {

                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = isChatsSelected,
                    onClick = { setSelectedTab("Chats") },
                    label = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Chats")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = isEnabled,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        labelColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = isEnabled,
                        selected = isChatsSelected,
                        borderColor = MaterialTheme.colorScheme.primary,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = isUsersSelected,
                    onClick = { setSelectedTab("User") },
                    label = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("User")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = isEnabled,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        labelColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = isEnabled,
                        selected = isUsersSelected,
                        borderColor = MaterialTheme.colorScheme.primary,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = isPinboardSelected,
                    onClick = { setSelectedTab("Pinnwand") },
                    label = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Pinnwand")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    enabled = isEnabled,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        labelColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = isEnabled,
                        selected = isPinboardSelected,
                        borderColor = MaterialTheme.colorScheme.primary,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (isChatsSelected) {
                        val filteredChats = chatList.filter { chat ->
                            searchQuery.isBlank() || chat.otherUser.userName?.contains(
                                searchQuery,
                                ignoreCase = true
                            ) == true
                        }

                        if (filteredChats.isNotEmpty()) {
                            items(filteredChats) { chatWithUser ->

                                val targetUserId = chatWithUser.otherUser.uid ?: return@items
                                val senderId = currentUserId ?: return@items

                                ChatListItem(
                                    userName = chatWithUser.otherUser.userName ?: "Unbekannt",
                                    lastMessage = chatWithUser.chat.lastMessage ?: "",
                                    lastMessageTime = chatWithUser.chat.getLastMessageInstant(),
                                    onClick = {
                                        navController.navigate(ChatRoute(senderId, targetUserId))
                                    },
                                    profileImageUrl = chatWithUser.otherUser.profileImageUrl
                                )
                            }
                        }
                    } else if (isUsersSelected) {
                        val filteredUsers = allUsers
                            .filter { it.uid != currentUserId }
                            .filter { user ->
                                searchQuery.isBlank() || user.userName?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) == true
                            }
                            .sortedBy { it.userName?.lowercase() }

                        if (filteredUsers.isEmpty()) {
                            item {
                                Text(
                                    "Dieser Nutzer ist nicht registriert.",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            items(filteredUsers) { user ->
                                UserListItem(
                                    user = user,
                                    onProfileClick = { onUserClick(user) },
                                    onStartChatClick = {
                                        val senderId = currentUserId ?: return@UserListItem
                                        val targetUserId = user.uid ?: return@UserListItem
                                        navController.navigate(ChatRoute(senderId, targetUserId))
                                    }
                                )
                            }
                        }
                    } else {
                        if (pinboardPosts.isEmpty()) {
                            item {
                                Text(
                                    "Noch keine Pinnwand-Beiträge.\nDrücke auf „+“, um einen neuen Post zu erstellen.",
                                    modifier = Modifier
                                        .fillParentMaxSize()
                                        .padding(24.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(pinboardPosts) { post ->
                                PostCard(
                                    post = post,
                                    currentUserId = pinBoardViewModel.currentUserId ?: return@items,
                                    isCommentFieldVisible = expandedPostIdForComment == post.id,
                                    onCommentFieldToggle = {
                                        expandedPostIdForComment =
                                            if (expandedPostIdForComment == post.id) null else post.id
                                    },
                                    onCommentSubmit = { comment ->
                                        val newComment = PostComment().apply {
                                            authorId   = currentUserId!!
                                            authorName = userViewModel.userName.value ?: "Unbekannt"
                                            content    = comment
                                            timestamp  = FirestoreInstant.fromInstant(Clock.System.now())
                                        }
                                        pinBoardViewModel.addCommentToPost(post.id, newComment)
                                        commentText = ""
                                        expandedPostIdForComment = null
                                    },
                                    commentText = commentText,
                                    onCommentTextChange = { commentText = it },
                                    onEditClick = { editingPost = it },
                                    onDeleteClick = { postToDelete.value = it }
                                )
                            }
                        }

                    }
                }
            }
        }
        if (isPinboardSelected) {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Neuer Post")
            }
        }

        postToEdit.value?.let { post ->
            EditPostSheet(
                post = post,
                onDismiss = { postToEdit.value = null },
                onSave = { updatedPost ->
                    pinBoardViewModel.updatePost(
                        postId = updatedPost.id,
                        newContent = updatedPost.content
                    )
                    postToEdit.value = null
                }
            )
        }

        postToDelete.value?.let { post ->
            ConfirmDeleteDialog(
                message = "Willst du diesen Beitrag wirklich löschen?",
                onConfirm = {
                    pinBoardViewModel.deletePostIfAuthorized(post)
                    postToDelete.value = null
                },
                onDismiss = { postToDelete.value = null }
            )
        }

        commentToEdit.value?.let { comment ->
            EditCommentSheet(
                comment = comment,
                onDismiss = { commentToEdit.value = null },
                onSave = { updatedComment ->
                    pinBoardViewModel.updateCommentIfAuthorized(
                        postId = comment.id,
                        comment = comment,
                        newContent = updatedComment
                    )
                    commentToEdit.value = null
                }
            )
        }

        commentToDelete.value?.let { comment ->
            ConfirmDeleteDialog(
                message = "Willst du diesen Kommentar wirklich löschen?",
                onConfirm = {
                    val (postId, comment) = commentToDelete.value ?: return@ConfirmDeleteDialog
                    pinBoardViewModel.deleteCommentIfAuthorized(
                        postId = postId,
                        comment = comment
                    )
                    commentToDelete.value = null
                },
                onDismiss = { commentToDelete.value = null }
            )
        }

        if (showCreateSheet) {
            CreatePostSheet(
                onDismiss = { showCreateSheet = false },
                onSave = { newContent ->
                    pinBoardViewModel.createPost(CommunityPost().apply {
                        authorId      = currentUserId!!
                        authorName    = userViewModel.userName.value ?: "Unbekannt"
                        authorImageUrl = userViewModel.userProfileImageUrl.value
                        content       = newContent
                        timestamp     = FirestoreInstant.fromInstant(Clock.System.now())
                    })
                    showCreateSheet = false
                }
            )
        }
    }
}