package com.lossydragon.steamauth.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.files.FileFilter
import com.afollestad.materialdialogs.files.fileChooser
import com.lossydragon.steamauth.R
import com.lossydragon.steamauth.steamauth.MaFileLoader
import java.util.*

fun requestPermissionsReasoning() {
    // TODO
}

fun Activity.showInfo(callback: (boolean: Boolean) -> Unit) {
    MaterialDialog(this).show {
        title(R.string.dialog_info_title)
        message(R.string.dialog_info_message)
        positiveButton(R.string.action_ok) {
            callback(false)
        }
        negativeButton(R.string.action_exit) {
            callback(true)
            finishAffinity()
        }
        cancelOnTouchOutside(false)
        onCancel { callback(true) }
    }
}

fun Activity.addAccount() {

    val fileFiler: FileFilter = { it.isDirectory || it.name.endsWith(".maFile", true) }
    var maFile: String? = null

    MaterialDialog(this).show {
        title(R.string.dialog_add_account_title)
        fileChooser(filter = fileFiler, waitForPositiveButton = true) { _, file ->
            maFile = file.absolutePath
        }
        positiveButton(R.string.action_select) {
            MaFileLoader.importMaFile(maFile)
            findNavController(R.id.nav_host_fragment).navigate(R.id.SecondFragment)
        }
        negativeButton(R.string.action_cancel)
    }

}

fun Activity.deleteAccount() {
    var isCleared: Boolean
    MaterialDialog(this).show {
        title(R.string.dialog_delete_account_title)
        message(R.string.dialog_delete_account_message)
        positiveButton(R.string.action_delete) {
            isCleared = PrefsManager.deletePrefs()
            if (isCleared)
                finishAffinity()
            else
                Toast.makeText(context, R.string.toast_delete_failed, Toast.LENGTH_LONG).show()
        }
        negativeButton(R.string.action_cancel)
        cancelOnTouchOutside(true)
    }
}
