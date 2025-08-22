package com.example.plauenblod.extension

import com.example.plauenblod.feature.route.model.routeProperty.HallSection

fun HallSection.displayName(): String = when(this) {
    HallSection.FRONT -> "Vordere Halle"
    HallSection.BACK -> "Hintere Halle"
}