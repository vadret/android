package fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup

import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.data.weatherforecastwidget.exception.WeatherForecastWidgetFailure
import fi.kroon.vadret.domain.theme.GetThemeModeTask
import fi.kroon.vadret.domain.weatherforecastwidget.medium.SetWidgetMediumConfigurationService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetAutoCompleteService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetThemeOptionTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetUpdateIntervalMillisKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetUpdateIntervalOptionTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetInitialisedKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetLocationInformationService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetLocationModeKeyValueTask
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di.WeatherForecastMediumSetupScope
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.model.WeatherForecastMediumConfigurationModel
import fi.kroon.vadret.util.AUTOCOMPLETE_DEBOUNCE_MILLIS
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.empty
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

@WeatherForecastMediumSetupScope
class WeatherForecastMediumSetupViewModel @Inject constructor(
    private var state: WeatherForecastMediumSetupView.State,
    private val getWidgetThemeOptionTask: GetWidgetThemeOptionTask,
    private val getWidgetWidgetIntervalOptionTask: GetWidgetUpdateIntervalOptionTask,
    private val getWidgetAutoCompleteService: GetWidgetAutoCompleteService,
    private val setWidgetLocationModeKeyValueTask: SetWidgetLocationModeKeyValueTask,
    private val setWidgetMediumConfigurationService: SetWidgetMediumConfigurationService,
    private val setWidgetLocationInformationService: SetWidgetLocationInformationService,
    private val getThemeModeTask: GetThemeModeTask,
    private val setWidgetInitialisedKeyValueTask: SetWidgetInitialisedKeyValueTask,
    private val getWidgetUpdateIntervalMillisKeyValueTask: GetWidgetUpdateIntervalMillisKeyValueTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WeatherForecastMediumSetupView.Event,
        WeatherForecastMediumSetupView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastMediumSetupView.Event,
        WeatherForecastMediumSetupView.State> { upstream: Observable<WeatherForecastMediumSetupView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastMediumSetupView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastMediumSetupView.Event.OnSetupInitialised::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnConfigurationConfirmed::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnSavedInstanceStateUpdated::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnCanceledClicked::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnForecastFormatSelected::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnThemeSelected::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnUpdateIntervalSelected::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnLocalitySearchDisabled::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnLocalitySearchEnabled::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnPhonePositionToggled::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnSearchViewDismissed::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnSearchButtonSubmitted::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnAutoCompleteItemClicked::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnLocalityTextUpdated::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnLocationPermissionDenied::class.java),
                shared.ofType(WeatherForecastMediumSetupView.Event.OnSearchTextChanged::class.java)
                    .compose(applyOperatorOnSearchTextChanged)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WeatherForecastMediumSetupView.Event, WeatherForecastMediumSetupView.State> { upstream ->
        upstream.flatMap { event: WeatherForecastMediumSetupView.Event ->
            when (event) {
                is WeatherForecastMediumSetupView.Event.OnSetupInitialised -> onWidgetSetupInitialised(event.appWidgetId)
                is WeatherForecastMediumSetupView.Event.OnConfigurationConfirmed -> onConfigurationConfirmed(event.appWidgetId)
                WeatherForecastMediumSetupView.Event.OnSavedInstanceStateUpdated -> onSavedInstanceStateUpdated()
                WeatherForecastMediumSetupView.Event.OnCanceledClicked -> onCanceledClicked()
                is WeatherForecastMediumSetupView.Event.OnForecastFormatSelected -> onForecastFormatSelected(event.position)
                is WeatherForecastMediumSetupView.Event.OnThemeSelected -> onThemeSelected(event.position)
                is WeatherForecastMediumSetupView.Event.OnUpdateIntervalSelected -> onUpdateIntervalSelected(event.position)
                is WeatherForecastMediumSetupView.Event.OnPhonePositionToggled -> onPhonePositionToggled(event.toggled)
                WeatherForecastMediumSetupView.Event.OnLocalitySearchEnabled -> updateSavedInstanceState()
                WeatherForecastMediumSetupView.Event.OnLocalitySearchDisabled -> updateSavedInstanceState()
                is WeatherForecastMediumSetupView.Event.OnSearchButtonSubmitted -> onSearchButtonSubmitted(event.text)
                is WeatherForecastMediumSetupView.Event.OnAutoCompleteItemClicked -> onAutoCompleteItemClicked(event.autoCompleteItem)
                is WeatherForecastMediumSetupView.Event.OnSearchTextChanged -> onSearchTextChanged(event)
                WeatherForecastMediumSetupView.Event.OnSearchViewDismissed -> onSearchViewDismissed()
                WeatherForecastMediumSetupView.Event.OnLocalityTextUpdated -> onLocalityTextUpdated()
                WeatherForecastMediumSetupView.Event.OnLocationPermissionDenied -> onLocationPermissionDenied()
            }
        }
    }

    private val applyOperatorOnSearchTextChanged =
        ObservableTransformer<WeatherForecastMediumSetupView.Event.OnSearchTextChanged,
            WeatherForecastMediumSetupView.Event> { upstream: Observable<WeatherForecastMediumSetupView.Event.OnSearchTextChanged> ->
            upstream.debounce(AUTOCOMPLETE_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .map { event: WeatherForecastMediumSetupView.Event.OnSearchTextChanged ->
                    Timber.d("Search text: ${event.text}")
                    event.copy(text = event.text.trim())
                }
                .distinctUntilChanged { previousEvent, currentEvent ->
                    previousEvent.text == currentEvent.text
                }
                .map { it }
        }

    private fun onSearchButtonSubmitted(text: String): Observable<WeatherForecastMediumSetupView.State> =
        getWidgetAutoCompleteService(text)
            .flatMapObservable { result: Either<Failure, GetWidgetAutoCompleteService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("getWidgetAutoCompleteService failed: $failure")
                        state.asObservable()
                    },
                    { data: GetWidgetAutoCompleteService.Data ->
                        val autoCompleteItem: AutoCompleteItem = data.newFilteredList.first()
                        state = state.copy(
                            searchText = String.empty(),
                            autoCompleteItem = autoCompleteItem
                        )
                        state.asObservable()
                    }
                )
            }

    private fun onLocationPermissionDenied(): Observable<WeatherForecastMediumSetupView.State> {
        Timber.i("Location permission denied")
        state = state.copy(renderEvent = WeatherForecastMediumSetupView.RenderEvent.TurnOffPhonePositionSwitch)
        return state.asObservable()
    }

    private fun onLocalityTextUpdated(): Observable<WeatherForecastMediumSetupView.State> {
        state = state.copy(
            renderEvent = WeatherForecastMediumSetupView.RenderEvent.ResetLocalitySearch,
            searchText = String.empty()
        )
        return state.asObservable()
    }

    private fun onSearchViewDismissed(): Observable<WeatherForecastMediumSetupView.State> {
        state = state.copy(renderEvent = WeatherForecastMediumSetupView.RenderEvent.ResetLocalitySearch, searchText = String.empty())
        return state.asObservable()
    }

    private fun onAutoCompleteItemClicked(autoCompleteItem: AutoCompleteItem): Observable<WeatherForecastMediumSetupView.State> {
        val renderEvent = WeatherForecastMediumSetupView.RenderEvent.UpdateSelectedLocalityText(locality = autoCompleteItem.locality)
        state = state.copy(
            autoCompleteItem = autoCompleteItem,
            renderEvent = renderEvent,
            searchText = String.empty(),
            locality = autoCompleteItem.locality
        )
        return state.asObservable()
    }

    private fun onSearchTextChanged(event: WeatherForecastMediumSetupView.Event.OnSearchTextChanged): Observable<WeatherForecastMediumSetupView.State> =
        getWidgetAutoCompleteService(event.text)
            .map { result: Either<Failure, GetWidgetAutoCompleteService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("onSearchTextChanged: $failure")
                        state
                    },
                    { data: GetWidgetAutoCompleteService.Data ->
                        val renderEvent: WeatherForecastMediumSetupView.RenderEvent.DisplayAutoComplete =
                            WeatherForecastMediumSetupView
                                .RenderEvent
                                .DisplayAutoComplete(
                                    data.diffResult,
                                    data.newFilteredList
                                )

                        state = state.copy(
                            renderEvent = renderEvent,
                            searchText = event.text
                        )
                        state
                    }

                )
            }.toObservable()

    private fun onPhonePositionToggled(toggleEvent: Boolean): Observable<WeatherForecastMediumSetupView.State> {
        val renderEvent: WeatherForecastMediumSetupView.RenderEvent = if (toggleEvent) {
            WeatherForecastMediumSetupView.RenderEvent.DisableLocalitySearch
        } else {
            WeatherForecastMediumSetupView.RenderEvent.EnableLocalitySearch
        }

        state = state.copy(usePhonesPosition = toggleEvent, renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onSavedInstanceStateUpdated(): Observable<WeatherForecastMediumSetupView.State> {
        state = state.copy(renderEvent = WeatherForecastMediumSetupView.RenderEvent.None)
        return state.asObservable()
    }

    private fun updateSavedInstanceState(): Observable<WeatherForecastMediumSetupView.State> {
        val renderEvent: WeatherForecastMediumSetupView.RenderEvent = WeatherForecastMediumSetupView.RenderEvent.UpdateSavedInstanceState
        state = state.copy(renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onForecastFormatSelected(position: Int): Observable<WeatherForecastMediumSetupView.State> {
        Timber.d("FORECAST FORMAT SELECTED: $position")
        state = state.copy(forecastFormat = position, renderEvent = WeatherForecastMediumSetupView.RenderEvent.None)
        return state.asObservable()
    }

    private fun onThemeSelected(position: Int): Observable<WeatherForecastMediumSetupView.State> =
        getWidgetThemeOptionTask(position)
            .flatMapObservable { result ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { theme: String ->
                        Timber.d("onThemeSelected: $theme")
                        val renderEvent: WeatherForecastMediumSetupView.RenderEvent = WeatherForecastMediumSetupView.RenderEvent.UpdateSavedInstanceState
                        state = state.copy(theme = theme, renderEvent = renderEvent)
                        state.asObservable()
                    }
                )
            }

    private fun onUpdateIntervalSelected(position: Int): Observable<WeatherForecastMediumSetupView.State> =
        getWidgetWidgetIntervalOptionTask(position)
            .flatMapObservable { result ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { updateInterval: String ->
                        Timber.d("onUpdateIntervalSelected: $updateInterval")
                        state = state.copy(
                            renderEvent = WeatherForecastMediumSetupView.RenderEvent.UpdateSavedInstanceState,
                            updateInterval = updateInterval
                        )
                        state.asObservable()
                    }
                )
            }

    private fun onWidgetSetupInitialised(appWidgetId: Int): Observable<WeatherForecastMediumSetupView.State> {
        val renderEvent: WeatherForecastMediumSetupView.RenderEvent = if (appWidgetId == INVALID_APPWIDGET_ID) {
            WeatherForecastMediumSetupView.RenderEvent.FinishActivity
        } else {
            WeatherForecastMediumSetupView.RenderEvent.None
        }
        Timber.d("onWidgetSetupInitialised: $appWidgetId")
        state = state.copy(renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onConfigurationConfirmed(appWidgetId: Int): Observable<WeatherForecastMediumSetupView.State> = when {
        (!state.usePhonesPosition && (state.locality == String.empty() || state.locality == null)) -> {

            Timber.d("STATE: $state")
            val error: Int = getErrorCode(WeatherForecastWidgetFailure.NoLocalitySelected)
            val renderEvent = WeatherForecastMediumSetupView.RenderEvent.DisplayError(error)

            state = state.copy(renderEvent = renderEvent)
            state.asObservable()
        }
        else -> {

            val config = WeatherForecastMediumConfigurationModel(
                appWidgetId = appWidgetId,
                theme = state.theme,
                forecastFormat = state.forecastFormat,
                updateInterval = state.updateInterval
            )
            setWidgetInitialisedKeyValueTask(appWidgetId, true)
                .flatMapObservable { _ ->
                    setWidgetLocationModeKeyValueTask(appWidgetId, state.usePhonesPosition)
                        .flatMapObservable { _ ->
                            setWidgetMediumConfigurationService(config)
                                .flatMapObservable { _ ->
                                    setWidgetLocationInformationService(appWidgetId, state.autoCompleteItem)
                                        .flatMapObservable { _ ->
                                            getWidgetUpdateIntervalMillisKeyValueTask(appWidgetId)
                                                .flatMapObservable { result ->
                                                    result.either(
                                                        { failure: Failure ->
                                                            Timber.e("Failure: $failure")
                                                            state.asObservable()
                                                        },
                                                        { updateIntervalMillis: Long ->
                                                            val renderEvent: WeatherForecastMediumSetupView.RenderEvent.ConfirmConfiguration =
                                                                WeatherForecastMediumSetupView.RenderEvent.ConfirmConfiguration(updateIntervalMillis)
                                                            state = state.copy(renderEvent = renderEvent)
                                                            state.asObservable()
                                                        }
                                                    )
                                                }
                                        }
                                }
                        }
                }
        }
    }

    private fun onCanceledClicked(): Observable<WeatherForecastMediumSetupView.State> {
        state = state.copy(renderEvent = WeatherForecastMediumSetupView.RenderEvent.FinishActivity)
        return state.asObservable()
    }

    /**
     *  @Workaround
     *  This needs to be exposed
     *  to allow for checking/applying theme synchronously
     *  before setContentView has executed in [WeatherForecastSmallSetup] onCreate.
     */
    fun getThemeMode(): Single<Either<Failure, Theme>> = getThemeModeTask()
        .map { result ->
            result.either(
                {
                    result
                },
                { theme: Theme ->
                    if (state.theme != theme.name) {
                        state = state.copy(theme = theme.name)
                    }
                    result
                }
            )
        }
}