package com.example.plauenblod.feature.dashboard.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.plauenblod.component.ConfirmationDialog
import com.example.plauenblod.feature.dashboard.component.LeaderboardCard
import com.example.plauenblod.feature.dashboard.component.NewsCard
import com.example.plauenblod.feature.dashboard.component.NewsPostDialog
import com.example.plauenblod.feature.dashboard.model.NewsPost
import com.example.plauenblod.feature.dashboard.viewModel.DashboardViewModel
import com.example.plauenblod.feature.ranking.viewModel.LeaderboardViewModel
import com.example.plauenblod.screen.NewsDetailRoute
import com.example.plauenblod.screen.UserProfileRoute
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = koinInject(),
    leaderboardViewModel: LeaderboardViewModel = koinInject(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val isOperator by dashboardViewModel.isOperator.collectAsState()
    val newsPosts by dashboardViewModel.news.collectAsState(initial = emptyList())
    var expandedAll by remember { mutableStateOf(false) }
    var showPostDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<NewsPost?>(null) }
    var postToEdit by remember { mutableStateOf<NewsPost?>(null) }

    val leaderboardState by leaderboardViewModel.leaderboardState.collectAsState()
    var showAllLeaderboard by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    if (isOperator) {
                        IconButton(onClick = { showPostDialog = true }) {
                            Icon(Icons.Default.Add, "Post hinzufügen")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Neuigkeiten", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                stickyHeader {
                    TextButton(
                        onClick = { expandedAll = !expandedAll },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            if (expandedAll) "Weniger anzeigen ▴"
                            else "Alle Neuigkeiten anzeigen ▾"
                        )
                    }
                }

                items(newsPosts.take(1)) { post ->
                    NewsCard(
                        post = post,
                        navController = navController,
                        isOperator = isOperator,
                        onEdit = {
                            postToEdit = it
                            showPostDialog = true
                        },
                        onDelete = {
                            postToDelete = post
                            showDeleteDialog = true
                        }
                    )
                }

                if (expandedAll) {
                    items(newsPosts.drop(1)) { post ->

                        NewsCard(
                            post = post,
                            navController = navController,
                            isOperator = isOperator,
                            onEdit = {
                                postToEdit = it
                                showPostDialog = true
                            },
                            onDelete = {
                                postToDelete = post
                                showDeleteDialog = true
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Leaderboards",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                        )

                        IconButton(
                            onClick = { }
                        ) {
                            Icon(Icons.Default.FilterList, "Leaderboard filtern")
                        }
                    }

                }

                val entriesToShow = if (showAllLeaderboard) {
                    leaderboardState.entries
                } else {
                    leaderboardState.entries.take(3)
                }

                items(entriesToShow) { entry ->
                    LeaderboardCard(
                        entry = entry,
                        onSelectUser = { userId ->
                            navController.navigate(UserProfileRoute(userId))
                        },
                        maxPointsInBoard = leaderboardState.maxPoints
                    )
                }

                item {
                    TextButton(
                        onClick = { showAllLeaderboard = !showAllLeaderboard },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = if (showAllLeaderboard) "Weniger anzeigen ▲" else "Alle anzeigen ▼"
                        )
                    }
                }

            }

        }
    }

    NewsPostDialog(
        show = showPostDialog,
        initialPost = postToEdit,
        onDismiss = { showPostDialog = false },
        onPost = { title, content, postId ->
            if (postId == null) {
                dashboardViewModel.postNews(title, content, /* imageUrl = */ null)
            } else {
                dashboardViewModel.updatePost(
                    postToEdit!!.copy(title = title, content = content)
                )
            }
            showPostDialog = false
            postToEdit = null
        }
    )

    ConfirmationDialog(
        show = showDeleteDialog,
        title = "Beitrag löschen?",
        message = "Bist du sicher, dass du diesen News-Post dauerhaft entfernen möchtest?",
        confirmLabel = "Löschen",
        dismissLabel = "Abbrechen",
        onConfirm = {
            postToDelete?.id?.let {
                dashboardViewModel.deletePost(it)
                scope.launch {
                    snackbarHostState.showSnackbar("Post wurde gelöscht")
                }
            }
            showDeleteDialog = false
        },
        onDismiss = {
            showDeleteDialog = false
        }
    )
}
