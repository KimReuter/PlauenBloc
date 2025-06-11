package com.example.plauenblod.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.component.map.BoulderSearchBar
import com.example.plauenblod.component.map.FirstHallMapScreen

@Composable
fun MapScreen(
    modifier: Modifier = Modifier
) {
    var selectedHall by remember { mutableStateOf("first") }
    var query by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = modifier
            .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoulderSearchBar(
                query = query,
                onQueryChanged = { query = it })

            Spacer(modifier = modifier.height(16.dp))

            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { selectedHall = "first"},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "first") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    ),

                ) {
                    Text("Vordere Halle")
                }

                Spacer(modifier = modifier.width(16.dp))

                Button(
                    onClick = { selectedHall = "second" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "second") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Hintere Halle")
                }
            }

            if (selectedHall == "first") {
                FirstHallMapScreen()
            } else {
                //SecondHallMapScreen()
            }
        }
    }
}