package fi.kroon.vadret.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.databinding.MainActivityBinding
import fi.kroon.vadret.main.di.DaggerMainActivityComponent
import fi.kroon.vadret.main.di.MainActivityComponent
import fi.kroon.vadret.shared.ILocalityActionBar
import fi.kroon.vadret.util.DEFAULT_SETTINGS
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toVisible
import io.github.sphrak.either.extension.onResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MainActivity : AppCompatActivity(), ILocalityActionBar {

    private lateinit var binding: MainActivityBinding

    private val component: MainActivityComponent by lazyAndroid {
        DaggerMainActivityComponent
            .factory()
            .create(context = this, coreComponent = coreComponent)
    }

    private var viewStateJob: Job? = null

    private val viewModel: MainActivityViewModel by lazyAndroid {
        component.provideMainActivityViewModel()
    }

    /**
     *  Currently the [MainActivity] is a bit cluttered
     *  because Android Architecture Navigation component
     *  lacks native support for multistack navigation.
     *  Google is currently working on a solution and
     *  the issue is being tracked here: https://issuetracker.google.com/issues/80029773#comment25
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ON CREATE")

        /**
         * on runtime theme needs to be applied before
         * setContentView has run.
         */
        applyTheme()

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSupportActionBar()

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        setupPreferences()
    }

    private fun applyTheme() = viewModel
        .getThemeMode()
        .onResult(
            { failure: Failure ->
                Timber.e("Error: $failure")
            },
            { theme: Theme ->
                setTheme(theme.resourceId)
            }
        )

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        //
        // This is needed in order to apply themes from settings.

        setupBottomNavigationBar()
    }

    /**
     *  Theme changes are only observed while started, a change
     *  made in the meantime is emitted again on the next start.
     */
    override fun onStart() {
        super.onStart()
        viewStateJob = viewModel
            .viewState
            .onEach(::render)
            .launchIn(lifecycleScope)
    }

    override fun onStop() {
        super.onStop()
        viewStateJob?.cancel()
        viewStateJob = null
    }

    private fun setupBottomNavigationBar() {
        Timber.d("setupBottomNavigationBar")

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(navController)
    }

    private fun setupSupportActionBar() {
        setSupportActionBar(binding.toolBar)
    }

    private fun setupPreferences() {
        PreferenceManager
            .setDefaultValues(
                this,
                DEFAULT_SETTINGS,
                MODE_PRIVATE,
                fi.kroon.vadret.core.R.xml.about_app_preferences,
                false
            )
    }

    private fun render(viewState: MainActivityView.State) {
        when (viewState.renderEvent) {
            MainActivityView.RenderEvent.None -> Unit
            MainActivityView.RenderEvent.RestartActivity -> restartActivity()
            is MainActivityView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
        }
    }

    private fun restartActivity() {
        Timber.d("Restarting activity")
        recreate()
    }

    private fun renderError(errorCode: Int) {
        Timber.e("Rendering error code: ${getString(errorCode)}")
    }

    override fun hideLocalityActionBar() = binding.currentLocationName.toGone()

    override fun setLocalityActionBar(locality: Locality) {
        locality.name?.let {
            binding.currentLocationName.text = locality.name
        } ?: binding.currentLocationName.setText(fi.kroon.vadret.core.R.string.unknown_area)
        binding.currentLocationName.toVisible()
    }
}