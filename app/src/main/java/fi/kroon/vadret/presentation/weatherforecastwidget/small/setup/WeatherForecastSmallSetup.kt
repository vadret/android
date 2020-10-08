package fi.kroon.vadret.presentation.weatherforecastwidget.small.setup

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.itemSelections
import fi.kroon.vadret.R
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.BaseAppWidgetSetup
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.WeatherForecastSmallAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.di.WeatherForecastSmallSetupComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.di.WeatherForecastSmallSetupScope
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toInvisible
import fi.kroon.vadret.util.extension.toObservable
import fi.kroon.vadret.util.extension.toVisible
import fi.kroon.vadret.util.extension.toast
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.weather_forecast_widget_small_setup.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions
@WeatherForecastSmallSetupScope
class WeatherForecastSmallSetup : BaseAppWidgetSetup() {

    private companion object {
        const val STATE_PARCEL_KEY = "WEATHER_FORECAST_APPWIDGET_SETUP_SMALL_KEY"
    }

    private var stateParcel: WeatherForecastSmallSetupView.StateParcel? = null

    private var bundle: Bundle? = null

    @LayoutRes
    private var spinnerItemLayoutId: Int = R.layout.weather_forecast_widget_setup_spinner_item

    private val itemDecoration: DividerItemDecoration by lazy(LazyThreadSafetyMode.NONE) {
        DividerItemDecoration(this, RecyclerView.VERTICAL)
    }

    private val drawable: Drawable? by lazy(LazyThreadSafetyMode.NONE) {
        getDrawable(R.drawable.search_item_divider)
    }

    private val providerIntent: Intent by lazy(LazyThreadSafetyMode.NONE) {
        Intent(this, WeatherForecastSmallAppWidgetProvider::class.java)
    }

    private val component: ComponentName by lazy(LazyThreadSafetyMode.NONE) {
        ComponentName(this, WeatherForecastSmallAppWidgetProvider::class.java)
    }

    private val pendingIntent: PendingIntent by lazy(LazyThreadSafetyMode.NONE) {
        PendingIntent
            .getBroadcast(
                this,
                0,
                providerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
    }

    private val alarmManager: AlarmManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val cmp: WeatherForecastSmallSetupComponent by lazy(LazyThreadSafetyMode.NONE) {
        appComponent()
            .weatherForecastSmallAppWidgetSetupComponentBuilder()
            .build()
    }

    private val viewModel: WeatherForecastSmallSetupViewModel by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideWeatherForecastSmallSetupViewModel()
    }

