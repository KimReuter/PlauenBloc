package com.example.plauenblod.extension

import androidx.compose.ui.geometry.Offset
import com.example.plauenblod.model.routeProperty.RelativePosition

fun Offset.toRelativePosition(): RelativePosition {
    return RelativePosition(x = this.x, y = this.y)
}

fun RelativePosition.toOffset(): Offset {
    return Offset(x = this.x, y = this.y)
}