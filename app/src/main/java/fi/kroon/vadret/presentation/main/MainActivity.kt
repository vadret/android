package fi.kroon.vadret.presentation.main

import android.os.Bundle
<<<<<<< HEAD
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
=======
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
>>>>>>> 4a5e7b8... fixes #209 -- replace rx2 with coroutines in weather forecast
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.presentation.main.di.MainActivityComponent
import fi.kroon.vadret.presentation.shared.BaseActivity
import fi.kroon.vadret.util.DEFAULT_SETTINGS
import fi.kroon.vadret.util.Scheduler
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val cmp: MainActivityComponent by lazy(LazyThreadSafetyMode.NONE) {
        appComponent()
            .mainActivityComponentBuilder()
            .build()
    }

    private val subscriptions: CompositeDisposable by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideCompositeDisposable()
    }

    private val scheduler: Scheduler by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideScheduler()
    }

    private val viewModel: MainActivityViewModel by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideMainActivityViewModel()
    }

    private val onViewInitialisedSubject: PublishSubject<MainActivityView.Event.OnViewInitialised> by lazy(LazyThreadSafetyMode.NONE) {
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

    override fun onStop() {
        super.onStop()
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

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setupWithNavController(navController)
    }

    private fun setupSupportActionBar() {
        setSupportActionBar(toolBar)
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