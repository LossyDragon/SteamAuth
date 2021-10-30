package com.lossydragon.steamauth.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lossydragon.steamauth.R
import com.lossydragon.steamauth.utils.PrefsManager

@Composable
fun DialogRemoveAccount(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.dialog_delete_account_title)) },
        text = { Text(text = stringResource(id = R.string.dialog_delete_account_message)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                content = {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            )
        },
    )
}

@Composable
fun DialogMessage(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            PrefsManager.firstTime = false
            onDismiss()
        },
        title = {
            Text(
                text = stringResource(id = R.string.dialog_info_title),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        text = { Text(text = stringResource(id = R.string.dialog_info_message)) },
        confirmButton = {
            TextButton(
                onClick = {
                    PrefsManager.firstTime = false
                    onConfirm()
                },
                content = {
                    Text(text = stringResource(id = R.string.action_ok))
                }
            )
        },
    )
}
