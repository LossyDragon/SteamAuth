package com.lossydragon.steamauth.steamauth

import android.util.Log
import com.google.gson.Gson
import com.lossydragon.steamauth.model.MaFileInfo
import com.lossydragon.steamauth.utils.PrefsManager

object MaFileLoader {

    private val TAG = MaFileLoader::class.java.simpleName

    fun importMaFile(file: String?): Boolean {

        if (file.isNullOrEmpty()) {
            Log.w(TAG, "importMaFile file is null or empty")
            return false
        }

        try {
            val maFileInfo = Gson().fromJson(file, MaFileInfo::class.java)

            // Save the JSON data to preferences
            PrefsManager.run {
                /* MaFile */
                accountName = maFileInfo.account_name
                deviceID = maFileInfo.device_id
                fullyEnrolled = maFileInfo.fully_enrolled
                identitySecret = maFileInfo.identity_secret
                revocationCode = maFileInfo.revocation_code
                secret1 = maFileInfo.secret_1
                serialNumber = maFileInfo.serial_number
                serverTime = maFileInfo.server_time
                sharedSecret = maFileInfo.shared_secret
                status = maFileInfo.status
                tokenGid = maFileInfo.token_gid
                uri = maFileInfo.uri
                /* MaFile - Session */
                oAuthToken = maFileInfo.Session.OAuthToken
                sessionID = maFileInfo.Session.SessionID
                steamID = maFileInfo.Session.SteamID
                steamLogin = maFileInfo.Session.SteamLogin
                steamLoginSecure = maFileInfo.Session.SteamLoginSecure
                webCookie = maFileInfo.Session.WebCookie
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w(TAG, "importMaFile failed to import")
            return false
        }
    }
}
