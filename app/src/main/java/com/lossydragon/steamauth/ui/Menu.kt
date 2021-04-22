package com.lossydragon.steamauth.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.lossydragon.steamauth.R
import com.lossydragon.steamauth.utils.deleteAccount
import com.lossydragon.steamauth.utils.showInfo

@Composable
fun MenuActions(
    onCleared: () -> Unit,
) {
    val context = LocalContext.current
    IconButton(onClick = {
        context.deleteAccount(onCleared)
    }) {
        Icon(
            imageVector = Icons.Default.Delete,
            tint = Color.White,
            contentDescription = stringResource(id = R.string.action_delete)
        )
    }
    IconButton(onClick = { context.showInfo(onAccept = {}, onCleared = { onCleared() }) }) {
        Icon(
            imageVector = Icons.Default.Info,
            tint = Color.White,
            contentDescription = stringResource(id = R.string.action_info)
        )
    }
}
