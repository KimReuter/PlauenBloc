package com.example.plauenblod.feature.routeCollection.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.plauenblod.android.util.toFirestoreInstant
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.routeCollection.model.RouteCollection
import com.example.plauenblod.feature.routeCollection.viewModel.RouteSelectionViewModel
import kotlinx.datetime.Clock
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionFormScreen(
    existing: RouteCollection? = null,
    onSaveNew: (creatorId: String, name: String, description: String?, routeIds: List<String>, isPublic: Boolean) -> Unit,
    onSaveUpdate: (updated: RouteCollection) -> Unit,
    navController: NavController,
    routeSelectionViewModel: RouteSelectionViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject()
) {
    val routes         by routeSelectionViewModel.allRoutes.collectAsState()
    val selectedIds    by routeSelectionViewModel.selectedRouteIds.collectAsState()
    val userId         by authViewModel.userId.collectAsState()
    var name           by remember { mutableStateOf(existing?.name.orEmpty()) }
    var description    by remember { mutableStateOf(existing?.description.orEmpty()) }
    var isPublic       by remember { mutableStateOf(existing?.`public` ?: true) }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        existing?.let { routeSelectionViewModel.setSelection(it.routeIds) }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Abbrechen?") },
            text = { Text("Möchtest du wirklich abbrechen? Alle Änderungen gehen verloren.") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    navController.popBackStack()
                }) {
                    Text("Ja, abbrechen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Weiter bearbeiten")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "Neue Sammlung" else "Sammlung bearbeiten") },
                actions = {
                    IconButton(onClick = { showCancelDialog = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Abbrechen")
                    }
                    IconButton(
                        enabled = name.isNotBlank() && selectedIds.isNotEmpty(),
                        onClick = {
                            if (existing == null) {
                                onSaveNew(
                                    userId!!, name, description.takeIf(String::isNotBlank),
                                    selectedIds.toList(), isPublic
                                )
                            } else {
                                onSaveUpdate(
                                    existing.copy(
                                        name        = name,
                                        description = description.takeIf(String::isNotBlank),
                                        `public`    = isPublic,
                                        routeIds    = selectedIds.toList(),
                                        updatedAt   = Clock.System.now().toFirestoreInstant()
                                    )
                                )
                            }
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Speichern")
                    }
                }
            )
        }
    )  { innerPadding ->
        LazyColumn(Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Beschreibung (optional)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Checkbox(checked = isPublic, onCheckedChange = { isPublic = it })
                    Spacer(Modifier.width(4.dp))
                    Text("öffentlich")
                }
            }

            item {
                Divider()
            }

            items(routes) { route ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { routeSelectionViewModel.toggleSelection(route.id) }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = selectedIds.contains(route.id),
                        onCheckedChange = { routeSelectionViewModel.toggleSelection(route.id) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(route.name)
                }
            }
        }
    }
}