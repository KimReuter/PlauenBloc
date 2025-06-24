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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.plauenblod.component.ScreenTitle
import com.example.plauenblod.component.createRoute.CancelDialog
import com.example.plauenblod.component.createRoute.CreateRouteSheet
import com.example.plauenblod.component.createRoute.MenuButton
import com.example.plauenblod.component.editRoutes.DeleteRouteDialogs
import com.example.plauenblod.component.editRoutes.RouteActionSheet
import com.example.plauenblod.component.map.BoulderSearchBar
import com.example.plauenblod.component.map.HallSectionButtons
import com.example.plauenblod.extension.hallSection
import com.example.plauenblod.extension.toOffset
import com.example.plauenblod.extension.toRelativePosition
import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.Route
import com.example.plauenblod.model.Sector
import com.example.plauenblod.viewmodel.AuthViewModel
import com.example.plauenblod.viewmodel.RouteViewModel
import com.example.plauenblod.viewmodel.state.DialogState
import com.example.plauenblod.viewmodel.state.RouteFormState
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

    // Route bearbeiten
    var selectedRoute by remember { mutableStateOf<Route?>(null) }
    var showRouteMenu by remember { mutableStateOf(false) }

    //Route löschen
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Hidden) }

    // CancelDialog
    var showCancelDialog by remember { mutableStateOf(false) }
    val errorMessage by routeViewModel.errorMessage.collectAsState()
    val isCreateEnabled = formState.selectedPoint != null &&
            formState.name.isNotBlank() &&
            formState.setter.isNotBlank() &&
            formState.description.isNotBlank()

    val hasUnsavedData =
        formState.name.isNotBlank() ||
                formState.description.isNotBlank() ||
                formState.setter.isNotBlank() ||
                formState.selectedPoint != null

    fun resetCreateRouteForm() {
        formState = RouteFormState()
        routeViewModel.clearError()
    }

    LaunchedEffect(routeCreated) {
        if (routeCreated?.isSuccess == true) {
            resetCreateRouteForm()
            routeViewModel.clearRouteCreatedStatus()
            refreshKey++
            navController.popBackStack()
            navController.navigate(MapRoute)
            dialogState = DialogState.ShowCreateSuccess
        }
    }

    LaunchedEffect(availableNumbers) {
        if (availableNumbers.isNotEmpty()) {
            formState = formState.copy(number = availableNumbers.first())
        }
    }

    LaunchedEffect(dialogState) {
        if (dialogState == DialogState.ShowDeleteSuccess) {
            delay(2000)
            dialogState = DialogState.Hidden
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

            HallSectionButtons(
                selectedHall = selectedHall,
                onHallSelected = { selectedHall = it },
                modifier = Modifier.padding(top = 16.dp)
            )

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
                    //TODO: Listenansicht
                }
            }


            if (showCreateSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        if (hasUnsavedData) {
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
                        onPointSelected = { formState = formState.copy(selectedPoint = it?.toRelativePosition()) },
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
                            if (formState.selectedPoint != null && formState.sector != null && formState.holdColor != null && formState.difficulty != null) {
                                routeViewModel.createRoute(
                                    Route(
                                        id = "",
                                        name = formState.name,
                                        sector = formState.sector!!,
                                        holdColor = formState.holdColor!!,
                                        difficulty = formState.difficulty!!,
                                        number = formState.number,
                                        description = formState.description,
                                        setter = formState.setter,
                                        x = formState.selectedPoint!!.x,
                                        y = formState.selectedPoint!!.y
                                    )
                                )
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
                        availableNumbers = availableNumbers
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
                                routeViewModel.deleteRoute(route.id)
                                dialogState = DialogState.ShowDeleteSuccess
                                selectedRoute = null
                            }
                        },
                        showSuccessDialog = false,
                        onSuccessDismiss = {}
                    )
                }

                DialogState.ShowDeleteSuccess -> {
                    DeleteRouteDialogs(
                        showConfirmDialog = false,
                        onConfirmDismiss = {},
                        onDeleteConfirmed = {},
                        showSuccessDialog = true,
                        onSuccessDismiss = { dialogState = DialogState.Hidden }
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

                else -> {} // kein Dialog anzeigen
            }
        }
    }
}

