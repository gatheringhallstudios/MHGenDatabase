package com.ghstudios.android

import android.app.Application

class MHApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // intiialize app settings
        AppSettings.bindApplication(this)
    }
}