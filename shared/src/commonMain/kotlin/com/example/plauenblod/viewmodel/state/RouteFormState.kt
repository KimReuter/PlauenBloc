package com.example.plauenblod.viewmodel.state

import com.example.plauenblod.model.Difficulty
import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.HoldColor
import com.example.plauenblod.model.RelativePosition
import com.example.plauenblod.model.Sector

data class RouteFormState(
    val name: String = "",
    val hall: HallSection = HallSection.FRONT,
    val sector: Sector? = Sector.ROCO_DE_LA_FINESTRA,
    val holdColor: HoldColor? = HoldColor.GREEN,
    val difficulty: Difficulty? = Difficulty.PINK,
    val number: Int = 1,
    val description: String = "",
    val setter: String = "",
    val selectedPoint: RelativePosition? = null
)
