package com.example.plauenblod.component.createRoute

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
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
    name: String,
    onNameChange: (String) -> Unit,
    hall: HallSection,
    onHallChange: (HallSection) -> Unit,
    sector: Sector?,
    sectors: List<Sector>,
    onSectorChange: (Sector) -> Unit,
    holdColor: HoldColor?,
    onHoldColorChange: (HoldColor) -> Unit,
    difficulty: Difficulty?,
    onDifficultyChange: (Difficulty) -> Unit,
    number: Int,
    onNumberChange: (Int) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    setter: String,
    onSetterChange: (String) -> Unit,
    selectedPoint: Offset?,
    onPointSelected: (Offset?) -> Unit,
    showMap: Boolean,
    onDismissMap: () -> Unit,
    onSelectPointClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDismiss: () -> Unit,
    onCreateClick: () -> Unit,
    isCreateEnabled: Boolean,
    errorMessage: String?,
    viewModel: RouteViewModel = koinInject()
) {
    val routeCreated by viewModel.routeCreated.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(routeCreated) {
        if (routeCreated?.isSuccess == true) {
            showSuccessDialog = true
            delay(2000)
            showSuccessDialog = false
            onDismiss()
        }
    }

    if (showSuccessDialog) {
        RouteCreatedDialog(onDismiss = {
            showSuccessDialog = false
            onDismiss()
        })
    }

    if (showCancelDialog) {
        CancelDialog(
            onConfirm = {
                showCancelDialog = false
                onDismiss()
            },
            onDismiss = {
                showCancelDialog = false
            }
        )
    }

    CreateRouteForm(
        name, onNameChange,
        hall, onHallChange,
        sector, sectors, onSectorChange,
        holdColor, onHoldColorChange,
        difficulty, onDifficultyChange,
        number, onNumberChange,
        description, onDescriptionChange,
        setter, onSetterChange,
        onSelectPointClick,
        onCreateClick,
        isCreateEnabled,
        errorMessage,
        showMap,
        onDismissMap,
        selectedPoint,
        { onPointSelected(it) },
        onCancelClick = { showCancelDialog = true }
    )
}
