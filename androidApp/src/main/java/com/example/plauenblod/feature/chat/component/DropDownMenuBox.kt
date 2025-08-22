package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.plauenblod.feature.user.model.UserDto

@Composable
fun DropdownMenuBox(
    users: List<UserDto>,
    selectedUser: UserDto?,
    onUserSelected: (UserDto) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedUser?.userName ?: "Empfänger auswählen")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            users.forEach { user ->
                DropdownMenuItem(
                    text = { Text(user.userName ?: "") },
                    onClick = {
                        onUserSelected(user)
                        expanded = false
                    }
                )
            }
        }
    }
}