package com.lossydragon.steamauth.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.lossydragon.steamauth.R

@Composable
fun AppBar(
    onCleared: () -> Unit,
    onShowDialog: () -> Unit,
) {
    val systemsColor = MaterialTheme.colorScheme.secondary
    val appBarColor = TopAppBarDefaults.largeTopAppBarColors(containerColor = systemsColor)
    SmallTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                color = Color.White
            )
        },
        colors = appBarColor,
        actions = { MenuActions(onCleared, onShowDialog) },
    )
}
