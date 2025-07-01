package com.example.plauenblod.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.plauenblod.component.review.GiveReviewSheet
import com.example.plauenblod.component.review.ReviewItem
import com.example.plauenblod.component.routes.ColorDot
import com.example.plauenblod.component.routes.routesList.DifficultyCircle
import com.example.plauenblod.extension.displayName
import com.example.plauenblod.extension.toColor
import com.example.plauenblod.model.routeProperty.Difficulty
import com.example.plauenblod.viewmodel.AuthViewModel
import com.example.plauenblod.viewmodel.RouteReviewViewModel
import com.example.plauenblod.viewmodel.RouteViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: String,
    modifier: Modifier = Modifier,
    routeViewModel: RouteViewModel = koinInject(),
    routeReviewViewModel: RouteReviewViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    onBackClick: () -> Unit
) {
    val allRoutes by routeViewModel.routes.collectAsState()
    val route = allRoutes.find { it.id == routeId }

    // Review
    var reviewComment by remember { mutableStateOf("") }
    var reviewRating by remember { mutableStateOf(0) }
    var reviewCompleted by remember { mutableStateOf(false) }
    var reviewDate by remember { mutableStateOf<LocalDate?>(null) }
    var reviewAttempts by remember { mutableStateOf(1) }
    var reviewDifficulty by remember { mutableStateOf(Difficulty.values().first()) }

    // Review geben
    var showReviewSheet by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var reOpenSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val hasUserReviewed by routeReviewViewModel.hasUserReviewed.collectAsState()
    val currentUserId = authViewModel.userId.collectAsState().value ?: ""
    val currentUserName = authViewModel.userName.collectAsState().value ?: "Anonymer Nutzer"
    val currentUserProfileImageUrl = authViewModel.userProfileImageUrl.collectAsState().value

    // Review anzeigen
    val reviews by routeReviewViewModel.reviews.collectAsState()
    var reviewsExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val isDirty = reviewComment.isNotEmpty() ||
            reviewRating > 0 ||
            reviewDate != null ||
            reviewAttempts > 1

    if (route == null) {
        Text("Route wird geladen...", modifier = modifier.padding(16.dp))
        return
    }

    LaunchedEffect(reOpenSheet) {
        if (reOpenSheet) {
            showReviewSheet = true
            reOpenSheet = false
        }
    }

    LaunchedEffect(routeId) {
        if (currentUserId != null) {
            routeReviewViewModel.loadReviews(routeId, currentUserId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routendetails") },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, "Zurück"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    action = {
                        data.visuals.actionLabel?.let { actionLabel ->
                            TextButton(onClick = { data.performAction() }) {
                                Text(actionLabel)
                            }
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(data.visuals.message)
                    }
                }
            }
        }
    ) { innerPadding ->
        Card(
            modifier = modifier
                .animateContentSize()
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .heightIn(min = 600.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)

        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState
            ) {
                stickyHeader {
                    Surface(
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            DifficultyCircle(difficulty = route.difficulty, number = route.number)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = route.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                item {
                    Text(
                        text = "Informationen",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "${route.hall.displayName()} - ${route.sector.displayName()}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Grifffarbe:", style = MaterialTheme.typography.bodyMedium)
                        ColorDot(route.holdColor.toColor())
                        Text(
                            "Schrauber: ${route.setter}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = route.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = modifier
                            .fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { showReviewSheet = !showReviewSheet },
                            enabled = !hasUserReviewed
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddComment,
                                contentDescription = "Route bewerten",
                                modifier = modifier
                                    .padding(horizontal = 32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        OutlinedButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Route zu Liste hinzufügen",
                                modifier = modifier
                                    .padding(horizontal = 32.dp)
                            )
                        }
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rezensionen",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(24.dp))
                            if (reviews.isNotEmpty()) {
                                val averageRating = reviews.map { it.stars }.average()
                                Text(
                                    text = "Ø ⭐ ${
                                        String.format(
                                            "%.1f",
                                            averageRating
                                        )
                                    } (${reviews.size})",
                                    color = MaterialTheme.colorScheme.tertiary

                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                reviewsExpanded = !reviewsExpanded
                            }
                        ) {
                            Icon(
                                imageVector = if (reviewsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (reviewsExpanded) "Einklappen" else "Ausklappen"
                            )
                        }
                    }
                }

                if (reviewsExpanded) {
                    if (reviews.isEmpty()) {
                        item {
                            Text(
                                text = "Noch keine Rezensionen vorhanden.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        itemsIndexed(reviews) { index, review ->
                            ReviewItem(
                                review,
                                backgroundColor = if (index % 2 == 0)
                                    MaterialTheme.colorScheme.surface
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                currentUserId = currentUserId,
                                onEdit = { review ->

                                },
                                onDelete = { review ->
                                    routeReviewViewModel.deleteReview(routeId, review.id) { success ->
                                        if (success) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Review wurde gelöscht")
                                            }
                                            routeReviewViewModel.loadReviews(routeId, currentUserId)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    if (showReviewSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                if (isDirty) {
                                    showDiscardDialog = true
                                } else {
                                    showReviewSheet = false
                                }
                            }
                        ) {
                            GiveReviewSheet(
                                perceivedDifficulty = reviewDifficulty,
                                onDifficultyChange = { reviewDifficulty = it },
                                commentText = reviewComment,
                                onCommentChange = { reviewComment = it },
                                rating = reviewRating,
                                onRatingChange = { reviewRating = it },
                                completed = reviewCompleted,
                                onCompletedChange = { reviewCompleted = it },
                                selectedDate = reviewDate,
                                onDateChange = { reviewDate = it },
                                attempts = reviewAttempts,
                                onAttemptsChange = { reviewAttempts = it },
                                onSubmit = { review ->
                                    val reviewWithUserId = review.copy(
                                        userId = currentUserId.ifBlank { "anonymous" },
                                        userName = currentUserName,
                                        userProfileImageUrl = currentUserProfileImageUrl
                                    )
                                    routeReviewViewModel.addReview(
                                        routeId,
                                        reviewWithUserId
                                    ) { success ->
                                        if (success) {
                                            routeReviewViewModel.loadReviews(
                                                routeId,
                                                currentUserId ?: ""
                                            )
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Danke für deine Rezension! 🎉")
                                            }
                                            showReviewSheet = false
                                        } else {
                                            // Fehlerhandling
                                        }
                                    }
                                },
                                routeId = routeId
                            )
                        }
                    }
                }

                item {
                    if (showDiscardDialog) {
                        AlertDialog(
                            onDismissRequest = { showDiscardDialog = false },
                            confirmButton = {
                                Button(onClick = {
                                    showDiscardDialog = false
                                    showReviewSheet = false
                                    // Reset fields
                                    reviewComment = ""
                                    reviewRating = 0
                                    reviewDate = null
                                    reviewAttempts = 1
                                }) {
                                    Text("Verwerfen")
                                }
                            },
                            dismissButton = {
                                Button(onClick = {
                                    showDiscardDialog = false
                                    reOpenSheet = true
                                }) {
                                    Text("Zurück")
                                }
                            },
                            title = { Text("Rezension verwerfen?") },
                            text = { Text("Deine bisherigen Eingaben gehen verloren. Fortfahren?") }
                        )
                    }
                }
            }
        }
    }
}