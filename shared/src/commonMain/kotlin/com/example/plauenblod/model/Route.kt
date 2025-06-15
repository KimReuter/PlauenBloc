package com.example.plauenblod.model

data class Route(
    val id: String,
    val name: String,
    val sector: Sector,
    val holdColor: HoldColor,
    val difficulty: Difficulty,
    val number: Int,
    val description: String,
    val setter: String,
    val x: Float,
    val y: Float
)