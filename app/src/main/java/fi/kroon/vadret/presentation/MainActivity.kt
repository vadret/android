package fi.kroon.vadret.presentation

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.utils.DEFAULT_PREFERENCES
import fi.kroon.vadret.utils.extensions.appComponent
import fi.kroon.vadret.utils.extensions.setupWithNavController
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toVisible
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var navController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent()
            .inject(this)

        setContentView(R.layout.activity_main)
        setupSupportActionBar()

        if (savedInstanceState == null) {
            Timber.d("savedInstanceState was null")
            setupBottomNavigationBar()
        }

        PreferenceManager.setDefaultValues(this, DEFAULT_PREFERENCES, MODE_PRIVATE, R.xml.preferences, false)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        Timber.d("onRestoreInstanceState")
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        Timber.d("setupBottomNavigationBar")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navGraphIds: List<Int> = listOf(R.navigation.weather, R.navigation.alert)
        // Setup the bottom navigation view with a list of navigation graphs
        val controller: LiveData<NavController> = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(
            this,
            Observer { navController ->
                setupActionBarWithNavController(navController)
            }
        )
        navController = controller
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.value?.navigateUp() ?: false
    }

    /**
     * Overriding popBackStack is necessary in this case if the app is started from the deep link.
     */
    override fun onBackPressed() {
        if (navController?.value?.popBackStack() != true) {
            super.onBackPressed()
        }
    }

    private fun setupSupportActionBar() {
        setSupportActionBar(toolBar)
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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)

        return navHostFragment?.childFragmentManager?.fragments?.filterNotNull()?.find {
            it.javaClass.name == className
        }!! as T
    }
}