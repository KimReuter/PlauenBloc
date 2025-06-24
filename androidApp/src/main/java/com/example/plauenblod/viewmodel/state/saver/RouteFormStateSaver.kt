package com.example.plauenblod.viewmodel.state.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import com.example.plauenblod.model.*
import com.example.plauenblod.viewmodel.state.RouteFormState

object RouteFormStateSaver {
    fun saver(): Saver<RouteFormState, Any> = mapSaver(
        save = { state ->
            mapOf(
                "name" to state.name,
                "hall" to state.hall.name,
                "sector" to state.sector?.name,
                "holdColor" to state.holdColor?.name,
                "difficulty" to state.difficulty?.name,
                "number" to state.number,
                "description" to state.description,
                "setter" to state.setter,
                "x" to state.selectedPoint?.x,
                "y" to state.selectedPoint?.y
            )
        },
        restore = { map ->
            RouteFormState(
                name = map["name"] as? String ?: "",
                hall = HallSection.valueOf(map["hall"] as String),
                sector = (map["sector"] as? String)?.let { Sector.valueOf(it) },
                holdColor = (map["holdColor"] as? String)?.let { HoldColor.valueOf(it) },
                difficulty = (map["difficulty"] as? String)?.let { Difficulty.valueOf(it) },
                number = (map["number"] as? Int) ?: 1,
                description = map["description"] as? String ?: "",
                setter = map["setter"] as? String ?: "",
                selectedPoint = (map["x"] as? Float)?.let { x ->
                    (map["y"] as? Float)?.let { y -> RelativePosition(x, y) }
                }
            )
        }
    )
}