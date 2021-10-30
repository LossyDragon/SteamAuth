package com.lossydragon.steamauth.utils

import android.content.Context
import android.widget.Toast
import com.lossydragon.steamauth.R
import com.lossydragon.steamauth.steamauth.SteamGuardAccount
import java.util.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

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
