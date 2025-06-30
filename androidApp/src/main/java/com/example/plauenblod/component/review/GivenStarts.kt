package com.example.plauenblod.component.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun GivenStars(
    stars: Int
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(stars) { star ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Given Stars",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}