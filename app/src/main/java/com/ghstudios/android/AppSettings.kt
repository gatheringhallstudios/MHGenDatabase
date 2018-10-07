package com.ghstudios.android

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.ghstudios.android.data.DataManager
import java.util.*

/**
 * A list of languages that are automatically supported by mode default.
 * Only languages where the app is translated are supported as a "default" language.
 * Must be a subset of DataManager.get().getLanguages()
 */
private val defaultLanguages = listOf("en", "de")

/**
 * A static class used to manage shared preferences and application settings.
 * Must be initialized via the AppSettings.bindApplication() function.
 */
class AppSettings {
    companion object {
        @JvmStatic
        val SETTINGS_FILE_NAME = "MHGUDatabase.settings"

        private var application: Application? = null

        /**
         * Initializes AppSettings. Use in the onCreate event of the application object.
         */
        @JvmStatic
        fun bindApplication(app: Application) {
            application = app
        }

        private val sharedPreferences: SharedPreferences
            get() {
                if (application == null) {
                    throw UninitializedPropertyAccessException("Application not initialized")
                }
                return application!!.applicationContext.getSharedPreferences(SETTINGS_FILE_NAME, MODE_PRIVATE)
            }

        @JvmStatic
        val isJapaneseEnabled
            get() = sharedPreferences.getBoolean(PROP_JAPANESE_ENABLED, false)

        /**
         * Returns the "true" data locale setting.
         * This is used when resolving locales, queries should instead use the dataLocale property.
         */
        @JvmStatic
        val trueDataLocale: String
            get() = sharedPreferences.getString(PROP_DATA_LOCALE, "")

        /**
         * Returns the data locale, with the empty locale resolving to the app language, or en if invalid.
         */
        @JvmStatic
        val dataLocale: String
            get() {
                val pref = trueDataLocale
                if (pref.isNotBlank()) {
                    return pref
                }

                val locale = Locale.getDefault().language
                return when (locale) {
                    in defaultLanguages -> locale
                    else -> "en"
                }
            }

        // keys
        private const val PROP_JAPANESE_ENABLED = "JAPANESE_ENABLED"
        const val PROP_DATA_LOCALE = "DATA_LOCALE"
    }
}