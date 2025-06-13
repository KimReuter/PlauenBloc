package com.example.plauenblod.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plauenblod.component.ScreenTitle
import com.example.plauenblod.component.map.BoulderSearchBar
import com.example.plauenblod.component.map.FirstHallMapScreen
import com.example.plauenblod.component.map.SecondHallMapScreen

@Composable
fun RouteScreen(
    modifier: Modifier = Modifier
) {
    var selectedHall by remember { mutableStateOf("first") }
    var query by remember { mutableStateOf("") }
    var showMap by remember { mutableStateOf(true) }

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
                    modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { },

                ) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menü"
                    )
                }

            }

            Spacer(modifier = modifier.height(16.dp))

            Row {
                Button(
                    onClick = { selectedHall = "second" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "second") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("Hintere Halle")
                }

                Spacer(modifier = modifier.width(16.dp))

                Button(
                    onClick = { selectedHall = "first" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedHall == "first") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    ),

                    ) {
                    Text("Vordere Halle")
                }
            }

            if (selectedHall == "first") {
                if (showMap) {
                    FirstHallMapScreen()
                } else {
                    //FirstHallListScreen()
                }
            } else {
                if (showMap) {
                    SecondHallMapScreen()
                } else {
                    //SecondHallListScreen()
                }
            }
        }
    }
}