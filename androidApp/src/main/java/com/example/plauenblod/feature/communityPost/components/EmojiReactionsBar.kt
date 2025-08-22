package com.example.plauenblod.feature.communityPost.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmojiReactionsBar(
    reactions: Map<String, Int>,
    onReact: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            reactions
                .filter { it.value > 0 }
                .forEach { (emoji, count) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onReact(emoji) }
                            .padding(4.dp)
                    ) {
                        Text(emoji, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.width(4.dp))
                        Text(count.toString(), style = MaterialTheme.typography.bodySmall)
                    }
                }

            IconButton(onClick = { showPicker = !showPicker }) {
                Icon(Icons.Default.Add, contentDescription = "Reaktion hinzufügen")
            }
        }

        AnimatedVisibility(visible = showPicker) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf("👍", "❤️", "😂", "🎉", "😮", "😢", "😡").forEach { emoji ->
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .clickable {
                                onReact(emoji)
                                showPicker = false
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}