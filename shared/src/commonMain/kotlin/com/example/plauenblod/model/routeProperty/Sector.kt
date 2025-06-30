package com.example.plauenblod.model.routeProperty

import com.example.plauenblod.model.LabeledEnum

enum class Sector(override val label: String): LabeledEnum {
    ROCO_DE_LA_FINESTRA("Roco de la Finestra"),
    MASSONE("Massone"),
    TREBENNA("Trebenna"),
    SONNENPLATTE("Sonnenplatte"),
    GRAND_BROWEDA("Grand Broweda"),
    DIEBESLOCH("Diebesloch"),
    MONTE_CUCCO("Monte Cucco"),
    HÖLLENHUND("Höllenhund")
}