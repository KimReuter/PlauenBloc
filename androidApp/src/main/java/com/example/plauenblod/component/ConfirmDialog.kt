package com.example.plauenblod.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmationDialog(
    show: Boolean,
    title: String,
    message: String,
    confirmLabel: String = "OK",
    dismissLabel: String = "Abbrechen",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text(title) },
        text             = { Text(message) },
        confirmButton    = {
            Row {
                Button(
                    onClick = {
                        onConfirm()
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(confirmLabel)
                }
                Button(onClick = onDismiss) {
                    Text(dismissLabel)
                }
            }
        }
    )
}