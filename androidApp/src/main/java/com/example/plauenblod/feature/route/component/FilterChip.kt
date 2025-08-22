package com.example.plauenblod.feature.route.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterChip(text: String, onRemove: () -> Unit) {
    AssistChip(
        onClick = { onRemove() },
        label = { Text(text) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Entfernen",
                modifier = Modifier.size(18.dp)
            )
        },
        shape = RoundedCornerShape(20.dp)
    )
}