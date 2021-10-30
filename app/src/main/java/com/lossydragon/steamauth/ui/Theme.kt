package com.lossydragon.steamauth.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val isAtLeastS: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        isAtLeastS && isDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        isAtLeastS && !isDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        isDarkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme) {

        val systemsColor = MaterialTheme.colorScheme.secondary
        val uiController = rememberSystemUiController()

        SideEffect {
            uiController.setSystemBarsColor(
                color = systemsColor
            )
        }

        content()
    }
}
