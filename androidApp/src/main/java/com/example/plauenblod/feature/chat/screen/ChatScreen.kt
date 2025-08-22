package com.example.plauenblod.feature.chat.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plauenblod.feature.chat.component.ChatInput
import com.example.plauenblod.feature.chat.component.DropdownItem
import com.example.plauenblod.feature.chat.component.EmojiGrid
import com.example.plauenblod.feature.chat.component.MessagesList
import com.example.plauenblod.feature.chat.model.Message
import com.example.plauenblod.feature.chat.viewmodel.ChatViewModel
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import com.example.plauenblod.screen.BoulderDetailRoute
import com.example.plauenblod.screen.CollectionDetailRoute
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    targetUserId: String,
    chatViewModel: ChatViewModel = koinInject(),
    userViewModel: UserViewModel = koinInject(),
    navController: NavController
) {
    val userState by userViewModel.userState.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    var selectedMessage by rememberSaveable { mutableStateOf<Message?>(null) }
    var showMessageActions by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var editMode by rememberSaveable { mutableStateOf(false) }
    var messageToEdit by rememberSaveable { mutableStateOf<Message?>(null) }
    val sharedRoutes by chatViewModel.sharedRoutes.collectAsState()
    val sharedCollections by chatViewModel.sharedCollections.collectAsState()

    LaunchedEffect(targetUserId) {
        userViewModel.loadUser(targetUserId)
        chatViewModel.initChat(currentUserId, targetUserId)
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        userState != null -> {
            val targetUser = userState!!

            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = {
                        Text(text = targetUser.userName!!)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack()}) {
                            Icon(Icons.Default.ArrowBack, "Zurück")
                        }
                    }
                )

                MessagesList(
                    messages = messages,
                    currentUserId = currentUserId,
                    selectedMessage = selectedMessage,
                    onLongClick = {
                        selectedMessage = it
                        showMessageActions = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    onRouteClick = { routeId ->
                        navController.navigate(BoulderDetailRoute(routeId))
                    } ,
                    onCollectionClick  = { navController.navigate(CollectionDetailRoute(it)) },
                    sharedRoutes = sharedRoutes,
                    sharedCollections  = sharedCollections,
                )

                ChatInput(
                    initialText = messageToEdit?.messageText ?: "",
                    onSend = { text ->
                        if (editMode && messageToEdit != null) {
                            chatViewModel.updateMessage(messageToEdit!!.id!!, text)
                        } else {
                            chatViewModel.sendMessage(text, currentUserId, targetUserId)
                        }
                        editMode = false
                        messageToEdit = null
                        selectedMessage = null
                    }
                )
            }

            if (showMessageActions && selectedMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            showMessageActions = false
                            selectedMessage = null
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Surface(
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                listOf("👍", "❤️", "😂", "😮", "😢", "🙏", "🌙").forEach { emoji ->
                                    Text(
                                        text = emoji,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clickable {
                                                selectedMessage?.let {
                                                    chatViewModel.reactToMessage(it, emoji)
                                                }
                                                showMessageActions = false
                                                selectedMessage = null
                                            },
                                        fontSize = 24.sp
                                    )
                                }
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            bottomSheetState.show()
                                        }
                                    }
                                ) {
                                    Text("➕", fontSize = 24.sp, modifier = Modifier.padding(start = 8.dp)) }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 8.dp,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .padding(48.dp)
                        ) {
                            Column {
                                DropdownItem("Bearbeiten") {
                                    if (selectedMessage?.senderId == currentUserId) {
                                        editMode = true
                                        messageToEdit = selectedMessage
                                        showMessageActions = false
                                    }
                                }
                                DropdownItem("Löschen", isDestructive = true) {
                                    val messageId = selectedMessage?.id
                                    if (messageId != null) {
                                        chatViewModel.deleteMessage(messageId)
                                    }
                                    showMessageActions = false
                                    selectedMessage = null
                                }
                            }
                        }
                    }
                }

                if (bottomSheetState.isVisible) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            scope.launch { bottomSheetState.hide() }
                        },
                        sheetState = bottomSheetState
                    ) {
                        EmojiGrid { emoji ->
                            selectedMessage?.let {
                                chatViewModel.reactToMessage(it, emoji)
                            }
                            scope.launch {
                                bottomSheetState.hide()
                                showMessageActions = false
                                selectedMessage = null
                            }
                        }
                    }
                }
            }
        }

        else -> {
            Text("Fehler beim Laden des Benutzers")
        }
    }
}