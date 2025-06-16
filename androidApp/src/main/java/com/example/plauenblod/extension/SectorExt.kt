package com.example.plauenblod.extension

import com.example.plauenblod.model.HallSection
import com.example.plauenblod.model.Sector

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