package com.example.plauenblod.feature.route.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.route.model.RouteFilter
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import com.example.plauenblod.feature.route.model.routeProperty.Sector

@Composable
fun FilterBottomSheet(
    currentFilter: RouteFilter,
    onFilterChanged: (RouteFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHall by remember { mutableStateOf(currentFilter.hall) }
    var selectedSector by remember { mutableStateOf(currentFilter.sector) }
    var selectedColor by remember { mutableStateOf(currentFilter.holdColor) }
    var selectedDifficulty by remember { mutableStateOf(currentFilter.difficulty) }

    Column(Modifier.padding(16.dp)) {
        FilterDropdown("Hall", HallSection.values(), selectedHall) { selectedHall = it }
        FilterDropdown("Sektor", Sector.values(), selectedSector) { selectedSector = it }
        FilterDropdown("Farbe", HoldColor.values(), selectedColor) { selectedColor = it }
        FilterDropdown("Schwierigkeit", Difficulty.values(), selectedDifficulty) { selectedDifficulty = it }

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onDismiss) { Text("Abbrechen") }
            Button(onClick = {
                onFilterChanged(
                    RouteFilter(
                        hall = selectedHall,
                        sector = selectedSector,
                        holdColor = selectedColor,
                        difficulty = selectedDifficulty
                    )
                )
            }) {
                Text("Übernehmen")
            }
        }
    }
}