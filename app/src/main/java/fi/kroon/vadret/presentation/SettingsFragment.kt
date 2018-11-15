package fi.kroon.vadret.presentation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import fi.kroon.vadret.R
import fi.kroon.vadret.BaseApplication
import fi.kroon.vadret.data.DEFAULT_PREFERENCES
import fi.kroon.vadret.di.component.VadretApplicationComponent
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    val cmp: VadretApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as BaseApplication).cmp
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        cmp.inject(this)
        getPreferenceManager().setSharedPreferencesName(DEFAULT_PREFERENCES)
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE)
        setPreferencesFromResource(R.xml.preferences, rootKey)
        loadSettings()
    }

    private fun loadSettings() {
        findPreference(getString(R.string.city_key)).setSummary(sharedPreferences.getString(getString(R.string.city_key), "None"))
        findPreference(getString(R.string.province_key)).setSummary(sharedPreferences.getString(getString(R.string.province_key), "None"))
        findPreference(getString(R.string.latitude_key)).setSummary(sharedPreferences.getString(getString(R.string.latitude_key), "None"))
        findPreference(getString(R.string.longitude_key)).setSummary(sharedPreferences.getString(getString(R.string.longitude_key), "None"))
    }
}