package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup

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
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.WeatherForecastTinyAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.di.WeatherForecastTinySetupComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.di.WeatherForecastTinySetupScope
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
import kotlinx.android.synthetic.main.weather_forecast_widget_tiny_setup.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions
@WeatherForecastTinySetupScope
class WeatherForecastTinySetup : BaseAppWidgetSetup() {

    private companion object {
        const val STATE_PARCEL_KEY = "WEATHER_FORECAST_APPWIDGET_SETUP_TINY_KEY"
    }

    private var stateParcel: WeatherForecastTinySetupView.StateParcel? = null

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
        Intent(this, WeatherForecastTinyAppWidgetProvider::class.java)
    }

    private val component: ComponentName by lazy {
        ComponentName(this, WeatherForecastTinyAppWidgetProvider::class.java)
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

    private val cmp: WeatherForecastTinySetupComponent by lazy {
        appComponent()
            .weatherForecastTinyAppWidgetSetupComponentBuilder()
            .build()
    }

    private val viewModel: WeatherForecastTinySetupViewModel by lazy {
        cmp.provideWeatherForecastTinySetupViewModel()
    }

    private val subscriptions: CompositeDisposable by lazy {
        cmp.provideCompositeDisposable()
    }

    private val onSetupInitialisedSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnSetupInitialised> by lazy {
        cmp.provideOnSetupInitialised()
    }

    private val onConfigurationConfirmedSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnConfigurationConfirmed> by lazy {
        cmp.provideOnConfigurationConfirmed()
    }

    private val onCanceledClickedSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnCanceledClicked> by lazy {
        cmp.provideOnCanceledClicked()
    }

    private val onLocalitySearchEnabledSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnLocalitySearchEnabled> by lazy {
        cmp.provideOnLocalitySearchEnabled()
    }

    private val onLocalitySearchDisabledSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnLocalitySearchDisabled> by lazy {
        cmp.provideOnLocalitySearchDisabled()
    }

    private val onAutoCompleteItemClickedSubject: PublishSubject<AutoCompleteItem> by lazy {
        cmp.provideOnAutoCompleteItemClicked()
    }

    private val onSearchViewDismissedSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnSearchViewDismissed> by lazy {
        cmp.provideOnSearchViewDismissed()
    }

    private val onLocalityTextUpdatedSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnLocalityTextUpdated> by lazy {
        cmp.provideOnLocalityTextUpdated()
    }

    private val onLocationPermissionDeniedSubject: PublishSubject<WeatherForecastTinySetupView.Event.OnLocationPermissionDenied> by lazy {
        cmp.provideOnLocationPermissionDenied()
    }

    private val autoCompleteAdapter: AutoCompleteAdapter by lazy {
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
        setContentView(R.layout.weather_forecast_widget_tiny_setup)
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

    private fun render(state: WeatherForecastTinySetupView.State) =
        when (state.renderEvent) {
            WeatherForecastTinySetupView.RenderEvent.None -> Unit
            WeatherForecastTinySetupView.RenderEvent.FinishActivity -> finishActivity()
            WeatherForecastTinySetupView.RenderEvent.UpdateSavedInstanceState -> updateSavedInstanceState(state)
            is WeatherForecastTinySetupView.RenderEvent.ConfirmConfiguration -> confirmConfiguration(state.renderEvent.updateIntervalMillis)
            WeatherForecastTinySetupView.RenderEvent.EnableLocalitySearch -> enableLocalitySearch()
            WeatherForecastTinySetupView.RenderEvent.DisableLocalitySearch -> requestLocationPermission()
            is WeatherForecastTinySetupView.RenderEvent.DisplayAutoComplete -> displayAutoCompleteList(state.renderEvent)
            is WeatherForecastTinySetupView.RenderEvent.UpdateSelectedLocalityText -> updateSelectedLocalityText(state.renderEvent.locality)
            WeatherForecastTinySetupView.RenderEvent.ResetLocalitySearch -> resetLocalitySearch(state.searchText)
            is WeatherForecastTinySetupView.RenderEvent.DisplayError -> renderError(state.renderEvent.errorCode)
            WeatherForecastTinySetupView.RenderEvent.TurnOffPhonePositionSwitch -> turnOffPhonePositionSwitch()
        }

    private fun setup() {
        setupAdapters()
        setupEvents()
    }

    private fun turnOffPhonePositionSwitch() {
        widgetTinySetupLocationModeKey.isChecked = false
    }

    private fun resetLocalitySearch(searchText: String) {
        Timber.d("RESET LOCALITY SEARCH: $searchText")
        widgetTinySetupLocalitySearchView.apply {
            setQuery(searchText, true)
        }
    }

    private fun displayAutoCompleteList(renderEvent: WeatherForecastTinySetupView.RenderEvent.DisplayAutoComplete) {
        autoCompleteAdapter.updateList(renderEvent.newFilteredList)
        widgetTinySetupAutoCompleteRecyclerView.adapter?.run {
            renderEvent.diffResult?.dispatchUpdatesTo(this)
        }
    }

    private fun updateSelectedLocalityText(locality: String) {
        widgetTinySetupSelectedLocalityName.text = locality

        onLocalityTextUpdatedSubject.onNext(
            WeatherForecastTinySetupView
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
            WeatherForecastTinySetupView
                .Event
                .OnLocationPermissionDenied
        )
    }

    @OnNeverAskAgain(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onNeverAskAgain() {
        toast(getString(R.string.permission_denied))
        onLocationPermissionDeniedSubject.onNext(
            WeatherForecastTinySetupView
                .Event
                .OnLocationPermissionDenied
        )
    }

    @NeedsPermission(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun disableLocalitySearch() {

        widgetTinySetupSelectedLocalityName.isEnabled = false
        widgetTinySetupCurrentLocality.isEnabled = false

        widgetTinySetupLocalitySearchView
            .toGone()

        widgetTinySetupAutoCompleteRecyclerView.apply {
            adapter = null
            toInvisible()
        }

        onLocalitySearchDisabledSubject
            .onNext(
                WeatherForecastTinySetupView
                    .Event
                    .OnLocalitySearchDisabled
            )
    }

    private fun enableLocalitySearch() {

        widgetTinySetupSelectedLocalityName.isEnabled = true
        widgetTinySetupCurrentLocality.isEnabled = true
        widgetTinySetupLocalitySearchView.apply {
            toVisible()
            isFocusable = true
            isIconified = false
            requestFocusFromTouch()
        }

        widgetTinySetupAutoCompleteRecyclerView.apply {
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
                WeatherForecastTinySetupView
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
        ).also { themeAdapter ->
            themeAdapter.setDropDownViewResource(
                R.layout.weather_forecast_dropdown_item
            )

            widgetTinySetupThemeSpinnerView.apply {
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

            widgetSetupTinyIntervalSpinnerView.apply {
                adapter = updateIntervalAdapter
            }
        }
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {

            widgetTinySetupLocalitySearchView
                .setOnCloseListener {
                    onSearchViewDismissedSubject.onNext(
                        WeatherForecastTinySetupView
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
                        WeatherForecastTinySetupView
                            .Event
                            .OnAutoCompleteItemClicked(item)
                    },
                widgetSetupTinyIntervalSpinnerView
                    .itemSelections()
                    .map { position: Int ->
                        WeatherForecastTinySetupView
                            .Event
                            .OnUpdateIntervalSelected(position)
                    },
                widgetTinySetupThemeSpinnerView
                    .itemSelections()
                    .map { position: Int ->
                        WeatherForecastTinySetupView
                            .Event
                            .OnThemeSelected(position)
                    },
                widgetTinySetupLocationModeKey
                    .checkedChanges()
                    .map { toggled: Boolean ->
                        WeatherForecastTinySetupView
                            .Event
                            .OnPhonePositionToggled(toggled)
                    },
                widgetTinySetupCancelView
                    .clicks()
                    .map {
                        WeatherForecastTinySetupView
                            .Event
                            .OnCanceledClicked
                    },
                widgetTinySetupConfirmView
                    .clicks()
                    .map {
                        WeatherForecastTinySetupView
                            .Event
                            .OnConfigurationConfirmed(
                                appWidgetId
                            )
                    },
                widgetTinySetupLocalitySearchView
                    .queryTextChangeEvents()
                    .skipInitialValue()
                    .map { searchEvent ->
                        when {
                            searchEvent.isSubmitted -> {
                                WeatherForecastTinySetupView
                                    .Event
                                    .OnSearchButtonSubmitted(
                                        searchEvent
                                            .queryText
                                            .toString()
                                    )
                            }
                            else -> {
                                WeatherForecastTinySetupView
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
                    WeatherForecastTinySetupView
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

    private fun updateSavedInstanceState(state: WeatherForecastTinySetupView.State) {
        stateParcel = WeatherForecastTinySetupView.StateParcel(
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