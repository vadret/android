package fi.kroon.vadret.presentation.aboutapp

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutFragment
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryFragment
import timber.log.Timber
import javax.inject.Inject

class AboutAppFragmentPagerAdapter @Inject constructor(
    fragmentManager: FragmentManager,
    private val context: Context
) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> AboutAppAboutFragment.newInstance()
            1 -> AboutAppLibraryFragment.newInstance()
            else -> throw Exception()
        }

    override fun getPageTitle(position: Int): CharSequence =
        when (position) {
            0 -> context.resources.getString(R.string.about_app_about_title)
            1 -> context.resources.getString(R.string.about_app_libraries_tab_title)
            else -> throw Exception()
        }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)

        Timber.d("$`object`")
    }
}