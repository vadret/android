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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.itemSelections
import fi.kroon.vadret.R
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.WeatherForecastMediumAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di.WeatherForecastMediumSetupComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di.WeatherForecastMediumSetupScope
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.BaseAppWidgetSetup
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toInvisible
import fi.kroon.vadret.util.extension.toObservable
import fi.kroon.vadret.util.extension.toVisible
import fi.kroon.vadret.util.extension.toast
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
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

    @Inject
    lateinit var viewModel: WeatherForecastMediumSetupViewModel

    @Inject
    lateinit var subscriptions: CompositeDisposable

    @Inject
    lateinit var onSetupInitialisedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnSetupInitialised>

    @Inject
    lateinit var onConfigurationConfirmedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnConfigurationConfirmed>

    @Inject
    lateinit var onCanceledClickedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnCanceledClicked>

    @Inject
    lateinit var onLocalitySearchEnabledSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchEnabled>

    @Inject
    lateinit var onLocalitySearchDisabledSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchDisabled>

    @Inject
    lateinit var onAutoCompleteItemClickedSubject: PublishSubject<AutoCompleteItem>

    @Inject
    lateinit var onSearchViewDismissedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnSearchViewDismissed>

    @Inject
    lateinit var onLocalityTextUpdatedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalityTextUpdated>

    @Inject
    lateinit var onLocationPermissionDeniedSubject: PublishSubject<WeatherForecastMediumSetupView.Event.OnLocationPermissionDenied>

    @Inject
    lateinit var autoCompleteAdapter: AutoCompleteAdapter

    private var stateParcel: WeatherForecastMediumSetupView.StateParcel? = null

    private var bundle: Bundle? = null

    @LayoutRes
    private var spinnerItemLayoutId: Int = R.layout.weather_forecast_widget_setup_spinner_item

    private val itemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(this, RecyclerView.VERTICAL)
    }

    private val drawable: Drawable? by lazy {
        getDrawable(R.drawable.search_item_divider)
    }

    private val providerIntent: Intent by lazy {
        Intent(this, WeatherForecastMediumAppWidgetProvider::class.java)
    }

    private val component: ComponentName by lazy {
        ComponentName(this, WeatherForecastMediumAppWidgetProvider::class.java)
    }

    private val pendingIntent: PendingIntent by lazy {
        PendingIntent
            .getBroadcast(
                this,
                0,
                providerIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
    }

    private val alarmManager: AlarmManager by lazy {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val cmp: WeatherForecastMediumSetupComponent by lazy {
        appComponent()
            .weatherForecastMediumAppWidgetSetupComponentBuilder()
            .build()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.apply {
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