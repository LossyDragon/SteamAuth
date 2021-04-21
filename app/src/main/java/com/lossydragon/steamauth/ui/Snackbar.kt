package com.lossydragon.steamauth.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Snackbar(
    snackBarState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        modifier = modifier,
        hostState = snackBarState,
        snackbar = { snack ->
            androidx.compose.material.Snackbar(
                modifier = Modifier.padding(16.dp),
                content = { Text(text = snack.message) },
            )
        }
    )
}
