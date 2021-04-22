package com.lossydragon.steamauth.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkTheme else lightTheme,
    ) {
        val uiController = rememberSystemUiController()
        val background = MaterialTheme.colors.background
        val darkIcons = MaterialTheme.colors.isLight

        SideEffect {
            uiController.setStatusBarColor(
                color = primary
            )
            uiController.setNavigationBarColor(
                color = background,
                darkIcons = darkIcons,
            )
        }

        content()
    }
}
