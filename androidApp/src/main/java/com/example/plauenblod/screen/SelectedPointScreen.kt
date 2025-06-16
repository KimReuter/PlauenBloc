package com.example.plauenblod.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.plauenblod.component.map.BackMap
import com.example.plauenblod.component.map.FrontMap
import com.example.plauenblod.model.HallSection

@Composable
fun SelectPointScreen(
    hallSection: HallSection,
    onPointSelected: (Float, Float) -> Unit
) {
    var selectedPoint by remember { mutableStateOf<Offset?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Punkt auswählen (${hallSection.name.lowercase().replaceFirstChar { it.uppercase() }})",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Die Karte
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clickable {
                    // Dummy: Für TouchPosition brauchst du Modifier.pointerInput
                    // Diese Variante hier ist fürs Prinzip
                }
        ) {
            when (hallSection) {
                HallSection.FRONT -> FrontMap (
                    selectedPoint = selectedPoint,
                    onTap = { offset -> selectedPoint = offset }
                )
                HallSection.BACK -> BackMap(
                    selectedPoint = selectedPoint,
                    onTap = { offset -> selectedPoint = offset}
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedPoint?.let { onPointSelected(it.x, it.y)}
            },
            enabled = selectedPoint != null
        ) {
            Text("✓ Punkt übernehmen")
        }
    }
}