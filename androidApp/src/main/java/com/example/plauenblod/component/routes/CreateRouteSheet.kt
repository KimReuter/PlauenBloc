package com.example.plauenblod.component.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.plauenblod.component.LabeledTextField
import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.Route
import com.example.plauenblod.model.Sector
import com.example.plauenblod.viewmodel.RouteViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import com.example.plauenblod.extension.hallSection
import com.example.plauenblod.screen.SelectPointRoute
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.plauenblod.viewmodel.NavigationEvent

@Composable
fun CreateRouteSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RouteViewModel = koinInject(),
    navController: NavController
) {
    var name by remember { mutableStateOf("") }

    val initialHall = HallSection.entries.first()
    var hall by remember { mutableStateOf(initialHall) }

    val filteredSectors = remember(hall) { Sector.entries.filter { it.hallSection() == hall } }
    var sector by remember { mutableStateOf(filteredSectors.firstOrNull()) }

    var holdColor by remember { mutableStateOf<HoldColor?>(HoldColor.entries.first()) }
    var difficulty by remember { mutableStateOf<Difficulty?>(Difficulty.entries.first()) }
    var description by remember { mutableStateOf("") }
    var number by remember { mutableStateOf(1) }
    var setter by remember { mutableStateOf("") }

    val routeCreated by viewModel.routeCreated.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showSuccesDialog by remember { mutableStateOf(false)}

    val navigationEvents = viewModel.navigation.collectAsState(initial = null)
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    println("BackStackEntry = ${navController.currentBackStackEntry}")

    val selectedX = savedStateHandle?.getLiveData<Float>("x")?.observeAsState()?.value
    val selectedY = savedStateHandle?.getLiveData<Float>("y")?.observeAsState()?.value

    LaunchedEffect(routeCreated) {
        if (routeCreated?.isSuccess == true) {
            showSuccesDialog = true

            delay(2000)
            showSuccesDialog = false
            onDismiss()
        }
    }

    LaunchedEffect(name, description, setter) {
        if (name.isNotBlank() && description.isNotBlank() && setter.isNotBlank()) {
            viewModel.clearError()
        }
    }

    LaunchedEffect(hall) {
        val newList = Sector.entries.filter { it.hallSection() == hall }
        if (sector !in newList) {
            sector = newList.firstOrNull()
        }
    }

    LaunchedEffect(navigationEvents.value) {
        when (val event = navigationEvents.value) {
            is NavigationEvent.NavigateToSelectPoint -> {
                navController.navigate(
                    SelectPointRoute(hallSectionName = event.hallSection.name)
                )
            }
            null -> Unit
        }
    }

    if (showSuccesDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccesDialog = false
                onDismiss()
            },
            confirmButton = {
                Button(onClick = {
                    showSuccesDialog = false
                    onDismiss()
                }) {
                    Text(
                        text = "Ok",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                        )
                }
            },
            title = {
                Text(
                    text = "Herzlichen Glückwunsch! 🎉",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ) },
            text = {
                Text(
                    text = "Du hast eine neue Route erstellt.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                ) }
        )
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Neue Route erstellen",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )

        if(!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = modifier.height(16.dp))

        LabeledTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name",
            placeholder = "Wie heißt die Route?",
            leadingIcon = Icons.Default.Create
        )

        DropdownSelector(
            label = "Hallenteil",
            options = HallSection.values().toList(),
            selected = hall,
            onSelected = { hall = it }
        )

        DropdownSelector(
            label = "Sektor",
            options = filteredSectors,
            selected = sector,
            onSelected = { sector = it }
        )

        DropdownSelector(
            label = "Grifffarbe",
            options = HoldColor.values().toList(),
            selected = holdColor,
            onSelected = { holdColor = it }
        )

        DropdownSelector(
            label = "Schwierigkeit",
            options = Difficulty.values().toList(),
            selected = difficulty,
            onSelected = { difficulty = it }
        )

        IntDropdownSelector(
            label = "Nummer",
            options = (1..25).toList(),
            selected = number,
            onSelected = { number = it }
        )

        Button(
            onClick = { viewModel.onSelectPointClicked(hall) }
        ) {
            Text("Punkt auf Karte festlegen")
        }

        LabeledTextField(
            value = description,
            onValueChange = { description = it },
            label = "Beschreibung",
            placeholder = "Wie fühlt sich die Route an?",
            leadingIcon = Icons.Default.Description
        )

        LabeledTextField(
            value = setter,
            onValueChange = { setter = it },
            label = "Routesetter",
            placeholder = "Wer hat die Route geschraubt?",
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedX != null && selectedY != null && sector != null && holdColor != null && difficulty != null && number != null) {
                    viewModel.createRoute(
                        Route(
                            id = "",
                            name = name,
                            sector = sector!!,
                            holdColor = holdColor!!,
                            difficulty = difficulty!!,
                            number = number,
                            description = description,
                            setter = setter,
                            x = selectedX,
                            y = selectedY
                        )
                    )
                }
            }
        ) {
            Text(
                text = "Route erstellen",
                color = MaterialTheme.colorScheme.surface
                )
        }
    }
}