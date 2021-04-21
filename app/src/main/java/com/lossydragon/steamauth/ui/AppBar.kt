package com.lossydragon.steamauth.ui

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.lossydragon.steamauth.R

@Composable
fun AppBar(
    onCleared: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.White
            )
        },
        actions = { MenuActions(onCleared) },
    )
}
