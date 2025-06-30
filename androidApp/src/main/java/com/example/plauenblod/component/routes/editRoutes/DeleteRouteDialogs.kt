package com.example.plauenblod.component.routes.editRoutes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun DeleteRouteDialogs(
    showConfirmDialog: Boolean,
    onConfirmDismiss: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = onConfirmDismiss,
            title = { Text("Route löschen?") },
            text = { Text("Bist du sicher, dass du die Route dauerhaft löschen möchtest?") },
            confirmButton = {
                Row {
                    Button(
                        onClick = {
                            onConfirmDismiss()
                            onDeleteConfirmed()
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Löschen")
                    }
                    Button(
                        onClick = onConfirmDismiss
                    ) {
                        Text("Abbrechen")
                    }
                }
            }
        )
    }

}