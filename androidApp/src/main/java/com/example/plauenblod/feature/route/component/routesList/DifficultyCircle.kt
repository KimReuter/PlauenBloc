package com.example.plauenblod.feature.route.component.routesList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plauenblod.extension.toColor
import com.example.plauenblod.feature.route.model.routeProperty.Difficulty

@Composable
fun DifficultyCircle(
    difficulty: Difficulty,
    number: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .background(difficulty.toColor(), shape = CircleShape)
    ) {
        Text(
            text = number.toString(),
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}

