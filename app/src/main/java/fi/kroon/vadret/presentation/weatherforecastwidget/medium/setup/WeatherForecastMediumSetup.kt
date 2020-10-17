package fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
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
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.WeatherForecastMediumAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di.WeatherForecastMediumSetupComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di.WeatherForecastMediumSetupScope
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.AutoCompleteAdapterLegacy
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.BaseAppWidgetSetup
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
import kotlinx.android.synthetic.main.weather_forecast_widget_medium_setup.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions
@WeatherForecastMediumSetupScope
class WeatherForecastMediumSetup : BaseAppWidgetSetup() {

    private companion object {
        const val STATE_PARCEL_KEY = "WEATHER_FORECAST_APPWIDGET_SETUP_MEDIUM_KEY"
    }

    private var stateParcel: WeatherForecastMediumSetupView.StateParcel? = null

    private var bundle: Bundle? = null

    @LayoutRes
    private var spinnerItemLayoutId: Int = R.layout.weather_forecast_widget_setup_spinner_item

    private val itemDecoration: DividerItemDecoration by lazy(LazyThreadSafetyMode.NONE) {
        DividerItemDecoration(this, RecyclerView.VERTICAL)
    }

    private val drawable: Drawable? by lazy(LazyThreadSafetyMode.NONE) {
        ContextCompat.getDrawable(this, R.drawable.search_item_divider)
    }

    private val providerIntent: Intent by lazy(LazyThreadSafetyMode.NONE) {
        Intent(this, WeatherForecastMediumAppWidgetProvider::class.java)
    }

    private val component: ComponentName by lazy(LazyThreadSafetyMode.NONE) {
        ComponentName(this, WeatherForecastMediumAppWidgetProvider::class.java)
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

    private val cmp: WeatherForecastMediumSetupComponent by lazy(LazyThreadSafetyMode.NONE) {
        appComponent()
            .weatherForecastMediumAppWidgetSetupComponentBuilder()
            .build()
    }

    private val viewModel: WeatherForecastMediumSetupViewModel by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideWeatherForecastMediumSetupViewModel()
    }

