package com.ghstudios.android.features.preferences

import android.os.Bundle
import android.support.v4.app.Fragment
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

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppSettings.SETTINGS_FILE_NAME

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}