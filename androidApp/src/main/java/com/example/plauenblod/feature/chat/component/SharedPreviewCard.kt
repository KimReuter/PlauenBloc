package com.example.plauenblod.feature.chat.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SharedPreviewCard(
    title: String,
    details: List<String>,
    stats: List<Pair<String, String>> = emptyList(),
    sharedMessage: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))

            details.forEach { line ->
                Text(text = line, style = MaterialTheme.typography.bodyMedium)
            }
            if (sharedMessage != null || stats.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            sharedMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            if (stats.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    stats.forEach { (label, value) ->
                        Text(text = "$label: $value", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
