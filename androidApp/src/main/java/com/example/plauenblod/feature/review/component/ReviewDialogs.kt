package com.example.plauenblod.feature.review.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.viewmodel.state.DialogState

@Composable
fun ReviewDialogs(
    dialogState: DialogState,
    onDismissDialog: () -> Unit,
    onCancelConfirm: () -> Unit,
    onCancelBack: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit
) {
    when (dialogState) {
        DialogState.ShowCancelDialog -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text("Bearbeitung verwerfen?") },
                text = { Text("Deine Eingaben gehen verloren.") },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCancelConfirm,
                            modifier = Modifier.weight(1f)
                        ) { Text("Verwerfen") }

                        Button(
                            onClick = onCancelBack,
                            modifier = Modifier.weight(1f)
                        ) { Text("Zurück") }
                    }
                }
            )
        }

        DialogState.ShowDeleteConfirm -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = { Text("Rezension wirklich löschen?") },
                text = { Text("Diese Rezension wird dauerhaft entfernt.") },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDeleteConfirm,
                            modifier = Modifier.weight(1f)
                        ) { Text("Löschen") }

                        Button(
                            onClick = onDeleteCancel,
                            modifier = Modifier.weight(1f)
                        ) { Text("Abbrechen") }
                    }
                }
            )
        }

        else -> {}
    }
}