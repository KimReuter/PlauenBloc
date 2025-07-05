package com.example.plauenblod.component.routes.createRoute

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.auth.UserRole

@Composable
fun MenuButton(
    currentUserRole: UserRole?,
    showMap: Boolean,
    onToggleView: () -> Unit,
    onCreateRouteClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Menü",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(220.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            DropdownMenuItem(
                text = { Text(
                    if(showMap) "Listenansicht" else "Kartenansicht",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                    )},
                leadingIcon = { Icon(
                    if (showMap) Icons.Default.List else Icons.Default.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) },
                onClick = {
                    onToggleView()
                    expanded = false
                }
            )

            if (currentUserRole == UserRole.OPERATOR) {
                DropdownMenuItem(
                    text = { Text(
                        "Route erstellen",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                        ) },
                    leadingIcon = { Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                        ) },
                    onClick = {
                        expanded = false
                        onCreateRouteClick()
                    }
                )
            }

            DropdownMenuItem(
                text = { Text(
                    "Filter anzeigen",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                    ) },
                leadingIcon = { Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                    ) },
                onClick = {
                    expanded = false
                    onFilterClick()
                }
            )
        }
    }
}