    private val subscriptions: CompositeDisposable by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideCompositeDisposable()
    }

    private val onSetupInitialisedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnSetupInitialised> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnSetupInitialised()
    }

    private val onConfigurationConfirmedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnConfigurationConfirmed> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnConfigurationConfirmed()
    }

    private val onCanceledClickedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnCanceledClicked> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnCanceledClicked()
    }

    private val onLocalitySearchEnabledSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchEnabled> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocalitySearchEnabled()
    }

    private val onLocalitySearchDisabledSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchDisabled> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocalitySearchDisabled()
    }

    private val onAutoCompleteItemClickedSubject: PublishSubject<AutoCompleteItem> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnAutoCompleteItemClickedSubject()
    }

    private val onSearchViewDismissedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnSearchViewDismissed> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnSearchViewDismissed()
    }

    private val onLocalityTextUpdatedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalityTextUpdated> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocalityTextUpdated()
    }

    private val onLocationPermissionDeniedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocationPermissionDenied> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnLocationPermissionDenied()
    }

    private val autoCompleteAdapter: AutoCompleteAdapterLegacy by lazy(LazyThreadSafetyMode.NONE) {
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
        setContentView(R.layout.weather_forecast_widget_medium_setup)
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

    private fun render(state: WeatherForecastMediumSetupView.State) =
        when (state.renderEvent) {
            WeatherForecastMediumSetupView.RenderEvent.None -> Unit
            WeatherForecastMediumSetupView.RenderEvent.FinishActivity -> finishActivity()
            WeatherForecastMediumSetupView.RenderEvent.UpdateSavedInstanceState -> updateSavedInstanceState(state)
            is WeatherForecastMediumSetupView.RenderEvent.ConfirmConfiguration -> confirmConfiguration(state.renderEvent.updateIntervalMillis)
            WeatherForecastMediumSetupView.RenderEvent.EnableLocalitySearch -> enableLocalitySearch()
            WeatherForecastMediumSetupView.RenderEvent.DisableLocalitySearch -> requestLocationPermission()
            is WeatherForecastMediumSetupView.RenderEvent.DisplayAutoComplete -> displayAutoCompleteList(state.renderEvent)
            is WeatherForecastMediumSetupView.RenderEvent.UpdateSelectedLocalityText -> updateSelectedLocalityText(state.renderEvent.locality)
            WeatherForecastMediumSetupView.RenderEvent.ResetLocalitySearch -> resetLocalitySearch(state.searchText)
            is WeatherForecastMediumSetupView.RenderEvent.DisplayError -> renderError(state.renderEvent.errorCode)
            WeatherForecastMediumSetupView.RenderEvent.TurnOffPhonePositionSwitch -> turnOffPhonePositionSwitch()
        }

    private fun setup() {
        setupAdapters()
        setupEvents()
    }

    private fun turnOffPhonePositionSwitch() {
        widgetMediumSetupLocationModeKey.isChecked = false
    }

    private fun resetLocalitySearch(searchText: String) {
        Timber.d("RESET LOCALITY SEARCH: $searchText")
        widgetMediumSetupLocalitySearchView.apply {
            setQuery(searchText, true)
        }
    }

    private fun displayAutoCompleteList(renderEvent: WeatherForecastMediumSetupView.RenderEvent.DisplayAutoComplete) {
        autoCompleteAdapter.updateList(renderEvent.newFilteredList)
        widgetMediumSetupAutoCompleteRecyclerView.adapter?.run {
            renderEvent.diffResult?.dispatchUpdatesTo(this)
        }
    }

    private fun updateSelectedLocalityText(locality: String) {
        widgetMediumSetupSelectedLocalityName.text = locality

        onLocalityTextUpdatedSubject.onNext(
            WeatherForecastMediumSetupView
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
            WeatherForecastMediumSetupView
                .Event
                .OnLocationPermissionDenied
        )
    }

    @OnNeverAskAgain(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onNeverAskAgain() {
        toast(getString(R.string.permission_denied))
        onLocationPermissionDeniedSubject.onNext(
            WeatherForecastMediumSetupView
                .Event
                .OnLocationPermissionDenied
        )
    }

    @NeedsPermission(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun disableLocalitySearch() {

        widgetMediumSetupSelectedLocalityName.isEnabled = false
        widgetMediumSetupCurrentLocality.isEnabled = false

        widgetMediumSetupLocalitySearchView
            .toGone()

        widgetMediumSetupAutoCompleteRecyclerView.apply {
            adapter = null
            toInvisible()
        }

        onLocalitySearchDisabledSubject
            .onNext(
                WeatherForecastMediumSetupView
                    .Event
                    .OnLocalitySearchDisabled
            )
    }

    private fun enableLocalitySearch() {

        widgetMediumSetupSelectedLocalityName.isEnabled = true
        widgetMediumSetupCurrentLocality.isEnabled = true
        widgetMediumSetupLocalitySearchView.apply {
            toVisible()
            isFocusable = true
            isIconified = false
            requestFocusFromTouch()
        }

        widgetMediumSetupAutoCompleteRecyclerView.apply {
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
                WeatherForecastMediumSetupView
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

            widgetMediumSetupThemeSpinnerView.apply {
                adapter = themeAdapter
            }
        }

        // Forecast format
        ArrayAdapter.createFromResource(
            this,
            R.array.weather_widget_forecast_format,
            spinnerItemLayoutId
        ).also { forecastAdapter: ArrayAdapter<CharSequence> ->
            forecastAdapter.setDropDownViewResource(
                R.layout.weather_forecast_dropdown_item
            )

            widgetMediumSetupForecastFormatSpinnerView.apply {
                adapter = forecastAdapter
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

            widgetSetupMediumIntervalSpinnerView.apply {
                adapter = updateIntervalAdapter
            }
        }
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {

            widgetMediumSetupLocalitySearchView
                .setOnCloseListener {
                    onSearchViewDismissedSubject.onNext(
                        WeatherForecastMediumSetupView
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
                        WeatherForecastMediumSetupView
                            .Event
                            .OnAutoCompleteItemClicked(item)
                    },
                widgetSetupMediumIntervalSpinnerView
                    .itemSelections()
                    .map { position: Int ->
                        WeatherForecastMediumSetupView
                            .Event
                            .OnUpdateIntervalSelected(position)
                    },
                widgetMediumSetupThemeSpinnerView
                    .itemSelections()
                    .map { position: Int ->
                        WeatherForecastMediumSetupView
                            .Event
                            .OnThemeSelected(position)
                    },
                widgetMediumSetupLocationModeKey
                    .checkedChanges()
                    .map { toggled: Boolean ->
                        WeatherForecastMediumSetupView
                            .Event
                            .OnPhonePositionToggled(toggled)
                    },
                widgetMediumSetupForecastFormatSpinnerView
                    .itemSelections()
                    .map { position: Int ->
                        WeatherForecastMediumSetupView
                            .Event
                            .OnForecastFormatSelected(position)
                    },
                widgetMediumSetupCancelView
                    .clicks()
                    .map {
                        WeatherForecastMediumSetupView
                            .Event
                            .OnCanceledClicked
                    },
                widgetMediumSetupConfirmView
                    .clicks()
                    .map {
                        WeatherForecastMediumSetupView
                            .Event
                            .OnConfigurationConfirmed(
                                appWidgetId
                            )
                    },
                widgetMediumSetupLocalitySearchView
                    .queryTextChangeEvents()
                    .skipInitialValue()
                    .map { searchEvent ->
                        when {
                            searchEvent.isSubmitted -> {
                                WeatherForecastMediumSetupView
                                    .Event
                                    .OnSearchButtonSubmitted(
                                        searchEvent
                                            .queryText
                                            .toString()
                                    )
                            }
                            else -> {
                                WeatherForecastMediumSetupView
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
                    WeatherForecastMediumSetupView
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

    private fun updateSavedInstanceState(state: WeatherForecastMediumSetupView.State) {
        stateParcel = WeatherForecastMediumSetupView.StateParcel(
            locality = state.locality,
            theme = state.theme,
            updateInterval = state.updateInterval,
            usePhonesPosition = state.usePhonesPosition,
            forecastFormat = state.forecastFormat,
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
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
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