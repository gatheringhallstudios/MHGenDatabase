package com.ghstudios.android

import android.app.Application
import com.ghstudios.android.data.DataManager

// note: android studio might say that MHApplication is never used, but it is used (automatically)

/**
 * The main entry point for the application. Initialize all global objects here.
 */
class MHApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // initialize app settings
        AppSettings.bindApplication(this)
        AssetLoader.bindApplication(this)
        DataManager.bindApplication(this)
    }
}