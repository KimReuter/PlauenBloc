package com.example.plauenblod.feature.route.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.route.component.createRoute.DropdownSelector
import com.example.plauenblod.feature.route.model.RouteFilter
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty

@Composable
fun FilterBottomSheet(
    currentFilter: RouteFilter,
    onFilterChanged: (RouteFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSetter by remember { mutableStateOf(currentFilter.routeSetter ?: "Alle") }
    val allSetters = listOf("Jens Grimm", "Jörg Schwerdt", "Jörg Band")
    var selectedDifficulty by remember { mutableStateOf(currentFilter.difficulty ?: Difficulty.YELLOW) }


    Column(Modifier.padding(16.dp)) {

        DropdownSelector(
            label = "Schwierigkeit",
            options = Difficulty.values().toList(),
            selected = selectedDifficulty,
            onSelected = { selectedDifficulty = it }
        )

        DropdownSelector(
            label = "Routesetter:in",
            options = allSetters,
            selected = selectedSetter,
            onSelected = { selectedSetter = it }
        )

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                ) { Text("Abbrechen") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                onFilterChanged(
                    RouteFilter(
                        difficulty = selectedDifficulty,
                        routeSetter = if (selectedSetter == "Alle") null else selectedSetter
                    )
                )
            },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Übernehmen")
            }
        }
    }
}