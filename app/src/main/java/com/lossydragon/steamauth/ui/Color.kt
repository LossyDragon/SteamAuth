package com.lossydragon.steamauth.ui

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val background = Color(0xFF293134)
val primary = Color(0xFF4F829A)

val lightTheme = lightColors(
    primary = primary,
)

val darkTheme = darkColors(
    background = background,
    primary = primary,
    onBackground = Color.White,
)