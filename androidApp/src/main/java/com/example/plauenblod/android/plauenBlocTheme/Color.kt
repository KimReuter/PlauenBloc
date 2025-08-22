package com.example.plauenblod.android.plauenBlocTheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


val BoulderGreen = Color(0xFF4CAF50)
val BoulderOrange = Color(0xFFFF9800)
val BoulderRed = Color(0xFFE53935)
val BoulderWhite = Color(0xFFFFFFFF)
val BoulderDark = Color(0xFF121212)
val BoulderGray = Color(0xFF9E9E9E)

val LightColorScheme = lightColorScheme(
    primary = BoulderGreen,
    secondary = BoulderOrange,
    tertiary = BoulderGray,
    error = BoulderRed,
    background = BoulderWhite,
    surface = BoulderWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

val DarkColorScheme = darkColorScheme(
    primary = BoulderGreen,
    secondary = BoulderOrange,
    tertiary = BoulderGray,
    error = BoulderRed,
    background = BoulderDark,
    surface = BoulderDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)