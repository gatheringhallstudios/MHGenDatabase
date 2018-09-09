package com.ghstudios.android

import android.app.Application
import com.ghstudios.android.data.database.DataManager

class MHApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize app settings
        AppSettings.bindApplication(this)
        AssetLoader.bindApplication(this)
        DataManager.bindApplication(this)
    }
}