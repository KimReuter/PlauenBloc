package com.example.plauenblod.feature.communityPost.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.communityPost.model.CommunityPost
import com.example.plauenblod.feature.communityPost.model.PostComment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCommentSheet(
    comment: PostComment,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newText by remember { mutableStateOf(comment.content) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Kommentar bearbeiten", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                label = { Text("Kommentar") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Abbrechen") }
                Button(
                    onClick = {
                        onSave(newText)
                    },
                    enabled = newText != comment.content && newText.isNotBlank()
                ) {
                    Text("Speichern")
                }
            }
        }
    }
}