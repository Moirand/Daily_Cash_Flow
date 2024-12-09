package com.my.dailycashflow.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.my.dailycashflow.R
import com.my.dailycashflow.util.NightMode

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var prefManager: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>(getString(R.string.pref_key_night))?.setOnPreferenceChangeListener { _, newValue ->
            val mode = when (newValue) {
                resources.getStringArray(R.array.night_mode_value)[0] -> NightMode.AUTO.value
                resources.getStringArray(R.array.night_mode_value)[1] -> NightMode.ON.value
                else -> NightMode.OFF.value
            }
            updateTheme(mode)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefManager = PreferenceManager.getDefaultSharedPreferences(requireContext())
        editor = prefManager.edit()
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }

}