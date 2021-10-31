package com.lossydragon.steamauth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lossydragon.steamauth.R

@Composable
fun AppBar(
    onCleared: () -> Unit,
    onShowDialog: () -> Unit,
) {
    val systemsColor = MaterialTheme.colorScheme.background
    val appBarColor = TopAppBarDefaults.largeTopAppBarColors(containerColor = systemsColor)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SmallTopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                )
            },
            colors = appBarColor,
            actions = { MenuActions(onCleared, onShowDialog) },
        )
        Divider()
    }
}
