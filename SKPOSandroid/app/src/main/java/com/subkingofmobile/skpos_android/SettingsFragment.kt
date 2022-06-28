package com.subkingofmobile.skpos_android

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment() : PreferenceFragmentCompat() {
    private lateinit var settingsManager : SettingsManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        this.settingsManager = SettingsManager(requireActivity().applicationContext)
    }
}