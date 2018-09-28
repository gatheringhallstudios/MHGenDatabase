package com.ghstudios.android.features.meta

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.ghstudios.android.AppSettings
import com.ghstudios.android.GenericActivity
import com.ghstudios.android.MenuSection
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.mhgendatabase.R

class PreferencesActivity : GenericActivity() {
    override fun getSelectedSection() = MenuSection.NONE

    override fun createFragment(): Fragment {
        return PreferencesFragment()
    }
}

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppSettings.SETTINGS_FILE_NAME

        setPreferencesFromResource(R.xml.preferences, rootKey)

        val localePref = findPreference(AppSettings.PROP_DATA_LOCALE) as ListPreference

        // Create language map. Default language + DB languages
        val supportedLanguages = mapOf(
                "" to getString(R.string.preference_language_default)
        ) + DataManager.get().getLanguages()

        localePref.entries = supportedLanguages.values.toTypedArray()
        localePref.entryValues = supportedLanguages.keys.toTypedArray()
        localePref.value = AppSettings.dataLocale // ensure a value is set
    }
}