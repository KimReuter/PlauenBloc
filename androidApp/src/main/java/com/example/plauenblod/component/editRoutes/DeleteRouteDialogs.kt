package com.example.plauenblod.component.editRoutes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay


@Composable
fun DeleteRouteDialogs(
    showConfirmDialog: Boolean,
    onConfirmDismiss: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    showSuccessDialog: Boolean,
    onSuccessDismiss: () -> Unit
) {
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = onConfirmDismiss,
            title = { Text("Route löschen?") },
            text = { Text("Bist du sicher, dass du die Route dauerhaft löschen möchtest?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmDismiss()
                        onDeleteConfirmed()
                    }
                ) { Text("Löschen") }
            },
            dismissButton = {
                TextButton(onClick = onConfirmDismiss) {
                    Text("Abbrechen")
                }
            }
        )
    }

    if (showSuccessDialog) {
        LaunchedEffect(Unit) {
            delay(2000)
            onSuccessDismiss()
        }
        AlertDialog(
            onDismissRequest = onSuccessDismiss,
            title = { Text("Erfolgreich gelöscht") },
            text = { Text("Die Route wurde erfolgreich gelöscht.") },
            confirmButton = {
                TextButton(onClick = onSuccessDismiss) {
                    Text("Ok")
                }
            }
        )
    }
}