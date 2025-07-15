package com.example.plauenblod.viewmodel.state

import com.example.plauenblod.feature.route.model.routeProperty.Difficulty
import com.example.plauenblod.feature.route.model.routeProperty.HoldColor
import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.RelativePosition
import com.example.plauenblod.feature.route.model.routeProperty.Sector

data class RouteFormState(
    val routeId: String = "",
    val name: String = "",
    val isEditMode: Boolean = false,
    val hall: HallSection = HallSection.FRONT,
    val sector: Sector? = Sector.ROCO_DE_LA_FINESTRA,
    val holdColor: HoldColor? = HoldColor.GREEN,
    val difficulty: Difficulty? = Difficulty.PINK,
    val number: Int = 1,
    val description: String = "",
    val setter: String = "",
    val selectedPoint: RelativePosition? = null,
    val points: Int = 0,
)

fun RouteFormState.hasChangedComparedTo(original: RouteFormState): Boolean {
    return name != original.name ||
            hall != original.hall ||
            sector != original.sector ||
            holdColor != original.holdColor ||
            difficulty != original.difficulty ||
            number != original.number ||
            description != original.description ||
            setter != original.setter ||
            selectedPoint != original.selectedPoint
}