package com.example.plauenblod.component.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.plauenblod.android.R

@Composable
fun FrontMap(
    selectedPoint: Offset?,
    onTap: (Offset) -> Unit,
    difficultyColor: Color? = null,
    number: Int? = null
) {
    BoulderMapImage(
        imageResId = R.drawable.boulderhalle_grundriss_vordere_halle,
        selectedPoint = selectedPoint,
        onTap = onTap,
        difficultyColor = difficultyColor,
        number = number
    )
}