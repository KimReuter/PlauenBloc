package com.example.plauenblod.feature.route.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.plauenblod.feature.route.model.RouteFilter

@Composable
fun ActiveFilterChips(
    filter: RouteFilter,
    onClear: (RouteFilter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val interactionSource = remember { MutableInteractionSource() }

        filter.hall?.let { name ->
            FilterChip(
                selected = true,
                onClick = { onClear(filter.copy(hall = null)) },
                label = { name },
                interactionSource = interactionSource,
                modifier = Modifier
            )
        }

        filter.sector?.let {
            FilterChip(
                selected = true,
                onClick = { onClear(filter.copy(sector = null)) },
                label = { Text(it.name) },
                interactionSource = interactionSource
            )
        }

        filter.holdColor?.let {
            FilterChip(
                selected = true,
                onClick = { onClear(filter.copy(holdColor = null)) },
                label = { Text(it.name) },
                interactionSource = interactionSource
            )
        }

        filter.difficulty?.let {
            FilterChip(
                selected = true,
                onClick = { onClear(filter.copy(difficulty = null)) },
                label = { Text(it.name) },
                interactionSource = interactionSource
            )
        }
    }
}