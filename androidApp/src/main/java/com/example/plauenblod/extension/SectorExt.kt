package com.example.plauenblod.extension

import com.example.plauenblod.feature.route.model.routeProperty.HallSection
import com.example.plauenblod.feature.route.model.routeProperty.Sector

fun Sector.hallSection(): HallSection = when (this) {
    Sector.ROCO_DE_LA_FINESTRA -> HallSection.FRONT
    Sector.MASSONE -> HallSection.FRONT
    Sector.TREBENNA -> HallSection.FRONT
    Sector.SONNENPLATTE -> HallSection.FRONT
    Sector.GRAND_BROWEDA -> HallSection.BACK
    Sector.DIEBESLOCH -> HallSection.BACK
    Sector.MONTE_CUCCO -> HallSection.BACK
    Sector.HÖLLENHUND -> HallSection.BACK
}

fun Sector.displayName(): String {
    return name
        .lowercase()
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() }}
}