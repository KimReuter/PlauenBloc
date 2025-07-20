package com.example.plauenblod.feature.routeCollection.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
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
import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import com.example.plauenblod.feature.routeCollection.viewModel.RouteCollectionViewModel
import com.example.plauenblod.feature.routeCollection.viewModel.RouteSelectionViewModel
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
    routeSelectionViewModel: RouteSelectionViewModel = koinInject(),
    navController: NavController,
    routeCollectionViewModel: RouteCollectionViewModel = koinInject()
) {
    val currentUserId by authViewModel.userId.collectAsState()
    if (currentUserId == null) return
    val senderId: String = currentUserId!!

    val allUsers by userViewModel.allUsers.collectAsState()
    val allRoutes by routeSelectionViewModel.allRoutes.collectAsState()
    val publicCollections by routeCollectionViewModel.publicCollections.collectAsState()
    val userCollections by routeCollectionViewModel.userCollections.collectAsState()
    val isLoading by routeCollectionViewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Meistgeliked") }
    val filterOptions = listOf("Meistgeliked", "Neueste", "Größte", "Schwierigkeit")
    val tabs = listOf("Öffentlich", "Meine Sammlungen")

    fun averageDifficulty(collection: RouteCollection): Double {
        val difficulties = allRoutes
            .filter { it.id in collection.routeIds }
            .map { it.difficulty.ordinal.toDouble() }
        return if (difficulties.isEmpty()) 0.0 else difficulties.average()
    }

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
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(Icons.Default.Search, contentDescription = "Sammlung suchen")
                    }
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtern")
                    }
                    IconButton(onClick = { navController.navigate(NewCollectionRoute) }) {
                        Icon(Icons.Default.Add, contentDescription = "Sammlung erstellen")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedFilter = option
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            )
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
                    shape = RoundedCornerShape(12.dp),
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
                val baseList = if (selectedTab == 0) publicCollections else userCollections
                val filteredBySearch = if (searchQuery.isBlank()) baseList
                else baseList.filter { it.name.contains(searchQuery, true) }

                val itemsToShow = when (selectedFilter) {
                    "Meistgeliked"  -> filteredBySearch.sortedByDescending { it.likesCount }
                    "Neueste"       -> filteredBySearch.sortedByDescending { it.updatedAt.seconds }
                    "Größte"        -> filteredBySearch.sortedByDescending { it.routeIds.size }
                    "Schwierigkeit" -> filteredBySearch.sortedByDescending { averageDifficulty(it) }
                    else            -> filteredBySearch
                }

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
                                currentUserId = senderId,
                                onClick = { navController.navigate(CollectionDetailRoute(collection.id)) },
                                onEdit = { navController.navigate(EditCollectionRoute(collection.id)) },
                                onDelete = {
                                    routeCollectionViewModel.deleteCollection(
                                        collection.id,
                                        currentUserId!!
                                    )
                                },
                                onShare = { message, recipientId, collectionId ->
                                    chatViewModel.sendMessage(
                                        senderId = senderId,
                                        recipientId = recipientId,
                                        messageText = "$message\n\nSammlung: ${collection.name}"
                                    )
                                    navController.navigate(ChatRoute(senderId, recipientId))
                                },
                                allUsers = allUsers,
                                onLike = { collectionId ->
                                    routeCollectionViewModel.toggleLike(collectionId, senderId)
                                }
                            )
                        }
                    }
                }

            }
        }
    }
}