    private val subscriptions: CompositeDisposable by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideCompositeDisposable()
    }

    private val onSetupInitialisedSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnSetupInitialised> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnSetupInitialised()
    }

    private val onConfigurationConfirmedSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnConfigurationConfirmed> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnConfigurationConfirmed()
    }

    private val onCanceledClickedSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnCanceledClicked> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnCanceledClicked()
    }

    private val onLocalitySearchEnabledSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalitySearchEnabled> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocalitySearchEnabled()
    }

    private val onLocalitySearchDisabledSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalitySearchDisabled> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocalitySearchDisabled()
    }

    private val onAutoCompleteItemClickedSubject: PublishSubject<AutoCompleteItem> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnAutoCompleteItemClicked()
    }

    private val onSearchViewDismissedSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnSearchViewDismissed> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnSearchViewDismissed()
    }

    private val onLocalityTextUpdatedSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalityTextUpdated> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocalityTextUpdated()
    }

    private val onLocationPermissionDeniedSubject: PublishSubject<WeatherForecastSmallSetupView.Event.OnLocationPermissionDenied> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocationPermissionDenied()
    }

    private val autoCompleteAdapter: AutoCompleteAdapter by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideAutoCompleteAdapter()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.apply {
            putParcelable(STATE_PARCEL_KEY, stateParcel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        preSetupEvent()
        setContentView(R.layout.weather_forecast_widget_small_setup)
        restoreSavedInstanceState(savedInstanceState)
        setResultCanceled()
        setup()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun preSetupEvent() = viewModel
        .getThemeMode()
        .map { result: Either<Failure, Theme> ->
            result.either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                },
                { theme: Theme ->
                    setTheme(theme.resourceId)
                }
            )
        }.blockingGet()

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) =
        savedInstanceState?.let { savedBundle: Bundle ->
            if (bundle == null) bundle = savedBundle
        }

    private fun setResultCanceled() {
        Intent().apply {
            putExtra(
                extraAppWidgetId,
                appWidgetId
            ).also { resultIntent: Intent ->
                setResult(RESULT_CANCELED, resultIntent)
            }
        }
    }

    private fun render(state: WeatherForecastSmallSetupView.State) =
        when (state.renderEvent) {
            WeatherForecastSmallSetupView.RenderEvent.None -> Unit
            WeatherForecastSmallSetupView.RenderEvent.FinishActivity -> finishActivity()
            WeatherForecastSmallSetupView.RenderEvent.UpdateSavedInstanceState -> updateSavedInstanceState(state)
            is WeatherForecastSmallSetupView.RenderEvent.ConfirmConfiguration -> confirmConfiguration(state.renderEvent.updateIntervalMillis)
            WeatherForecastSmallSetupView.RenderEvent.EnableLocalitySearch -> enableLocalitySearch()
            WeatherForecastSmallSetupView.RenderEvent.DisableLocalitySearch -> requestLocationPermission()
            is WeatherForecastSmallSetupView.RenderEvent.DisplayAutoComplete -> displayAutoCompleteList(state.renderEvent)
            is WeatherForecastSmallSetupView.RenderEvent.UpdateSelectedLocalityText -> updateSelectedLocalityText(state.renderEvent.locality)
            WeatherForecastSmallSetupView.RenderEvent.ResetLocalitySearch -> resetLocalitySearch(state.searchText)
            is WeatherForecastSmallSetupView.RenderEvent.DisplayError -> renderError(state.renderEvent.errorCode)
            WeatherForecastSmallSetupView.RenderEvent.TurnOffPhonePositionSwitch -> turnOffPhonePositionSwitch()
        }

    private fun setup() {
        setupAdapters()
        setupEvents()
    }

    private fun turnOffPhonePositionSwitch() {
        widgetSmallSetupLocationModeKey.isChecked = false
    }

    private fun resetLocalitySearch(searchText: String) {
        Timber.d("RESET LOCALITY SEARCH: $searchText")
        widgetSmallSetupLocalitySearchView.apply {
            setQuery(searchText, true)
        }
    }

    private fun displayAutoCompleteList(renderEvent: WeatherForecastSmallSetupView.RenderEvent.DisplayAutoComplete) {
        autoCompleteAdapter.updateList(renderEvent.newFilteredList)
        widgetSmallSetupAutoCompleteRecyclerView.adapter?.run {
            renderEvent.diffResult?.dispatchUpdatesTo(this)
        }
    }

    private fun updateSelectedLocalityText(locality: String) {
        widgetSmallSetupSelectedLocalityName.text = locality

        onLocalityTextUpdatedSubject.onNext(
            WeatherForecastSmallSetupView
                .Event
                .OnLocalityTextUpdated
        )
    }

    private fun requestLocationPermission(): Unit =
        disableLocalitySearchWithPermissionCheck()

    @OnPermissionDenied(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onPermissionDenied() {
        toast(getString(R.string.permission_denied))
        onLocationPermissionDeniedSubject.onNext(
            WeatherForecastSmallSetupView
                .Event
                .OnLocationPermissionDenied
        )
    }

    @OnNeverAskAgain(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onNeverAskAgain() {
        toast(getString(R.string.permission_denied))
        onLocationPermissionDeniedSubject.onNext(
            WeatherForecastSmallSetupView
                .Event
                .OnLocationPermissionDenied
        )
    }

    @NeedsPermission(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun disableLocalitySearch() {

        widgetSmallSetupSelectedLocalityName.isEnabled = false
        widgetSmallSetupCurrentLocality.isEnabled = false

        widgetSmallSetupLocalitySearchView
            .toGone()

        widgetSmallSetupAutoCompleteRecyclerView.apply {
            adapter = null
            toInvisible()
        }

        onLocalitySearchDisabledSubject
            .onNext(
                WeatherForecastSmallSetupView
                    .Event
                    .OnLocalitySearchDisabled
            )
    }

    private fun enableLocalitySearch() {

        widgetSmallSetupSelectedLocalityName.isEnabled = true
        widgetSmallSetupCurrentLocality.isEnabled = true
        widgetSmallSetupLocalitySearchView.apply {
            toVisible()
            isFocusable = true
            isIconified = false
            requestFocusFromTouch()
        }

        widgetSmallSetupAutoCompleteRecyclerView.apply {
            adapter = autoCompleteAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

            drawable?.let { res: Drawable ->
                itemDecoration
                    .setDrawable(res)
            }

            addItemDecoration(itemDecoration)
            hasFixedSize()
            toVisible()
        }

        onLocalitySearchEnabledSubject
            .onNext(
                WeatherForecastSmallSetupView
                    .Event
                    .OnLocalitySearchEnabled
            )
    }

    private fun setupAdapters() {

        // Themes
        ArrayAdapter.createFromResource(
            this,
            R.array.weather_widget_themes,
            spinnerItemLayoutId
        ).also { themeAdapter: ArrayAdapter<CharSequence> ->
            themeAdapter.setDropDownViewResource(
                R.layout.weather_forecast_dropdown_item
            )

            widgetSmallSetupThemeSpinnerView.apply {
                adapter = themeAdapter
            }
        }

        // Update interval
        ArrayAdapter.createFromResource(
            this,
            R.array.weather_widget_update_intervals,
            android.R.layout.simple_spinner_item
        ).also { updateIntervalAdapter: ArrayAdapter<CharSequence> ->
            updateIntervalAdapter.setDropDownViewResource(
                R.layout.weather_forecast_dropdown_item
            )

            widgetSetupSmallIntervalSpinnerView.apply {
                adapter = updateIntervalAdapter
            }
        }
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {

            widgetSmallSetupLocalitySearchView
                .setOnCloseListener {
                    onSearchViewDismissedSubject.onNext(
                        WeatherForecastSmallSetupView
                            .Event
                            .OnSearchViewDismissed
                    )
                    true
                }

            Observable.mergeArray(
                onSetupInitialisedSubject
                    .toObservable(),
                onSearchViewDismissedSubject
                    .toObservable(),
                onConfigurationConfirmedSubject
                    .toObservable(),
                onCanceledClickedSubject
                    .toObservable(),
                onLocalityTextUpdatedSubject
                    .toObservable(),
                onLocationPermissionDeniedSubject
                    .toObservable(),
                onAutoCompleteItemClickedSubject
                    .toObservable()
                    .map { item: AutoCompleteItem ->
                        WeatherForecastSmallSetupView
                            .Event
                            .OnAutoCompleteItemClicked(item)
                    },
                widgetSetupSmallIntervalSpinnerView
                    .itemSelections()
                    .map { position: Int ->
                        WeatherForecastSmallSetupView
                            .Event
                            .OnUpdateIntervalSelected(position)
                    },
                widgetSmallSetupThemeSpinnerView
                    .itemSelections()
                    .map { position: Int ->
                        WeatherForecastSmallSetupView
                            .Event
                            .OnThemeSelected(position)
                    },
                widgetSmallSetupLocationModeKey
                    .checkedChanges()
                    .map { toggled: Boolean ->
                        WeatherForecastSmallSetupView
                            .Event
                            .OnPhonePositionToggled(toggled)
                    },
                widgetSmallSetupCancelView
                    .clicks()
                    .map {
                        WeatherForecastSmallSetupView
                            .Event
                            .OnCanceledClicked
                    },
                widgetSmallSetupConfirmView
                    .clicks()
                    .map {
                        WeatherForecastSmallSetupView
                            .Event
                            .OnConfigurationConfirmed(
                                appWidgetId
                            )
                    },
                widgetSmallSetupLocalitySearchView
                    .queryTextChangeEvents()
                    .skipInitialValue()
                    .map { searchEvent ->
                        when {
                            searchEvent.isSubmitted -> {
                                WeatherForecastSmallSetupView
                                    .Event
                                    .OnSearchButtonSubmitted(
                                        searchEvent
                                            .queryText
                                            .toString()
                                    )
                            }
                            else -> {
                                WeatherForecastSmallSetupView
                                    .Event
                                    .OnSearchTextChanged(
                                        searchEvent
                                            .queryText
                                            .toString()
                                    )
                            }
                        }
                    }
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

            onSetupInitialisedSubject
                .onNext(
                    WeatherForecastSmallSetupView
                        .Event
                        .OnSetupInitialised(
                            appWidgetId,
                            stateParcel = bundle?.getParcelable(
                                STATE_PARCEL_KEY
                            )
                        )
                )
        }
    }

    private fun updateSavedInstanceState(state: WeatherForecastSmallSetupView.State) {
        stateParcel = WeatherForecastSmallSetupView.StateParcel(
            locality = state.locality,
            theme = state.theme,
            updateInterval = state.updateInterval,
            usePhonesPosition = state.usePhonesPosition,
            autoCompleteItem = state.autoCompleteItem,
            searchText = state.searchText
        )
    }

    private fun confirmConfiguration(updateIntervalMillis: Long) {
        scheduleAlarm(updateIntervalMillis = updateIntervalMillis)
        setResultOk()
        finish()
    }

    private fun setResultOk() {
        providerIntent.apply {
            putExtra(extraAppWidgetId, appWidgetId)
        }.also { resultIntent: Intent ->
            setResult(RESULT_OK, resultIntent)
        }
    }

    private fun scheduleAlarm(updateIntervalMillis: Long) {
        Timber.d("SCHEDULE ALARM")
        Timber.d("SCHEDULE INTERVAL: $updateIntervalMillis")

        val ids: IntArray = appWidgetManager.getAppWidgetIds(component)

        providerIntent.apply {
            action = ACTION_APPWIDGET_UPDATE
            putExtra(EXTRA_APPWIDGET_IDS, ids)
        }

        Timber.d("APPWIDGET IDS: ${ids.size}")
        Timber.d("APPWIDGET ID: $appWidgetId")

        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            initialTriggerAtMillis,
            updateIntervalMillis,
            pendingIntent
        )
    }

    private fun finishActivity() {
        Timber.d("FINISH ACTIVITY")
        toast(getString(R.string.widget_setup_setup_canceled))
        finish()
    }

    override fun renderError(errorCode: Int) {
        toast(getString(errorCode))
    }
}