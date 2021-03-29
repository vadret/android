package fi.kroon.vadret.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.databinding.MainActivityBinding
import fi.kroon.vadret.presentation.main.di.DaggerMainActivityComponent
import fi.kroon.vadret.presentation.main.di.MainActivityComponent
import fi.kroon.vadret.util.DEFAULT_SETTINGS
import fi.kroon.vadret.util.Scheduler
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toObservable
import fi.kroon.vadret.util.extension.toVisible
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    private val component: MainActivityComponent by lazyAndroid {
        DaggerMainActivityComponent
            .factory()
            .create(context = this, coreComponent = coreComponent)
    }

    private val subscriptions: CompositeDisposable by lazyAndroid {
        component.provideCompositeDisposable()
    }

    private val scheduler: Scheduler by lazyAndroid {
        component.provideScheduler()
    }

    private val viewModel: MainActivityViewModel by lazyAndroid {
        component.provideMainActivityViewModel()
    }

    private val onViewInitialisedSubject: PublishSubject<MainActivityView.Event.OnViewInitialised> by lazyAndroid {
        component.provideOnViewInitialised()
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
         * setContentView has run. But we cant run
         * setupEvents() since they open up UI
         * which wont exist at this point.
         */
        preSetupEvents()

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSupportActionBar()

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        setupPreferences()
        setupEvents()
    }

    /**
     *  Needs to run before to allow
     *  asynchronous comm with
     *  doman layer before
     *  contentview is setup.
     */
    private fun preSetupEvents() = viewModel
        .getThemeMode()
        .map { result ->
            result.either(
                { failure: Failure ->
                    Timber.e("Error: $failure")
                },
                { theme: Theme ->
                    setTheme(theme.resourceId)
                }
            )
        }.blockingGet()

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        //
        // This is needed in order to apply themes from settings.

        setupBottomNavigationBar()
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
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
                R.xml.about_app_preferences,
                false
            )
    }

    private fun setupEvents() {
        Timber.d("MAIN ACTIVITY -- SETUP EVENTS")
        if (subscriptions.size() == 0) {
            Observable.mergeArray(
                onViewInitialisedSubject
                    .toObservable()
            ).observeOn(
                scheduler.io()
            ).compose(
                viewModel()
            ).observeOn(
                scheduler.ui()
            ).subscribe(
                ::render
            ).addTo(
                subscriptions
            )

            onViewInitialisedSubject
                .onNext(
                    MainActivityView
                        .Event
                        .OnViewInitialised
                )
        }
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

    fun hideLocalityActionBar() = binding.currentLocationName.toGone()

    fun setLocalityActionBar(locality: Locality) {
        locality.name?.let {
            binding.currentLocationName.text = locality.name
        } ?: binding.currentLocationName.setText(R.string.unknown_area)
        binding.currentLocationName.toVisible()
    }
}