package com.lossydragon.steamauth.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.lossydragon.steamauth.R
import com.lossydragon.steamauth.steamauth.SteamGuardAccount
import java.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

val isAtLeastS: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Normal padding modifier but with 16.dp padding applied to start and end.
 * Additional padding can be added in [Dp]: [start], [top], [end], and [bottom]
 */
fun Modifier.waterfallPadding(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
) = this.padding(start = start + 16.dp, end = end + 16.dp, top = top, bottom = bottom)

fun Context.removeAccount(block: () -> Unit) {
    val isCleared = PrefsManager.deletePrefs()
    if (isCleared)
        block()
    else
        Toast.makeText(
            this,
            R.string.toast_delete_failed,
            Toast.LENGTH_LONG
        ).show()
}

fun String.upperCase(): String = this.uppercase(Locale.getDefault())

fun Context.toast(string: String) = Toast.makeText(this, string, Toast.LENGTH_LONG).show()

fun totpFlow() = flow {
    var progress: Float
    var timeNewCode = 10000.0
    var code = ""

    coroutineScope {
        while (isActive) {
            val codeTime: Double = 30.0 - System.currentTimeMillis() / 1000.0 % 30.0
            val currentTime: Long = System.currentTimeMillis() / 1000L
            val validityTime = 30.0 - codeTime
            progress = (validityTime / 30.0).toFloat()

            val newCode = timeNewCode > validityTime
            timeNewCode = validityTime

            if (newCode) {
                val secret = PrefsManager.sharedSecret
                code = SteamGuardAccount.generateSteamGuardCodeForTime(secret, currentTime)
            }

            delay(10) // Delays before emit, while active.
            emit(Pair(progress, code))
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun getPermissionText(permission: PermissionState): AnnotatedString {
    val permissionString = buildAnnotatedString {
        if (permission.permission.isEmpty()) {
            append("[No Permissions Found?!]")
            return@buildAnnotatedString
        }

        append(permission.permission.substringAfterLast('.'))
    }

    return permissionString
}
