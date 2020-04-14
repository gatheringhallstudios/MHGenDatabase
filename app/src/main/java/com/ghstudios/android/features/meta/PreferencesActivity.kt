package com.ghstudios.android.features.meta

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.ghstudios.android.*
import com.ghstudios.android.data.DataManager
import com.ghstudios.android.mhgendatabase.R

/**
 * The activity hosting the PreferencesFragment, which does the real work.
 * This activity is top level so that you cannot go back to a previous fragment,
 * this allow language settings to be changed without restart.
 */
class PreferencesActivity : GenericActivity() {
    override fun getSelectedSection() = MenuSection.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setAsTopLevel()
    }

    override fun createFragment(): androidx.fragment.app.Fragment {
        return PreferencesFragment()
    }
}

class PreferencesFragment : PreferenceFragmentCompat() {
    private val defaultLabel get() = getString(R.string.preference_language_default)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppSettings.SETTINGS_FILE_NAME

        setPreferencesFromResource(R.xml.preferences, rootKey)
        //initAppLanguages()
        initDataLanguages()
    }


    /**
     * Initialize app languages preference.
     * Currently unused, as its difficult to do and keeps changing every android version,
     * with potential bugs such as activity titles not changing.
     */
    private fun initAppLanguages() {
        val localePref = findPreference<ListPreference>(AppSettings.PROP_APP_LOCALE)
        localePref ?: return

        // not supported on older android versions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            localePref.isEnabled = false
            localePref.summary = getString(R.string.preference_app_language_description_disabled)
        } else {
            val languageCodes = listOf("") + appLanguages
            val languageNames = languageCodes.map { allLanguages.getOrElse(it) { defaultLabel } }

            localePref.entryValues = languageCodes.toTypedArray()
            localePref.entries = languageNames.toTypedArray()
            localePref.value = AppSettings.trueDataLocale // ensure a value is set
        }
    }

    private fun initDataLanguages() {
        val localePref = findPreference<ListPreference>(AppSettings.PROP_DATA_LOCALE)
        localePref ?: return

        val languageCodes = listOf("") + DataManager.get().getLanguages()
        val languageNames = languageCodes.map { allLanguages.getOrElse(it) { defaultLabel } }

        localePref.entryValues = languageCodes.toTypedArray()
        localePref.entries = languageNames.toTypedArray()
        localePref.value = AppSettings.trueDataLocale // ensure a value is set
    }
}