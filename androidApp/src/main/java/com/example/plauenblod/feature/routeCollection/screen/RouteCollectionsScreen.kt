package com.example.plauenblod.feature.routeCollection.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.chat.viewmodel.ChatViewModel
import com.example.plauenblod.feature.routeCollection.component.RouteCollectionCard
import com.example.plauenblod.feature.routeCollection.viewModel.RouteCollectionViewModel
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import com.example.plauenblod.screen.ChatRoute
import com.example.plauenblod.screen.CollectionDetailRoute
import com.example.plauenblod.screen.EditCollectionRoute
import com.example.plauenblod.screen.NewCollectionRoute
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteCollectionsScreen(
    authViewModel: AuthViewModel = koinInject(),
    userViewModel: UserViewModel = koinInject(),
    chatViewModel: ChatViewModel = koinInject(),
    navController: NavController,
    routeCollectionViewModel: RouteCollectionViewModel = koinInject()
) {
    val currentUserId by authViewModel.userId.collectAsState()
    if (currentUserId == null) return
    val senderId: String = currentUserId!!

    val allUsers by userViewModel.allUsers.collectAsState()
    val publicCollections by routeCollectionViewModel.publicCollections.collectAsState()
    val userCollections by routeCollectionViewModel.userCollections.collectAsState()
    val isLoading by routeCollectionViewModel.isLoading.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("Öffentlich", "Meine Sammlungen")

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
        routeCollectionViewModel.loadPublicCollections()
        currentUserId?.let { routeCollectionViewModel.loadUserCollections(it) }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sammlungen") },
                actions = {
                    IconButton(onClick = { navController.navigate(NewCollectionRoute) }) {
                        Icon(Icons.Default.Search, contentDescription = "Sammlung suchen")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { navController.navigate(NewCollectionRoute) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Neue Sammlung")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(visible = isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Suche…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val itemsToShow = if (selectedTab == 0) publicCollections else userCollections
                if (itemsToShow.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Keine Sammlungen gefunden.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemsToShow) { collection ->
                            RouteCollectionCard(
                                collection = collection,
                                currentUserId = currentUserId,
                                onClick = { navController.navigate(CollectionDetailRoute(collection.id)) },
                                onEdit = { navController.navigate(EditCollectionRoute(collection.id)) },
                                onDelete = {
                                    routeCollectionViewModel.deleteCollection(
                                        collection.id,
                                        currentUserId!!
                                    )
                                },
                                onShare        = { message, recipientId, collectionId ->
                                    chatViewModel.sendMessage(
                                        senderId    = senderId,
                                        recipientId = recipientId,
                                        messageText = "$message\n\nSammlung: ${collection.name}"
                                    )
                                    navController.navigate(ChatRoute(senderId, recipientId))
                                },
                                allUsers = allUsers
                            )
                        }
                    }
                }

            }
        }
    }
}

