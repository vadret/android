package fi.kroon.vadret.presentation.aboutapp

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutFragment
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryFragment
import fi.kroon.vadret.presentation.aboutapp.setting.AboutAppSettingFragment
import javax.inject.Inject

class AboutAppFragmentPagerAdapter @Inject constructor(
    fragmentManager: FragmentManager,
    private val context: Context
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> AboutAppSettingFragment.newInstance()
            1 -> AboutAppLibraryFragment.newInstance()
            2 -> AboutAppAboutFragment.newInstance()
            else -> throw Exception()
        }

    override fun getPageTitle(position: Int): CharSequence =
        when (position) {
            0 -> context.resources.getString(R.string.settings)
            1 -> context.resources.getString(R.string.about_app_libraries_tab_title)
            2 -> context.resources.getString(R.string.about_app_about_title)
            else -> throw Exception()
        }
}