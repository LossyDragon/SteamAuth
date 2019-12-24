package com.lossydragon.steamauth.utils

/**
 * Data class containing the format for a .maFile from: Steam, Desktop Auth, SteamTrade-aegamsi
 */
data class MaFileInfo(
    val account_name: String,
    val device_id: String,
    val fully_enrolled: Boolean,
    val identity_secret: String,
    val revocation_code: String,
    val secret_1: String,
    val serial_number: String,
    val server_time: Int,
    val shared_secret: String,
    val status: Int,
    val token_gid: String,
    val uri: String,
    val Session: Session
)

data class Session(
    val OAuthToken: String,
    val SessionID: String,
    val SteamID: Long,
    val SteamLogin: String,
    val SteamLoginSecure: String,
    val WebCookie: String
)
