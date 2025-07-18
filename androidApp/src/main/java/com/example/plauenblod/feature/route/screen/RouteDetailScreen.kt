package com.example.plauenblod.feature.route.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plauenblod.feature.navigation.TabItem
import com.example.plauenblod.feature.review.component.ReviewDialogs
import com.example.plauenblod.feature.review.component.ReviewListSection
import com.example.plauenblod.feature.review.component.ReviewSheetModal
import com.example.plauenblod.feature.review.component.ReviewsHeader
import com.example.plauenblod.feature.review.component.RouteActionButtons
import com.example.plauenblod.feature.review.component.RouteDetailHeader
import com.example.plauenblod.feature.review.component.RouteInformationSection
import com.example.plauenblod.feature.routeReview.model.RouteReview
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.chat.viewmodel.ChatViewModel
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.routeReview.viewmodel.RouteReviewViewModel
import com.example.plauenblod.feature.route.viewmodel.RouteViewModel
import com.example.plauenblod.feature.chat.component.ShareDialog
import com.example.plauenblod.feature.user.viewmodel.UserViewModel
import com.example.plauenblod.screen.ChatRoute
import com.example.plauenblod.viewmodel.state.DialogState
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
    userViewModel: UserViewModel = koinInject(),
    chatViewModel: ChatViewModel = koinInject(),
    onBackClick: () -> Unit,
    navController: NavController,
    selectedTab: MutableState<TabItem>
) {
    // Routen
    val allRoutes by routeViewModel.allRoutes.collectAsState()
    val route = allRoutes.find { it.id == routeId }

    // Review anzeigen
    val reviews by routeReviewViewModel.reviews.collectAsState()
    var reviewsExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Aktueller User
    val currentUserId = authViewModel.userId.collectAsState().value ?: ""
    val currentUserName = userViewModel.userName.collectAsState().value ?: "Anonymer Nutzer"
    val currentUserProfileImageUrl = userViewModel.userProfileImageUrl.collectAsState().value

    // Review Formular
    var reviewComment by remember { mutableStateOf("") }
    var reviewRating by remember { mutableStateOf(1) }
    var reviewCompleted by remember { mutableStateOf(false) }
    var reviewDate by remember { mutableStateOf<LocalDate?>(null) }
    var reviewAttempts by remember { mutableStateOf(1) }
    var reviewDifficulty by remember {
        mutableStateOf(
            route?.difficulty ?: Difficulty.values().first()
        )
    }

    // BottomSheet & Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shouldReopenSheet by remember { mutableStateOf(false) }
    var showReviewSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Review Verwaltung
    var reviewBeingEdited by remember { mutableStateOf<RouteReview?>(null) }
    var initialReview by remember { mutableStateOf<RouteReview?>(null) }
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Hidden) }
    val hasUserReviewed by routeReviewViewModel.hasUserReviewed.collectAsState()

    //
    val isDirty = initialReview?.let {
        it.comment != reviewComment ||
                it.stars != reviewRating ||
                it.completed != reviewCompleted ||
                it.completionDate != reviewDate?.toString() ||
                it.attempts != reviewAttempts ||
                it.perceivedDifficulty != reviewDifficulty
    } ?: (
            reviewComment.isNotEmpty() ||
                    reviewRating != 1 ||
                    reviewDate != null ||
                    reviewAttempts > 1 ||
                    reviewCompleted != false
            )

    // Route teilen
    var showShareDialog by remember { mutableStateOf(false) }
    var allUsers = userViewModel.filteredUsers.collectAsState().value.filter { it.uid != currentUserId }

    fun resetReview() {
        reviewBeingEdited = null
        reviewComment = ""
        reviewRating = 1
        reviewDate = null
        reviewAttempts = 1
        reviewCompleted = false
        reviewDifficulty = route?.difficulty ?: Difficulty.values().first()
    }

    if (route == null) {
        Text("Route wird geladen...", modifier = modifier.padding(16.dp))
        return
    }

    LaunchedEffect(route) {
        route?.let {
            reviewDifficulty = it.difficulty
        }
    }

    LaunchedEffect(routeId) {
        if (currentUserId != null) {
            routeReviewViewModel.loadReviews(routeId, currentUserId)
        }
    }

    LaunchedEffect(shouldReopenSheet) {
        if (shouldReopenSheet) {
            shouldReopenSheet = false
            showReviewSheet = true
        }
    }

    LaunchedEffect (Unit) {
        userViewModel.loadAllUsers()
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
        Column (
            modifier = modifier
                .animateContentSize()
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Box {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .widthIn(max = 500.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState
                ) {
                    stickyHeader {
                        RouteDetailHeader(route)
                    }

                    item {
                        RouteInformationSection(route)
                    }

                    item {
                        RouteActionButtons(
                            onReviewClick = { showReviewSheet = true },
                            onAddToListClick = { },
                            onShareRoute = { showShareDialog = true },
                            isReviewEnabled = !hasUserReviewed
                        )
                    }

                    item {
                        ReviewsHeader(
                            reviews = reviews,
                            expanded = reviewsExpanded,
                            onToggle = { reviewsExpanded = !reviewsExpanded }
                        )
                    }

                    ReviewListSection(
                        reviewsExpanded = reviewsExpanded,
                        reviews = reviews,
                        currentUserId = currentUserId,
                        onEdit = { review ->
                            reviewBeingEdited = review
                            reviewComment = review.comment
                            reviewRating = review.stars
                            reviewCompleted = review.completed
                            reviewDate = review.completionDate?.let { LocalDate.parse(it) }
                            reviewAttempts = review.attempts
                            reviewDifficulty =
                                review.perceivedDifficulty ?: Difficulty.values().first()
                            initialReview = review.copy()
                            showReviewSheet = true
                        },
                        onDelete = { review ->
                            reviewBeingEdited = review
                            dialogState = DialogState.ShowDeleteConfirm
                        },
                        onUserClick = { },
                        navController = navController,
                        selectedTab = selectedTab
                    )
                }

                ReviewSheetModal(
                    showSheet = showReviewSheet,
                    sheetState = sheetState,
                    isDirty = isDirty,
                    onRequestCancel = { dialogState = DialogState.ShowCancelDialog },
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                            showReviewSheet = false
                            resetReview()
                        }
                    },
                    routeId = routeId,
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
                        println("✅ onSubmit triggered")
                        val reviewWithUserId = review.copy(
                            userId = currentUserId.ifBlank { "anonymous" },
                            userName = currentUserName,
                            userProfileImageUrl = currentUserProfileImageUrl,
                            id = reviewBeingEdited?.id.orEmpty()
                        )
                        if (reviewBeingEdited == null) {
                            routeReviewViewModel.addReview(
                                routeId = routeId,
                                stars = reviewWithUserId.stars,
                                comment = reviewWithUserId.comment,
                                completed = reviewWithUserId.completed,
                                attempts = reviewWithUserId.attempts,
                                perceivedDifficulty = reviewWithUserId.perceivedDifficulty ?: Difficulty.values().first(),
                                onResult = { success ->
                                    if (success) {
                                        if (reviewWithUserId.completed) {
                                            userViewModel.tickRoute(
                                                userId = currentUserId,
                                                route = route,
                                                attempts = reviewWithUserId.attempts,
                                                isFlash = (reviewWithUserId.attempts == 1)
                                            ) { tickSuccess ->
                                                if (tickSuccess) {
                                                    println("✅ Tick erfolgreich beim User gespeichert")
                                                    userViewModel.loadUser(currentUserId)
                                                } else {
                                                    println("❌ Fehler beim Tick speichern")
                                                }
                                            }
                                        }
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Danke für deine Rezension! 🎉")
                                        }
                                        routeReviewViewModel.loadReviews(routeId, currentUserId)
                                        showReviewSheet = false
                                        resetReview()
                                    }
                                }
                            )
                        } else {
                            routeReviewViewModel.updateReview(
                                routeId,
                                reviewWithUserId
                            ) { success ->
                                if (success) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Review aktualisiert!")
                                    }
                                    routeReviewViewModel.loadReviews(routeId, currentUserId)
                                    showReviewSheet = false
                                    resetReview()
                                }
                            }
                        }
                    }
                )

                ReviewDialogs(
                    dialogState = dialogState,
                    onDismissDialog = { dialogState = DialogState.Hidden },
                    onCancelConfirm = {
                        dialogState = DialogState.Hidden
                        resetReview()
                        scope.launch {
                            sheetState.hide()
                            showReviewSheet = false
                        }
                    },
                    onCancelBack = {
                        dialogState = DialogState.Hidden
                        scope.launch { sheetState.show() }
                    },
                    onDeleteConfirm = {
                        dialogState = DialogState.Hidden
                        reviewBeingEdited?.let { review ->
                            routeReviewViewModel.deleteReview(routeId, review.id) { success ->
                                if (success) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Rezension wurde gelöscht")
                                    }
                                    routeReviewViewModel.loadReviews(routeId, currentUserId)
                                    reviewBeingEdited = null
                                }
                            }
                        }
                    },
                    onDeleteCancel = {
                        dialogState = DialogState.Hidden
                        resetReview()
                        showReviewSheet = false
                    }
                )
            }
        }

        if (showShareDialog) {
            ShareDialog(
                itemTypeName   = "Route",
                itemId         = route.id,
                allUsers       = allUsers,
                defaultMessage = "Ich möchte dir diese Route empfehlen 🚀",
                onDismiss      = { showShareDialog = false },
                onSend         = { message, recipientId, itemId ->
                    chatViewModel.sendMessage(
                        senderId    = currentUserId,
                        recipientId = recipientId,
                        messageText = "$message\n\nRoute: ${route.name}"
                    )
                    navController.navigate(ChatRoute(currentUserId, recipientId))
                }
            )
        }
    }
}