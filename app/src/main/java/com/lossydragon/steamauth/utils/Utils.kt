package com.lossydragon.steamauth.utils

import com.lossydragon.steamauth.steamauth.SteamGuardAccount
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.util.*

fun String.upperCase(): String = this.toUpperCase(Locale.getDefault())

fun totpFlow() = flow {
    var progress: Float
    var timeNewCode = 10000.0
    var code = ""

    coroutineScope {
        while (isActive) {
            val codeTime: Double = 30.0 - System.currentTimeMillis() / 1000.0 % 30.0
            val currentTime: Long = System.currentTimeMillis() / 1000L
            val validityTime = 30.0 - codeTime
            progress = (3000.0 * (validityTime / 30.0)).toFloat() / 3000 // :)

            val newCode = timeNewCode > validityTime
            timeNewCode = validityTime

            if (newCode) {
                val secret = PrefsManager.sharedSecret
                code = SteamGuardAccount.generateSteamGuardCodeForTime(secret, currentTime)
            }

            emit(Pair(progress, code))
            delay(50)
        }
    }
}
