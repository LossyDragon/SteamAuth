package com.lossydragon.steamauth.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lossydragon.steamauth.R

@Composable
fun MenuActions(
    onCleared: () -> Unit,
    onShowDialog: () -> Unit,
) {
    IconButton(
        onClick = onCleared
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.action_delete)
        )
    }
    IconButton(
        onClick = onShowDialog,
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = stringResource(id = R.string.action_info)
        )
    }
}
