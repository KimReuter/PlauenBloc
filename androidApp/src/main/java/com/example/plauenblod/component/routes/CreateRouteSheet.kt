package com.example.plauenblod.component.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.navigation.NavController
import com.example.plauenblod.extension.hallSection
import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.Route
import com.example.plauenblod.model.Sector
import com.example.plauenblod.viewmodel.RouteViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun CreateRouteSheet(
    onDismiss: () -> Unit,
    viewModel: RouteViewModel = koinInject()
) {
    var name by remember { mutableStateOf("") }

    val initialHall = HallSection.entries.first()
    var hall by remember { mutableStateOf(initialHall) }

    val filteredSectors = remember(hall) { Sector.entries.filter { it.hallSection() == hall } }
    var sector by remember { mutableStateOf(filteredSectors.firstOrNull()) }

    var holdColor by remember { mutableStateOf<HoldColor?>(HoldColor.entries.first()) }
    var difficulty by remember { mutableStateOf<Difficulty?>(Difficulty.entries.first()) }
    var description by remember { mutableStateOf("") }
    var number by remember { mutableIntStateOf(1) }
    var setter by remember { mutableStateOf("") }

    val routeCreated by viewModel.routeCreated.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showSuccesDialog by remember { mutableStateOf(false) }

    var showMap by remember { mutableStateOf(false) }
    var selectedPoint by remember { mutableStateOf<Offset?>(null) }

    val isCreateEnabled = selectedPoint != null &&
            name.isNotBlank() &&
            setter.isNotBlank() &&
            description.isNotBlank()

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

    if (showSuccesDialog) {
        RouteCreatedDialog(onDismiss = {
            showSuccesDialog = false
            onDismiss()
        })
    }

    CreateRouteForm(
        name = name,
        onNameChange = { name = it },
        hall = hall,
        onHallChange = { hall = it },
        sector = sector,
        sectors = filteredSectors,
        onSectorChange = { sector = it },
        holdColor = holdColor,
        onHoldColorChange = { holdColor = it },
        difficulty = difficulty,
        onDifficultyChange = { difficulty = it },
        number = number,
        onNumberChange = { number = it },
        description = description,
        onDescriptionChange = { description = it },
        setter = setter,
        onSetterChange = { setter = it },
        onSelectPointClick = { showMap = true },
        onCreateClick = {
            if (selectedPoint != null && sector != null && holdColor != null && difficulty != null) {
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
                        x = selectedPoint!!.x,
                        y = selectedPoint!!.y
                    )
                )
            }
        },
        isCreateEnabled = isCreateEnabled,
        errorMessage = errorMessage,
        showMap = showMap,
        onDismissMap = { showMap = false },
        selectedPoint = selectedPoint,
        onPointSelected = { selectedPoint = it },
    )
}
