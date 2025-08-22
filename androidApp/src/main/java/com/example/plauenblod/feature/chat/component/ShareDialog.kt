package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.user.model.UserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDialog(
    itemTypeName: String,
    itemId: String,
    allUsers: List<UserDto>,
    defaultMessage: String,
    onDismiss: () -> Unit,
    onSend: (message: String, recipientId: String, itemId: String) -> Unit
) {
    var message      by rememberSaveable { mutableStateOf(defaultMessage) }
    var query        by rememberSaveable { mutableStateOf("") }
    var expanded     by rememberSaveable { mutableStateOf(false) }
    var selectedUser by rememberSaveable { mutableStateOf<UserDto?>(null) }

    // Neu: FokusRequester erzeugen
    val focusRequester = remember { FocusRequester() }

    val suggestions = remember(query, allUsers) {
        if (query.isBlank()) allUsers
        else allUsers.filter { it.userName?.contains(query, true) == true }
    }

    // Neu: Beim ersten Composable-Aufbau Fokus anfordern
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$itemTypeName teilen") },
        text = {
            Column {
                Text("Nachricht:")
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Text("Empfänger:")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { /* wird durch Fokus gesteuert */ }
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { new ->
                            query = new
                            selectedUser = null
                            expanded = true
                            // Neu: Fokus nach jeder Änderung halten
                            focusRequester.requestFocus()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            // Neu: Das Textfeld über den FocusRequester kontrollieren
                            .focusRequester(focusRequester)
                            // Neu: Öffne/Schließe Dropdown je nach Fokus
                            .onFocusChanged { expanded = it.isFocused },
                        label = { Text("Empfänger") },
                        placeholder = { Text("Name eingeben…") },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = {
                                    query = ""
                                    selectedUser = null
                                    focusRequester.requestFocus()  // Fokus zurückholen
                                }) {
                                    Icon(Icons.Default.Close, "Löschen")
                                }
                            } else {
                                IconButton(onClick = {
                                    expanded = !expanded
                                    focusRequester.requestFocus()  // Fokus behalten
                                }) {
                                    Icon(Icons.Default.ArrowDropDown, "Dropdown")
                                }
                            }
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded && suggestions.isNotEmpty(),
                        onDismissRequest = { expanded = false }
                    ) {
                        suggestions.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.userName.orEmpty()) },
                                onClick = {
                                    selectedUser = user
                                    query = user.userName.orEmpty()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedUser?.let {
                        onSend(message, it.uid!!, itemId)
                        onDismiss()
                    }
                },
                enabled = selectedUser != null
            ) { Text("Teilen") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}