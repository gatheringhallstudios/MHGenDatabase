package com.ghstudios.android

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class AppSettings {
    companion object {
        private var application : Application? = null

        @JvmStatic
        fun bindApplication(app : Application) {
            application = app
        }

        private val sharedPreferences : SharedPreferences
            get() {
                if (application == null) {
                    throw UninitializedPropertyAccessException("Application not initialized")
                }
                return application!!.applicationContext.getSharedPreferences("APP", MODE_PRIVATE)
            }

        @JvmStatic
        val isJapaneseEnabled
            get() = sharedPreferences.getBoolean(JAPANESE_ENABLED, false)

        // keys
        private val JAPANESE_ENABLED = "JAPANESE_ENABLED"
    }
}