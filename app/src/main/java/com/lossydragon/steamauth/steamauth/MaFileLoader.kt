package com.lossydragon.steamauth.steamauth

import com.google.gson.Gson
import com.lossydragon.steamauth.utils.MaFileInfo
import com.lossydragon.steamauth.utils.PrefsManager
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

object MaFileLoader {

    fun importMaFile(file: String?) {

        if (file.isNullOrEmpty()) {
            throw Exception("importMaFile file is null or empty")
        }

        try {
            val maFile = File(file)
            val fileReader = FileReader(maFile)
            val buffReader = BufferedReader(fileReader)
            val gson = Gson()
            val maFileInfo = gson.fromJson(buffReader, MaFileInfo::class.java)

            //Save the JSON data to preferences
            /* MaFile */
            PrefsManager.accountName = maFileInfo.account_name
            PrefsManager.deviceID = maFileInfo.device_id
            PrefsManager.fullyEnrolled = maFileInfo.fully_enrolled
            PrefsManager.identitySecret = maFileInfo.identity_secret
            PrefsManager.revocationCode = maFileInfo.revocation_code
            PrefsManager.secret1 = maFileInfo.secret_1
            PrefsManager.serialNumber = maFileInfo.serial_number
            PrefsManager.serverTime = maFileInfo.server_time
            PrefsManager.sharedSecret = maFileInfo.shared_secret
            PrefsManager.status = maFileInfo.status
            PrefsManager.tokenGid = maFileInfo.token_gid
            PrefsManager.uri = maFileInfo.uri
            /* MaFile - Session */
            PrefsManager.oAuthToken = maFileInfo.Session.OAuthToken
            PrefsManager.sessionID = maFileInfo.Session.SessionID
            PrefsManager.steamID = maFileInfo.Session.SteamID
            PrefsManager.steamLogin = maFileInfo.Session.SteamLogin
            PrefsManager.steamLoginSecure = maFileInfo.Session.SteamLoginSecure
            PrefsManager.webCookie = maFileInfo.Session.WebCookie

        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("importMaFile failed to import")
        }
    }
}
