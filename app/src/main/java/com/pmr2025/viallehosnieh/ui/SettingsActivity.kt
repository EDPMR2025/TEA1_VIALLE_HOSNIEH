package com.pmr2025.viallehosnieh.ui

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceActivity
import com.pmr2025.viallehosnieh.R

class SettingsActivity : PreferenceActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }
}