package com.ghstudios.android.features.preferences

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.ghstudios.android.AppSettings
import com.ghstudios.android.GenericActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.mhgendatabase.R

class PreferencesActivity : GenericActivity() {
    override fun getSelectedSection() = MenuSection.NONE

    override fun createFragment(): Fragment {
        return PreferencesFragment()
    }
}

// temporary list of supported languages.
// this should probably be moved somewhere more relevant
// (probably to the data access layer somewhere...)
val supportedLanguages = mapOf(
        "en" to "English",
        "es" to "Español",
        "fr" to "Français",
        "ja" to "日本語"
)

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppSettings.SETTINGS_FILE_NAME

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val localePref = findPreference("DATA_LOCALE") as ListPreference

        localePref.entries = supportedLanguages.values.toTypedArray()
        localePref.entryValues = supportedLanguages.keys.toTypedArray()
        localePref.value = AppSettings.dataLocale // ensure a value is set
    }
}