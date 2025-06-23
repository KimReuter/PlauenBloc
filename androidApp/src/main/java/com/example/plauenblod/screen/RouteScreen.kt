package com.example.plauenblod.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plauenblod.component.ScreenTitle
import com.example.plauenblod.component.createRoute.CancelDialog
import com.example.plauenblod.component.map.BoulderSearchBar
import com.example.plauenblod.component.map.FirstHallMapScreen
import com.example.plauenblod.component.map.SecondHallMapScreen
import com.example.plauenblod.component.createRoute.CreateRouteSheet
import com.example.plauenblod.component.createRoute.MenuButton
import com.example.plauenblod.extension.hallSection
import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.Route
import com.example.plauenblod.model.Sector
import com.example.plauenblod.viewmodel.AuthViewModel
import com.example.plauenblod.viewmodel.RouteViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import androidx.compose.runtime.key
import com.example.plauenblod.component.editRoutes.RouteActionSheet

private val OffsetSaver = Saver<Offset?, Map<String, Any>>(
    save = { offset -> offset?.let { mapOf("x" to it.x, "y" to it.y) } },
    restore = {
        val x = it["x"] as? Float
        val y = it["y"] as? Float
        if (x != null && y != null) Offset(x, y) else null
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteScreen(
    modifier: Modifier = Modifier,
    routeViewModel: RouteViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    navController: NavHostController
) {
    var selectedHall by remember { mutableStateOf("first") }
    var query by remember { mutableStateOf("") }
    var showMap by remember { mutableStateOf(true) }
    val userRole by authViewModel.userRole.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }
    val allRoutes by routeViewModel.routes.collectAsState()
    val filteredRoutes = allRoutes.filter {
        it.sector.hallSection() == if (selectedHall == "first") HallSection.FRONT else HallSection.BACK
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Route erstellen
    var name by rememberSaveable { mutableStateOf("") }
    var hall by rememberSaveable { mutableStateOf(HallSection.FRONT) }
    var sector by rememberSaveable { mutableStateOf(Sector.entries.firstOrNull()) }
    var holdColor by rememberSaveable { mutableStateOf(HoldColor.entries.firstOrNull()) }
    var difficulty by rememberSaveable { mutableStateOf(Difficulty.entries.firstOrNull()) }
    var number by rememberSaveable { mutableIntStateOf(1) }
    var description by rememberSaveable { mutableStateOf("") }
    var setter by rememberSaveable { mutableStateOf("") }
    var selectedPoint: Offset? by rememberSaveable(stateSaver = OffsetSaver) { mutableStateOf(null) }
    val routeCreated by routeViewModel.routeCreated.collectAsState()
    var refreshKey by remember { mutableStateOf(0) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val takenNumbers = allRoutes
        .filter { it.sector == sector }
        .map { it.number }
    val availableNumbers = (1..25).filterNot { it in takenNumbers }

    // Route bearbeiten
    var selectedRoute by remember { mutableStateOf<Route?>(null) }
    var showRouteMenu by remember { mutableStateOf(false) }

    // CancelDialog
    var showCancelDialog by remember { mutableStateOf(false) }
    val errorMessage by routeViewModel.errorMessage.collectAsState()
    val isCreateEnabled = selectedPoint != null &&
            name.isNotBlank() &&
            setter.isNotBlank() &&
            description.isNotBlank()

    val hasUnsavedData =
        name.isNotBlank() || description.isNotBlank() || setter.isNotBlank() || selectedPoint != null

    LaunchedEffect(routeCreated) {
        if (routeCreated?.isSuccess == true) {
            name = ""
            description = ""
            setter = ""
            selectedPoint = null

            routeViewModel.clearError()
            routeViewModel.clearRouteCreatedStatus()

            refreshKey++

            navController.popBackStack()
            navController.navigate(MapRoute)

            showSuccessDialog = true
        }
    }

    LaunchedEffect(availableNumbers) {
        if (availableNumbers.isNotEmpty()) {
            number = availableNumbers.first()
        }
    }

    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        ScreenTitle("Routen")

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BoulderSearchBar(
                    query = query,
                    onQueryChanged = { query = it },
                    modifier = Modifier.weight(1f)
                )

                MenuButton(
                    currentUserRole = userRole,
                    onCreateRouteClick = {
                        showCreateSheet = true
                    },
                    onFilterClick = {
                        println("Filter geöffnet")
                    }
                )

            }

            Spacer(modifier = modifier.height(16.dp))

            Row {
                Button(
                    onClick = { selectedHall = "second" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "second") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Hintere Halle")
                }

                Spacer(modifier = modifier.width(16.dp))

                Button(
                    onClick = { selectedHall = "first" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "first") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    ),

                    ) {
                    Text("Vordere Halle")
                }
            }

            key(refreshKey, selectedHall) {
                if (selectedHall == "first") {
                    if (showMap) {
                            FirstHallMapScreen(
                                routes = filteredRoutes,
                                navController = navController,
                                refreshKey = refreshKey,
                                onRouteLongClick = {
                                    println("Long press on route: ${it.name}")
                                    selectedRoute = it
                                    showRouteMenu = true
                                }
                            )
                    } else {
                        //FirstHallListScreen()
                    }
                } else {
                    if (showMap) {
                            SecondHallMapScreen(
                                routes = filteredRoutes,
                                navController = navController,
                                refreshKey = refreshKey,
                                onRouteLongClick = { route ->
                                    selectedRoute = route
                                    showRouteMenu = true
                                }
                            )
                    } else {
                        //SecondHallListScreen()
                    }
                }
            }


            if (showCreateSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        if (hasUnsavedData) {
                            showCancelDialog = true
                        } else {
                            scope.launch {
                                sheetState.hide()
                                showCreateSheet = false
                            }
                        }
                    },
                    sheetState = sheetState
                ) {
                    CreateRouteSheet(
                        name = name,
                        onNameChange = { name = it },
                        hall = hall,
                        onHallChange = { hall = it },
                        sector = sector,
                        sectors = Sector.entries.filter { it.hallSection() == hall },
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
                        selectedPoint = selectedPoint,
                        onPointSelected = { selectedPoint = it },
                        showMap = showMap,
                        onDismissMap = { showMap = false },
                        onSelectPointClick = { showMap = true },
                        onCancelClick = { showCancelDialog = true },
                        onDismiss = {
                            scope.launch {
                                sheetState.hide()
                                showCreateSheet = false

                                name = ""
                                description = ""
                                setter = ""
                                selectedPoint = null
                                hall = HallSection.FRONT
                                sector = null
                                holdColor = null
                                difficulty = null
                                number = 1

                                routeViewModel.clearError()

                            }
                        },
                        onCreateClick = {
                            if (selectedPoint != null && sector != null && holdColor != null && difficulty != null) {
                                routeViewModel.createRoute(
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
                        showSuccessDialog = showSuccessDialog,
                        onDismissSuccessDialog = {
                            showSuccessDialog = false
                            scope.launch {
                                sheetState.hide()
                                showCreateSheet = false
                            }
                        },
                        availableNumbers = availableNumbers
                    )
                }
            }

            if (showCancelDialog) {
                CancelDialog(
                    onConfirm = {
                        showCancelDialog = false
                        scope.launch {
                            sheetState.hide()
                            showCreateSheet = false
                        }
                    },
                    onDismiss = {
                        showCancelDialog = false
                        scope.launch {
                            sheetState.show()
                        }
                    }
                )
            }

            if (showRouteMenu && selectedRoute != null) {
                RouteActionSheet(
                    route = selectedRoute!!,
                    onDismiss = { showRouteMenu = false },
                    onEdit = { route ->

                    },
                    onDelete = { route ->
                        routeViewModel.deleteRoute(route.id)
                    }
                )
            }
        }
    }
}

