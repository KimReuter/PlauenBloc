package com.example.plauenblod.feature.dashboard.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.dashboard.model.NewsPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsPostDialog(
    show: Boolean,
    initialPost: NewsPost? = null,
    onDismiss: () -> Unit,
    onPost: (title: String, content: String, postId: String?) -> Unit
) {
    if (!show) return

    var title by remember(initialPost) { mutableStateOf(initialPost?.title ?: "") }
    var content by remember(initialPost) { mutableStateOf(initialPost?.content ?: "") }

    val minTitleLength = 5
    val minContentLength = 20

    val isValid = title.trim().length >= minTitleLength
            && content.trim().length >= minContentLength

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialPost == null) "Neue News posten" else "News bearbeiten")
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titel") },
                    supportingText = {
                        if (title.trim().length < minTitleLength)
                            Text("Min. $minTitleLength Zeichen", color = MaterialTheme.colorScheme.error)
                    },
                    isError = title.trim().length < minTitleLength,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Inhalt") },
                    supportingText = {
                        if (content.trim().length < minContentLength)
                            Text("Min. $minContentLength Zeichen", color = MaterialTheme.colorScheme.error)
                    },
                    isError = content.trim().length < minContentLength,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onPost(title.trim(), content.trim(), initialPost?.id)
                    onDismiss()
                },
                enabled = isValid
            ) {
                Text(if (initialPost == null) "Posten" else "Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}