package com.example.plauenblod.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plauenblod.component.map.BoulderSearchBar
import com.example.plauenblod.component.map.HallSectionButtons
import com.example.plauenblod.component.routes.createRoute.CancelDialog
import com.example.plauenblod.component.routes.createRoute.CreateRouteSheet
import com.example.plauenblod.component.routes.createRoute.MenuButton
import com.example.plauenblod.component.routes.editRoutes.DeleteRouteDialogs
import com.example.plauenblod.component.routes.editRoutes.RouteActionSheet
import com.example.plauenblod.component.routes.routesList.RouteListView
import com.example.plauenblod.extension.hallSection
import com.example.plauenblod.extension.toOffset
import com.example.plauenblod.extension.toRelativePosition
import com.example.plauenblod.feature.route.model.Route
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.RelativePosition
import com.example.plauenblod.feature.route.model.routeProperty.Sector
import com.example.plauenblod.feature.auth.viewmodel.AuthViewModel
import com.example.plauenblod.feature.route.viewmodel.RouteViewModel
import com.example.plauenblod.viewmodel.state.DialogState
import com.example.plauenblod.viewmodel.state.RouteFormState
import com.example.plauenblod.viewmodel.state.hasChangedComparedTo
import com.example.plauenblod.viewmodel.state.saver.RouteFormStateSaver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

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
    var selectedHall by remember { mutableStateOf(HallSection.FRONT) }
    var query by remember { mutableStateOf("") }
    var showMap by remember { mutableStateOf(true) }
    val userRole by authViewModel.userRole.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }
    val allRoutes by routeViewModel.routes.collectAsState()
    val filteredRoutes = allRoutes.filter {
        it.sector.hallSection() == selectedHall
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Route erstellen
    var formState by rememberSaveable(stateSaver = RouteFormStateSaver.saver()) {
        mutableStateOf(RouteFormState())
    }
    val routeCreated by routeViewModel.routeCreated.collectAsState()
    var refreshKey by remember { mutableStateOf(0) }
    val takenNumbers = allRoutes
        .filter { it.sector == formState.sector }
        .map { it.number }
    val availableNumbers = (1..25).filterNot { it in takenNumbers }

    //Route suchen
    var showBoulderSearchBar by remember { mutableStateOf(false) }

    // Route bearbeiten
    var selectedRoute by remember { mutableStateOf<Route?>(null) }
    var showRouteMenu by remember { mutableStateOf(false) }
    var originalFormState by remember { mutableStateOf(RouteFormState()) }

    //Route löschen
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Hidden) }
    val snackBarHostState = remember { SnackbarHostState() }

    // CancelDialog
    var showCancelDialog by remember { mutableStateOf(false) }
    val errorMessage by routeViewModel.errorMessage.collectAsState()
    val isCreateEnabled = formState.selectedPoint != null &&
            formState.name.isNotBlank() &&
            formState.setter.isNotBlank() &&
            formState.description.isNotBlank()

    fun hasUnsavedData(): Boolean {
        return if (formState.isEditMode) {
            formState.hasChangedComparedTo(originalFormState)
        } else {
            formState.name.isNotBlank() ||
                    formState.setter.isNotBlank() ||
                    formState.description.isNotBlank() ||
                    formState.selectedPoint != null
        }
    }

    val groupedRoutes = filteredRoutes.groupBy { it.sector }

    fun resetCreateRouteForm() {
        formState = RouteFormState()
        routeViewModel.clearError()
    }

    LaunchedEffect(allRoutes) {
        routeViewModel.loadRoutes()
    }

    LaunchedEffect(routeCreated) {
        if (routeCreated?.isSuccess == true) {
            originalFormState = formState
            resetCreateRouteForm()
            routeViewModel.clearRouteCreatedStatus()
            refreshKey++
            navController.popBackStack()
            navController.navigate(BoulderRoute)
            dialogState = DialogState.ShowCreateSuccess
        }
    }

    LaunchedEffect(availableNumbers) {
        if (availableNumbers.isNotEmpty()) {
            formState = formState.copy(number = availableNumbers.first())
        }
    }

    LaunchedEffect(dialogState) {
        when (dialogState) {
            DialogState.ShowDeleteSuccess -> {
                delay(2000)
                dialogState = DialogState.Hidden
                scope.launch {
                    snackBarHostState.showSnackbar("Route erfolgreich gelöscht")
                }
            }

            DialogState.ShowCreateSuccess,
            DialogState.ShowEditSuccess -> {
                delay(2000)
                dialogState = DialogState.Hidden
                scope.launch {
                    sheetState.hide()
                    showCreateSheet = false
                }
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routen") },
                actions = {
                    Row {
                        IconButton(
                            onClick = { showBoulderSearchBar = !showBoulderSearchBar }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Route suchen",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        MenuButton(
                            currentUserRole = userRole,
                            showMap = showMap,
                            onToggleView = { showMap = !showMap },
                            onCreateRouteClick = {
                                showCreateSheet = true
                            },
                            onFilterClick = {
                                println("Filter geöffnet")
                            }
                        )
                    }

                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showBoulderSearchBar) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        BoulderSearchBar(
                            query = query,
                            onQueryChanged = { query = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = modifier.height(8.dp))

                HallSectionButtons(
                    selectedHall = selectedHall,
                    onHallSelected = { selectedHall = it },
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = modifier.height(8.dp))

                key(refreshKey, selectedHall) {
                    if (showMap) {
                        HallMapScreen(
                            hall = selectedHall,
                            routes = filteredRoutes,
                            refreshKey = refreshKey,
                            navController = navController,
                            onRouteLongClick = {
                                selectedRoute = it
                                showRouteMenu = true
                            }
                        )
                    } else {
                        RouteListView(
                            groupedRoutes = groupedRoutes,
                            onRouteClick = { route ->
                                navController.navigate(BoulderDetailRoute(route.id))
                            },
                            onLongRouteClick = { route ->
                                selectedRoute = route
                                showRouteMenu = true
                            },
                            navController = navController
                        )
                    }
                }


                if (showCreateSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            if (hasUnsavedData()) {
                                dialogState = DialogState.ShowCancelDialog
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
                            name = formState.name,
                            onNameChange = { formState = formState.copy(name = it) },
                            hall = formState.hall,
                            onHallChange = { formState = formState.copy(hall = it) },
                            sector = formState.sector,
                            sectors = Sector.entries.filter { it.hallSection() == formState.hall },
                            onSectorChange = { formState = formState.copy(sector = it) },
                            holdColor = formState.holdColor,
                            onHoldColorChange = { formState = formState.copy(holdColor = it) },
                            difficulty = formState.difficulty,
                            onDifficultyChange = { formState = formState.copy(difficulty = it) },
                            number = formState.number,
                            onNumberChange = { formState = formState.copy(number = it) },
                            description = formState.description,
                            onDescriptionChange = { formState = formState.copy(description = it) },
                            setter = formState.setter,
                            onSetterChange = { formState = formState.copy(setter = it) },
                            selectedPoint = formState.selectedPoint?.toOffset(),
                            onPointSelected = {
                                formState = formState.copy(selectedPoint = it?.toRelativePosition())
                            },
                            showMap = showMap,
                            onDismissMap = { showMap = false },
                            onSelectPointClick = { showMap = true },
                            onCancelClick = { showCancelDialog = true },
                            onDismiss = {
                                scope.launch {
                                    sheetState.hide()
                                    showCreateSheet = false
                                    resetCreateRouteForm()
                                    routeViewModel.clearError()
                                }
                            },
                            onCreateClick = {
                                if (formState.isEditMode) {
                                    formState.routeId?.let { id ->
                                        routeViewModel.editRoute(
                                            userRole!!,
                                            id,
                                            Route(
                                                id = id,
                                                name = formState.name,
                                                hall = formState.hall,
                                                sector = formState.sector!!,
                                                holdColor = formState.holdColor!!,
                                                difficulty = formState.difficulty!!,
                                                number = formState.number,
                                                description = formState.description,
                                                setter = formState.setter,
                                                x = formState.selectedPoint!!.x,
                                                y = formState.selectedPoint!!.y,
                                                points = formState.points
                                            )
                                        )
                                    }
                                    dialogState = DialogState.ShowEditSuccess
                                } else {
                                    routeViewModel.createRoute(
                                        Route(
                                            id = "",
                                            name = formState.name,
                                            hall = formState.hall,
                                            sector = formState.sector!!,
                                            holdColor = formState.holdColor!!,
                                            difficulty = formState.difficulty!!,
                                            number = formState.number,
                                            description = formState.description,
                                            setter = formState.setter,
                                            x = formState.selectedPoint!!.x,
                                            y = formState.selectedPoint!!.y,
                                            points = formState.points
                                        )
                                    )
                                    dialogState = DialogState.ShowCreateSuccess
                                }
                            },
                            isCreateEnabled = isCreateEnabled,
                            errorMessage = errorMessage,
                            showSuccessDialog = dialogState == DialogState.ShowCreateSuccess,
                            onDismissSuccessDialog = {
                                dialogState = DialogState.Hidden
                                scope.launch {
                                    sheetState.hide()
                                    showCreateSheet = false
                                }
                            },
                            availableNumbers = availableNumbers,
                            dialogState = dialogState
                        )
                    }
                }

                if (showCancelDialog) {
                    CancelDialog(
                        onConfirm = {
                            showCancelDialog = false
                            resetCreateRouteForm()
                            scope.launch {
                                sheetState.hide()
                                showCreateSheet = false
                            }
                        },
                        onDismiss = {
                            dialogState = DialogState.Hidden
                            originalFormState = formState.copy()
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
                        userRole = userRole!!,
                        onDismiss = { showRouteMenu = false },
                        onEdit = { route ->
                            originalFormState = RouteFormState(
                                routeId = route.id,
                                name = route.name,
                                isEditMode = true,
                                sector = route.sector,
                                holdColor = route.holdColor,
                                difficulty = route.difficulty,
                                number = route.number,
                                description = route.description,
                                setter = route.setter,
                                selectedPoint = RelativePosition(route.x, route.y)
                            )
                            formState = originalFormState
                            showCreateSheet = true
                        },
                        onDelete = {
                            showRouteMenu = false
                            dialogState = DialogState.ShowDeleteConfirm
                        }
                    )
                }

                when (dialogState) {
                    DialogState.ShowDeleteConfirm -> {
                        DeleteRouteDialogs(
                            showConfirmDialog = true,
                            onConfirmDismiss = { dialogState = DialogState.Hidden },
                            onDeleteConfirmed = {
                                selectedRoute?.let { route ->
                                    routeViewModel.deleteRoute(userRole!!, route.id)
                                    dialogState = DialogState.ShowDeleteSuccess
                                    selectedRoute = null
                                }
                            }
                        )
                    }

                    DialogState.ShowCancelDialog -> {
                        CancelDialog(
                            onConfirm = {
                                dialogState = DialogState.Hidden
                                scope.launch {
                                    sheetState.hide()
                                    showCreateSheet = false
                                }
                            },
                            onDismiss = {
                                dialogState = DialogState.Hidden
                                scope.launch { sheetState.show() }
                            }
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

