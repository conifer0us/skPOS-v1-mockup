package com.subkingofmobile.skpos_android

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment(appContext : Context) : PreferenceFragmentCompat() {
    private val settingsManager : SettingsManager

    init {
        settingsManager = SettingsManager(appContext)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}