package com.example.plauenblod.feature.route.model.routeProperty

import com.example.plauenblod.model.LabeledEnum

enum class HallSection(override val label: String): LabeledEnum {
    FRONT("Vordere Halle"),
    BACK("Hintere Halle")
}