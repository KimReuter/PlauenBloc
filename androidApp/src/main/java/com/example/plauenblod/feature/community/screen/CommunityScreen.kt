package com.example.plauenblod.feature.community.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.chat.component.ChatListItem
import com.example.plauenblod.feature.chat.viewmodel.ChatViewModel
import com.example.plauenblod.feature.user.model.UserDto
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import com.example.plauenblod.screen.ChatRoute
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    userViewModel: UserViewModel = koinInject(),
    chatViewModel: ChatViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    onUserClick: (UserDto) -> Unit,
    navController: NavController
) {
    val allUsers by userViewModel.filteredUsers.collectAsState()
    val searchQuery by userViewModel.searchQuery.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val currentUserId by authViewModel.userId.collectAsState()
    val chatList by chatViewModel.chatList.collectAsState()

    LaunchedEffect(Unit) {
        while (currentUserId == null) {
            kotlinx.coroutines.delay(100)
        }
        Log.d("CommunityScreen", "👤 UserId verfügbar: $currentUserId → Lade Daten...")

        currentUserId?.let { uid ->
            chatViewModel.loadChatsForCurrentUser(uid)
            userViewModel.loadAllUsers()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Community") },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { userViewModel.updateSearchQuery(it) },
            label = { Text("Nutzer:in suchen") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                if (searchQuery.isBlank()) {
                    items(chatList) { chatWithUser ->
                        val targetUserId = chatWithUser.otherUser.uid ?: return@items
                        val senderId = currentUserId ?: return@items

                        ChatListItem(
                            userName = chatWithUser.otherUser.userName ?: "Unbekannt",
                            lastMessage = chatWithUser.chat.lastMessage ?: "",
                            onClick = {
                                navController.navigate(ChatRoute(senderId, targetUserId))
                            },
                            profileImageUrl = chatWithUser.otherUser.profileImageUrl
                        )
                    }
                } else {
                    val sortedUsers = allUsers
                        .filter { it.uid != currentUserId }
                        .sortedBy { it.userName?.lowercase() }

                    items(sortedUsers) { user ->
                        ChatListItem(
                            userName = user.userName ?: "Unbekannt",
                            lastMessage = "",
                            onClick = {
                                val senderId = currentUserId ?: return@ChatListItem
                                val targetUserId = user.uid ?: return@ChatListItem
                                navController.navigate(ChatRoute(senderId, targetUserId))
                            },
                            profileImageUrl = user.profileImageUrl

                        )
                    }
                }
            }
        }
    }
}