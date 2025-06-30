package com.example.plauenblod.component.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.model.routeProperty.HallSection

@Composable
fun HallSectionButtons(
    selectedHall: HallSection,
    onHallSelected: (HallSection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { onHallSelected(HallSection.BACK) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedHall == HallSection.BACK)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Hintere Halle")
        }

        Button(
            onClick = { onHallSelected(HallSection.FRONT) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedHall == HallSection.FRONT)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Vordere Halle")
        }
    }
}