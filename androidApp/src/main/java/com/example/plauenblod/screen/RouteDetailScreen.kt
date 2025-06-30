package com.example.plauenblod.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.plauenblod.component.review.ReviewSheet
import com.example.plauenblod.component.routes.ColorDot
import com.example.plauenblod.component.routes.routesList.DifficultyCircle
import com.example.plauenblod.extension.displayName
import com.example.plauenblod.extension.toColor
import com.example.plauenblod.model.routeProperty.Difficulty
import com.example.plauenblod.viewmodel.RouteReviewViewModel
import com.example.plauenblod.viewmodel.RouteViewModel
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    routeId: String,
    modifier: Modifier = Modifier,
    routeViewModel: RouteViewModel = koinInject(),
    routeReviewViewModel: RouteReviewViewModel = koinInject(),
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

    var showReviewSheet by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    if (route == null) {
        Text("Route wird geladen...", modifier = modifier.padding(16.dp))
        return
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
        }
    ) { innerPadding ->
        Card(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .widthIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DifficultyCircle(difficulty = route.difficulty, number = route.number)
                    Text(
                        text = route.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Informationen",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                Text(
                    text = "${route.hall.displayName()} · ${route.sector.displayName()}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Grifffarbe:", style = MaterialTheme.typography.bodyMedium)
                    ColorDot(route.holdColor.toColor())
                    Text("Schrauber: ${route.setter}", style = MaterialTheme.typography.bodyMedium)
                }

                Text(
                    text = route.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { showReviewSheet = !showReviewSheet }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "Route bewerten",
                            modifier = modifier
                                .padding(horizontal = 32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
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
                Text(
                    text = "Rezensionen",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                if (showReviewSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            if (
                                reviewComment.isNotEmpty() ||
                                reviewRating > 0 ||
                                reviewDate != null ||
                                reviewAttempts > 1
                            ) {
                                showDiscardDialog = true
                            } else {
                                showReviewSheet = false
                            }
                        }
                    ) {
                        ReviewSheet(
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
                                showReviewSheet = false
                            },
                            routeId = routeId
                        )
                    }
                }

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
                                showReviewSheet = true
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