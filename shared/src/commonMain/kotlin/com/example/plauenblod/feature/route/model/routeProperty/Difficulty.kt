package com.example.plauenblod.feature.route.model.routeProperty

import com.example.plauenblod.model.LabeledEnum

enum class Difficulty(override val label: String): LabeledEnum{
    PINK("Rosa"),
    WHITE("Weiß"),
    YELLOW("Gelb"),
    BLUE("Blau"),
    GREEN("Grün"),
    RED("Rot"),
    BROWN("Braun")
}
