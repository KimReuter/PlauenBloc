package com.example.plauenblod.feature.route.component.createRoute

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CancelDialog(
    onConfirm: () -> Unit,
    onDismiss:() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Erstellen abbrechen?")},
        text = { Text("Alle bisher eingegebenen Daten gehen verloren.") },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDismiss) {
                    Text("Weitermachen")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onConfirm) {
                    Text("Verwerfen")
                }

            }
        }
    )
}