package com.lossydragon.steamauth.steamauth

import android.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Modified from: https://github.com/geel9/SteamAuth/blob/master/SteamAuth/SteamGuardAccount.cs
 */

object SteamGuardAccount {

    // TODO: Option to sync time?

    private val STEAM_GUARD_CODE_TRANSLATIONS = byteArrayOf(
        50, 51, 52, 53, 54, 55, 56, 57, 66, 67, 68, 70, 71,
        72, 74, 75, 77, 78, 80, 81, 82, 84, 86, 87, 88, 89
    )

    private const val ERROR = "Error"

    fun generateSteamGuardCodeForTime(shared_secret: String, time: Long): String {

        if (shared_secret.isBlank()) {
            return ERROR
        }

        var t = time
        t /= 30L

        val sharedSecretArray: ByteArray = Base64.decode(shared_secret, Base64.DEFAULT)
        val timeArray = ByteArray(8)

        for (i in 8 downTo 1) {
            timeArray[i - 1] = t.toByte()
            t = t shr 8
        }

        return try {
            val signingKey = SecretKeySpec(sharedSecretArray, "HmacSHA1")
            val mac: Mac = Mac.getInstance("HmacSHA1")
            mac.init(signingKey)
            val hashedData: ByteArray = mac.doFinal(timeArray)
            val b = (hashedData[19].toInt() and 0xF)

            var codePoint: Int =
                hashedData[b].toInt() and 0x7F shl 24 or
                    (hashedData[b + 1].toInt() and 0xFF shl 16) or
                    (hashedData[b + 2].toInt() and 0xFF shl 8) or
                    (hashedData[b + 3].toInt() and 0xFF)

            val codeArray = ByteArray(5)
            val translationSize = STEAM_GUARD_CODE_TRANSLATIONS.size
            for (i in 0..4) {
                codeArray[i] = STEAM_GUARD_CODE_TRANSLATIONS[codePoint % translationSize]
                codePoint /= translationSize
            }

            return codeArray.toString(Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            ERROR
        }
    }
}
