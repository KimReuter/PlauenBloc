package com.example.plauenblod.component.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.component.authentication.AuthTextField
import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.Route
import com.example.plauenblod.model.Sector

@Composable
fun CreateRouteSheet(
    onDismiss: () -> Unit,
    onSave: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var selectedSector by remember { mutableStateOf<Sector?>(null) }
    var holdColor by remember { mutableStateOf<HoldColor?>(null) }
    var difficulty by remember { mutableStateOf<Difficulty?>(null) }
    var description by remember { mutableStateOf("") }
    var selectedNumber by remember { mutableStateOf(1) }
    var setter by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
    ) {
        AuthTextField(
            value = name,
            onValueChange = { name = it },
            label = "Name",
            leadingIcon = Icons.Default.Create
        )

        DropdownSelector(
            label = "Sektor",
            options = Sector.values().toList(),
            selected = selectedSector,
            onSelected = { selectedSector = it }
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

        AuthTextField(
            value = description,
            onValueChange = { description = it },
            label = "Beschreibung",
            leadingIcon = Icons.Default.Description
        )

        IntDropdownSelector(
            label = "Nummer",
            options = (1..25).toList(),
            selected = selectedNumber,
            onSelected = { selectedNumber = it }
        )

        AuthTextField(
            value = setter,
            onValueChange = { setter = it },
            label = "Schrauber",
            leadingIcon = Icons.Default.Person
        )

        OutlinedButton(
            onClick = {
                if (selectedSector != null && holdColor != null && difficulty != null && selectedNumber != null) {
                    onSave(
                        Route(
                            id = "",
                            name = name,
                            sector = selectedSector!!,
                            holdColor = holdColor!!,
                            difficulty = difficulty!!,
                            number = selectedNumber,
                            description = description,
                            setter = setter,
                            x = 0f,
                            y = 0f
                        )
                    )
                    onDismiss()
                }
            }
        ) {
            Text("Route erstellen")
        }
    }
}