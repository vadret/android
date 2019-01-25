package fi.kroon.vadret.presentation

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import fi.kroon.vadret.R
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.databinding.ActivityMainBinding
import fi.kroon.vadret.utils.DEFAULT_PREFERENCES
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toVisible
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        setSupportActionBar(binding.toolBar)

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        PreferenceManager.setDefaultValues(this, DEFAULT_PREFERENCES, MODE_PRIVATE, R.xml.preferences, false)

        binding.navigationView.setupWithNavController(navController)
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

    fun disableLocalityActionBar() = currentLocationName.toGone()
    fun displayLocalityActionBar(locality: Locality) {
        locality.name?.let {
            currentLocationName.text = locality.name
        } ?: currentLocationName.setText(R.string.unknown_area)
        currentLocationName.toVisible()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Fragment> getFragmentByClassName(className: String): T {

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

        return navHostFragment?.childFragmentManager?.fragments?.filterNotNull()?.find {
            it.javaClass.name == className
        }!! as T
    }
}