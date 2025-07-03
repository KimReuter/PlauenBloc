package com.example.plauenblod.component.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RouteActionButtons(
    onReviewClick: () -> Unit,
    onAddToListClick: () -> Unit,
    isReviewEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onReviewClick,
            enabled = isReviewEnabled
        ) {
            Icon(
                imageVector = Icons.Default.AddComment,
                contentDescription = "Route bewerten",
                modifier = modifier.padding(horizontal = 32.dp)
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        OutlinedButton(
            onClick = onAddToListClick
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Route zu Liste hinzufügen",
                modifier = modifier.padding(horizontal = 32.dp)
            )
        }
    }
}