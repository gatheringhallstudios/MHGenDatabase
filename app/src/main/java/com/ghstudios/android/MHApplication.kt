package com.ghstudios.android

import android.app.Application

class MHApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize app settings
        AppSettings.bindApplication(this)
        AssetLoader.bindApplication(this)
    }
}