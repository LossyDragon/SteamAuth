package com.lossydragon.steamauth.utils

import android.content.Context
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.lossydragon.steamauth.R

fun requestPermissionsReasoning() {
    // TODO
}

fun Context.showInfo(
    onCleared: () -> Unit
) {
    MaterialDialog(this).show {
        title(R.string.dialog_info_title)
        message(R.string.dialog_info_message)
        positiveButton(R.string.action_ok) {
            PrefsManager.firstTime = false
        }
        negativeButton(R.string.action_exit) {
            PrefsManager.firstTime = true
            onCleared()
        }
        cancelOnTouchOutside(false)
        onCancel { PrefsManager.firstTime = true }
    }
}

fun Context.deleteAccount(onCleared: () -> Unit) {
    var isCleared: Boolean
    MaterialDialog(this).show {
        title(R.string.dialog_delete_account_title)
        message(R.string.dialog_delete_account_message)
        positiveButton(R.string.action_delete) {
            isCleared = PrefsManager.deletePrefs()
            if (isCleared)
                onCleared()
            else
                Toast.makeText(context, R.string.toast_delete_failed, Toast.LENGTH_LONG).show()
        }
        negativeButton(R.string.action_cancel)
        cancelOnTouchOutside(true)
    }
}
