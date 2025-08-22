package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    initialText: String = "",
    onSend: (String) -> Unit
) {
    var messageText by remember { mutableStateOf(initialText) }
    var buttonIsEnabled = messageText.isNotBlank()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(initialText) {
        messageText = initialText
    }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Nachricht schreiben…") },
            leadingIcon = {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEmotions,
                        contentDescription = "Emoji auswählen"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (messageText.isNotBlank()) {
                    onSend(messageText.trim())
                    messageText = ""
                }
            },
            enabled = buttonIsEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Senden",
                tint = if (buttonIsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
        }
    }

    if (expanded) {
        ModalBottomSheet(
            onDismissRequest = { expanded = false }
        ) {
            EmojiGrid { emoji ->
                messageText += emoji
                expanded = false
            }
        }
    }

}