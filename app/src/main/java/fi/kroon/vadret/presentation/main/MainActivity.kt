package fi.kroon.vadret.presentation.main

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.presentation.main.di.MainActivityComponent
import fi.kroon.vadret.presentation.main.di.MainActivityScope
import fi.kroon.vadret.presentation.shared.BaseActivity
import fi.kroon.vadret.util.DEFAULT_SETTINGS
import fi.kroon.vadret.util.Scheduler
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.setupWithNavController
import fi.kroon.vadret.util.extension.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

@MainActivityScope
class MainActivity : BaseActivity() {

    private var navController: LiveData<NavController>? = null

    private val cmp: MainActivityComponent by lazy {
        appComponent()
            .mainActivityComponentBuilder()
            .build()
    }

    private val subscriptions: CompositeDisposable by lazy {
        cmp.provideCompositeDisposable()
    }

    private val scheduler: Scheduler by lazy {
        cmp.provideScheduler()
    }

    private val viewModel: MainActivityViewModel by lazy {
        cmp.provideMainActivityViewModel()
    }

    private val onViewInitialisedSubject: PublishSubject<MainActivityView.Event.OnViewInitialised> by lazy {
        cmp.provideOnViewInitialised()
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

        cmp.inject(this)

        /**
         * on runtime theme needs to be applied before
         * setContentView has run. But we cant run
         * setupEvents() since they open up UI
         * which wont exist at this point.
         */
        preSetupEvents()

        setContentView(R.layout.activity_main)
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

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        Timber.d("ON RESTORE INSTANCE STATE")
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        Timber.d("setupBottomNavigationBar")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navGraphIds: List<Int> = listOf(
            R.navigation.weather,
            R.navigation.alert,
            R.navigation.radar,
            R.navigation.settings
        )
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

    inline fun <reified T : Fragment> getFragmentByClassName(className: String): T {
        val navHostFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_container)
        return navHostFragment?.childFragmentManager?.fragments?.filterNotNull()?.find {
            it.javaClass.name == className
        }!! as T
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

    override fun renderError(errorCode: Int) {
        Timber.e("Rendering error code: ${getString(errorCode)}")
    }
}