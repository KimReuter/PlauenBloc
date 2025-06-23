package com.example.plauenblod.component.createRoute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.plauenblod.android.R
import com.example.plauenblod.component.LabeledButton
import com.example.plauenblod.component.LabeledTextField
import com.example.plauenblod.component.map.BoulderMap
import com.example.plauenblod.extension.hallSection
import com.example.plauenblod.extension.toColor
import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.RelativePosition
import com.example.plauenblod.model.Sector

@Composable
fun CreateRouteForm(
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
    onSelectPointClick: () -> Unit,
    onCreateClick: () -> Unit,
    isCreateEnabled: Boolean,
    errorMessage: String?,
    showMap: Boolean,
    onDismissMap: () -> Unit,
    selectedPoint: Offset?,
    onPointSelected: (Offset) -> Unit,
    onCancelClick: () -> Unit,
    availableNumbers: List<Int>
) {
    val imageResId = when (hall) {
        HallSection.FRONT -> R.drawable.boulderhalle_grundriss_vordere_halle_kleiner
        HallSection.BACK -> R.drawable.boulderhalle_grundriss_hinterehalle_kleiner
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { onCancelClick() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Abbrechen")
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "Neue Route erstellen",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )

        if (!errorMessage.isNullOrBlank()) {
            CreateRouteErrorMessage(errorMessage)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LabeledTextField(
            value = name,
            onValueChange = onNameChange,
            label = "Name",
            placeholder = "Wie heißt die Route?",
            leadingIcon = Icons.Default.Create
        )

        DropdownSelector(
            label = "Hallenteil",
            options = HallSection.values().toList(),
            selected = hall,
            onSelected = onHallChange
        )

        DropdownSelector(
            label = "Sektor",
            options = sectors,
            selected = sector,
            onSelected = onSectorChange
        )

        DropdownSelector(
            label = "Grifffarbe",
            options = HoldColor.values().toList(),
            selected = holdColor,
            onSelected = onHoldColorChange
        )

        DropdownSelector(
            label = "Schwierigkeit",
            options = Difficulty.values().toList(),
            selected = difficulty,
            onSelected = onDifficultyChange
        )

        IntDropdownSelector(
            label = "Nummer",
            options = availableNumbers,
            selected = number,
            onSelected = onNumberChange
        )

        if (availableNumbers.isEmpty()) {
            Text("Alle Nummern im gewählten Sektor sind bereits vergeben", color = MaterialTheme.colorScheme.error)
        }

        if (showMap) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Wähle einen Punkt auf der Karte:")

                BoulderMap(
                    imageResId = imageResId,
                    selectedPoint = selectedPoint?.let { RelativePosition(it.x, it.y) },
                    difficultyColor = difficulty?.toColor(),
                    number = number,
                    onTap = { relativePosition ->
                        onPointSelected(Offset(relativePosition.x, relativePosition.y))
                    },
                    enableZoom = true,
                    routes = emptyList(),
                    onRouteLongClick = { }
                )


            Button(
                onClick = { onDismissMap() },
                enabled = selectedPoint != null,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("✓ Punkt übernehmen")
            }
        }

        LabeledButton(
            label = "Koordinaten",
            buttonText = if (selectedPoint != null) "Punkt: ✓ gewählt" else "Punkt auf Karte festlegen",
            onClick = onSelectPointClick,
            enabled = !showMap
        )

        LabeledTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Beschreibung",
            placeholder = "Wie fühlt sich die Route an?",
            leadingIcon = Icons.Default.Description
        )

        LabeledTextField(
            value = setter,
            onValueChange = onSetterChange,
            label = "Routesetter",
            placeholder = "Wer hat die Route geschraubt?",
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        CreateRouteButton(
            enabled = isCreateEnabled,
            onClick = onCreateClick
        )
    }
}