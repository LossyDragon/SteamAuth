package com.lossydragon.steamauth.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 *  Universal-ish preference singleton
 */

object PrefsManager {

    // Preferences Keys
    private const val FIRST_TIME = "first_time"

    // MaFile
    private const val ACCOUNT_NAME = "account_name"
    private const val DEVICE_ID = "device_id"
    private const val FULLY_ENROLLED = "fully_enrolled"
    private const val IDENTITY_SECRET = "identity_secret"
    private const val REVOCATION_CODE = "revocation_code"
    private const val SECRET_1 = "secret_1"
    private const val SERIAL_NUMBER = "serial_number"
    private const val SERVER_TIME = "server_time"
    private const val SHARED_SECRET = "shared_secret"
    private const val STATUS = "status"
    private const val STEAMID = "steamid"
    private const val TOKEN_GID = "token_gid"
    private const val URI = "uri"

    // MaFile - Session
    private const val OAUTH_TOKEN = "OAuthToken"
    private const val SESSION_ID = "SessionID"
    private const val STEAM_LOGIN = "SteamLogin"
    private const val STEAM_LOGIN_SECURE = "SteamLoginSecure"
    private const val WEB_COOKIE = "WebCookie"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun deletePrefs(): Boolean {
        return prefs.edit().clear().commit()
    }

    private fun writePref(key: String, value: Any?) {
        if (value == null)
            throw Exception("Preference [$key] shouldn't be null!")

        val editor = prefs.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            else -> throw Exception("Something went wrong when saving [$key]")
        }

        editor.apply()
    }

    // MaFile
    var accountName: String
        get() = prefs.getString(ACCOUNT_NAME, "")!!
        set(value) = writePref(ACCOUNT_NAME, value)

    var deviceID: String
        get() = prefs.getString(DEVICE_ID, "")!!
        set(value) = writePref(DEVICE_ID, value)

    var fullyEnrolled: Boolean
        get() = prefs.getBoolean(FULLY_ENROLLED, false)
        set(value) = writePref(FULLY_ENROLLED, value)

    var identitySecret: String
        get() = prefs.getString(IDENTITY_SECRET, "")!!
        set(value) = writePref(IDENTITY_SECRET, value)

    var revocationCode: String
        get() = prefs.getString(REVOCATION_CODE, "")!!
        set(value) = writePref(REVOCATION_CODE, value)

    var secret1: String
        get() = prefs.getString(SECRET_1, "")!!
        set(value) = writePref(SECRET_1, value)

    var serialNumber: String
        get() = prefs.getString(
            SERIAL_NUMBER, ""
        )!!
        set(value) = writePref(SERIAL_NUMBER, value)

    var serverTime: Int
        get() = prefs.getInt(SERVER_TIME, 0)
        set(value) = writePref(SERVER_TIME, value)

    var sharedSecret: String
        get() = prefs.getString(SHARED_SECRET, "")!!
        set(value) = writePref(SHARED_SECRET, value)

    var status: Int
        get() = prefs.getInt(STATUS, 0)
        set(value) = writePref(STATUS, value)

    var steamID: Long
        get() = prefs.getLong(STEAMID, 0)
        set(value) = writePref(STEAMID, value)

    var tokenGid: String
        get() = prefs.getString(TOKEN_GID, "")!!
        set(value) = writePref(TOKEN_GID, value)

    var uri: String
        get() = prefs.getString(URI, "")!!
        set(value) = writePref(URI, value)

    // MaFile - Session
    var oAuthToken: String
        get() = prefs.getString(OAUTH_TOKEN, "")!!
        set(value) = writePref(OAUTH_TOKEN, value)

    var sessionID: String
        get() = prefs.getString(SESSION_ID, "")!!
        set(value) = writePref(SESSION_ID, value)

    var steamLogin: String
        get() = prefs.getString(STEAM_LOGIN, "")!!
        set(value) = writePref(STEAM_LOGIN, value)

    var steamLoginSecure: String
        get() = prefs.getString(STEAM_LOGIN_SECURE, "")!!
        set(value) = writePref(STEAM_LOGIN_SECURE, value)

    var webCookie: String
        get() = prefs.getString(WEB_COOKIE, "")!!
        set(value) = writePref(WEB_COOKIE, value)

    // Application
    var firstTime: Boolean
        get() = prefs.getBoolean(FIRST_TIME, true)
        set(value) = writePref(FIRST_TIME, value)
}
