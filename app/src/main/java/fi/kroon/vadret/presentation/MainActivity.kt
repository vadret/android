package fi.kroon.vadret.presentation

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import fi.kroon.vadret.R
import fi.kroon.vadret.VadretApplication
import fi.kroon.vadret.data.DEFAULT_PREFERENCES
import fi.kroon.vadret.data.nominatim.model.StatusBar
import fi.kroon.vadret.databinding.ActivityMainBinding
import fi.kroon.vadret.utils.extensions.splitBySpaceTakeFirst
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toVisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VadretApplication[applicationContext].cmp.inject(application)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        setSupportActionBar(binding.toolBar)

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        PreferenceManager.setDefaultValues(this, DEFAULT_PREFERENCES, MODE_PRIVATE, R.xml.preferences, false)

        binding.navigationView.setupWithNavController(navController)
        hideAppName()
    }

    fun hideAppName() {
        val systemScale: Float = resources.displayMetrics.density
        val fontScale: Float = resources.configuration.fontScale
        Timber.d("System scale: $systemScale")
        Timber.d("Font scale: $fontScale")

        if (fontScale > 1.0 || systemScale > 2.625) {
            toolBar.title = ""
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_host_fragment),
            drawerLayout
        )
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun hideActionBar() = currentLocationName.toGone()
    fun showActionBar() = currentLocationName.toVisible()

    fun renderStatusBarLocally(statusBar: StatusBar) {
        statusBar.let { status ->
            val banner = "${status.city}, ${status.state.splitBySpaceTakeFirst()} ${resources.getString(R.string.county)}"
            Timber.d("Updating actionbar: $banner")
            currentLocationName.text = banner
            showActionBar()
        }
    }

    fun renderActionBarNominatim(banner: String) {
        currentLocationName.text = banner
        showActionBar()
    }
}