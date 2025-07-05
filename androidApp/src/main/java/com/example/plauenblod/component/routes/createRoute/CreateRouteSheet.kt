package com.example.plauenblod.component.routes.createRoute

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import com.example.plauenblod.feature.route.model.routeProperty.Sector
import com.example.plauenblod.viewmodel.state.DialogState

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
    showSuccessDialog: Boolean,
    onDismissSuccessDialog: () -> Unit,
    availableNumbers: List<Int>,
    dialogState: DialogState
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    when (dialogState) {
        DialogState.ShowCreateSuccess -> {
            RouteCreatedDialog(
                title = "Herzlichen Glückwunsch! 🎉",
                message = "Du hast eine neue Route erstellt.",
                onDismiss = {
                    onDismissSuccessDialog()
                    onDismiss()
                }
            )
        }
        DialogState.ShowEditSuccess -> {
            RouteCreatedDialog(
                title = "Änderung gespeichert ✅",
                message = "Du hast die Route erfolgreich geändert.",
                onDismiss = {
                    onDismissSuccessDialog()
                    onDismiss()
                }
            )
        }
        else -> Unit
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
        onCancelClick = { showCancelDialog = true },
        availableNumbers = availableNumbers
    )
}
