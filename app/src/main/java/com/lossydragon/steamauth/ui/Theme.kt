package com.lossydragon.steamauth.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkTheme else lightTheme,
        content = content
    )
}
