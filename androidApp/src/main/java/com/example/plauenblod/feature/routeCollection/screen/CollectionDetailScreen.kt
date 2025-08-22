package com.example.plauenblod.feature.routeCollection.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.routeCollection.viewModel.RouteCollectionViewModel
import com.example.plauenblod.feature.routeCollection.viewModel.RouteSelectionViewModel
import com.example.plauenblod.screen.BoulderDetailRoute
import com.example.plauenblod.screen.EditCollectionRoute
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    routeCollectionViewModel: RouteCollectionViewModel = koinInject(),
    routeSelectionViewModel: RouteSelectionViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    navController: NavController
) {
    val currentUserId by authViewModel.userId.collectAsState()
    val collection by routeCollectionViewModel.selectedCollection.collectAsState()
    val allRoutes by routeSelectionViewModel.allRoutes.collectAsState()
    val isLiked = remember(collection, currentUserId) {
        collection?.likedBy?.contains(currentUserId) == true
    }

    var gridMode by remember { mutableStateOf(false) }

    LaunchedEffect(collectionId) {
        routeCollectionViewModel.loadCollection(collectionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = collection?.name ?: "Lade…") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    IconButton(
                        enabled = collection != null,
                        onClick = {
                            currentUserId?.let { uid ->
                                routeCollectionViewModel.toggleLike(collectionId, uid)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isLiked) "Entliken" else "Liken",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        enabled = collection != null,
                        onClick = {
                            navController.navigate(EditCollectionRoute(collectionId))
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Bearbeiten")
                    }
                    IconButton(onClick = { /* share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Teilen")
                    }
                }
            )
        }
    ) { inner ->
        when {
            collection == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                val routesInCollection = remember(allRoutes, collection) {
                    allRoutes.filter { it.id in collection!!.routeIds }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = collection!!.description ?: "Keine Beschreibung.",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                IconButton(onClick = { gridMode = !gridMode }) {
                                    Icon(
                                        imageVector = if (gridMode) Icons.Default.List else Icons.Default.GridView,
                                        contentDescription = "Ansicht wechseln"
                                    )
                                }
                            }

                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                    }


                    if (routesInCollection.isEmpty()) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Noch keine Routen in dieser Sammlung.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else {
                        items(routesInCollection) { route ->
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Row(
                                    Modifier
                                        .clickable { navController.navigate(BoulderDetailRoute(route.id)) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = collection?.doneRouteIds?.contains(route.id) == true,
                                        onCheckedChange = { isChecked ->
                                            routeCollectionViewModel.toggleDone(route.id, isChecked)
                                        }
                                    )
                                    Text(
                                        text = route.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Zur Route"
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}