package com.lossydragon.steamauth

import android.app.Application
import com.lossydragon.steamauth.utils.PrefsManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Init our preferences
        PrefsManager.init(applicationContext)
    }
}
