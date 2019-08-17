package fi.kroon.vadret.presentation.weatherforecastwidget.small.setup

import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.data.weatherforecastwidget.exception.WeatherForecastWidgetFailure
import fi.kroon.vadret.domain.theme.GetThemeModeTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetAutoCompleteService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetThemeOptionTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetUpdateIntervalMillisKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetUpdateIntervalOptionTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetInitialisedKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetLocationInformationService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetLocationModeKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.small.SetWidgetSmallConfigurationService
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.di.WeatherForecastSmallSetupScope
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.model.WeatherForecastSmallConfigurationModel
import fi.kroon.vadret.util.AUTOCOMPLETE_DEBOUNCE_MILLIS
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.empty
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

@WeatherForecastSmallSetupScope
class WeatherForecastSmallSetupViewModel @Inject constructor(
    private var state: WeatherForecastSmallSetupView.State,
    private val getWidgetThemeOptionTask: GetWidgetThemeOptionTask,
    private val getWidgetWidgetIntervalOptionTask: GetWidgetUpdateIntervalOptionTask,
    private val getWidgetAutoCompleteService: GetWidgetAutoCompleteService,
    private val setWidgetLocationModeKeyValueTask: SetWidgetLocationModeKeyValueTask,
    private val setWidgetSmallConfigurationService: SetWidgetSmallConfigurationService,
    private val setWidgetLocationInformationService: SetWidgetLocationInformationService,
    private val getThemeModeTask: GetThemeModeTask,
    private val setWidgetInitialisedKeyValueTask: SetWidgetInitialisedKeyValueTask,
    private val getWidgetUpdateIntervalMillisKeyValueTask: GetWidgetUpdateIntervalMillisKeyValueTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WeatherForecastSmallSetupView.Event,
        WeatherForecastSmallSetupView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastSmallSetupView.Event,
        WeatherForecastSmallSetupView.State> { upstream: Observable<WeatherForecastSmallSetupView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastSmallSetupView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastSmallSetupView.Event.OnSetupInitialised::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnConfigurationConfirmed::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnSavedInstanceStateUpdated::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnCanceledClicked::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnThemeSelected::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnUpdateIntervalSelected::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnLocalitySearchDisabled::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnLocalitySearchEnabled::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnPhonePositionToggled::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnSearchViewDismissed::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnSearchButtonSubmitted::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnAutoCompleteItemClicked::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnLocalityTextUpdated::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnLocationPermissionDenied::class.java),
                shared.ofType(WeatherForecastSmallSetupView.Event.OnSearchTextChanged::class.java)
                    .compose(applyOperatorOnSearchTextChanged)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WeatherForecastSmallSetupView.Event, WeatherForecastSmallSetupView.State> { upstream ->
        upstream.flatMap { event: WeatherForecastSmallSetupView.Event ->
            when (event) {
                is WeatherForecastSmallSetupView.Event.OnSetupInitialised -> onWidgetSetupInitialised(event.appWidgetId)
                is WeatherForecastSmallSetupView.Event.OnConfigurationConfirmed -> onConfigurationConfirmed(event.appWidgetId)
                WeatherForecastSmallSetupView.Event.OnSavedInstanceStateUpdated -> onSavedInstanceStateUpdated()
                WeatherForecastSmallSetupView.Event.OnCanceledClicked -> onCanceledClicked()
                is WeatherForecastSmallSetupView.Event.OnThemeSelected -> onThemeSelected(event.position)
                is WeatherForecastSmallSetupView.Event.OnUpdateIntervalSelected -> onUpdateIntervalSelected(event.position)
                is WeatherForecastSmallSetupView.Event.OnPhonePositionToggled -> onPhonePositionToggled(event.toggled)
                WeatherForecastSmallSetupView.Event.OnLocalitySearchEnabled -> updateSavedInstanceState()
                WeatherForecastSmallSetupView.Event.OnLocalitySearchDisabled -> updateSavedInstanceState()
                is WeatherForecastSmallSetupView.Event.OnSearchButtonSubmitted -> onSearchButtonSubmitted(event.text)
                is WeatherForecastSmallSetupView.Event.OnAutoCompleteItemClicked -> onAutoCompleteItemClicked(event.autoCompleteItem)
                is WeatherForecastSmallSetupView.Event.OnSearchTextChanged -> onSearchTextChanged(event)
                WeatherForecastSmallSetupView.Event.OnSearchViewDismissed -> onSearchViewDismissed()
                WeatherForecastSmallSetupView.Event.OnLocalityTextUpdated -> onLocalityTextUpdated()
                WeatherForecastSmallSetupView.Event.OnLocationPermissionDenied -> onLocationPermissionDenied()
            }
        }
    }

    private val applyOperatorOnSearchTextChanged =
        ObservableTransformer<WeatherForecastSmallSetupView.Event.OnSearchTextChanged,
            WeatherForecastSmallSetupView.Event> { upstream: Observable<WeatherForecastSmallSetupView.Event.OnSearchTextChanged> ->
            upstream.debounce(AUTOCOMPLETE_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .map { event: WeatherForecastSmallSetupView.Event.OnSearchTextChanged ->
                    Timber.d("Search text: ${event.text}")
                    event.copy(text = event.text.trim())
                }
                .distinctUntilChanged { previousEvent, currentEvent ->
                    previousEvent.text == currentEvent.text
                }
                .map { it }
        }

    private fun onSearchButtonSubmitted(text: String): Observable<WeatherForecastSmallSetupView.State> =
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

    private fun onLocationPermissionDenied(): Observable<WeatherForecastSmallSetupView.State> {
        Timber.i("Location permission denied")
        state = state.copy(renderEvent = WeatherForecastSmallSetupView.RenderEvent.TurnOffPhonePositionSwitch)
        return state.asObservable()
    }

    private fun onLocalityTextUpdated(): Observable<WeatherForecastSmallSetupView.State> {
        state = state.copy(
            renderEvent = WeatherForecastSmallSetupView.RenderEvent.ResetLocalitySearch,
            searchText = String.empty()
        )
        return state.asObservable()
    }

    private fun onSearchViewDismissed(): Observable<WeatherForecastSmallSetupView.State> {
        state = state.copy(renderEvent = WeatherForecastSmallSetupView.RenderEvent.ResetLocalitySearch, searchText = String.empty())
        return state.asObservable()
    }

    private fun onAutoCompleteItemClicked(autoCompleteItem: AutoCompleteItem): Observable<WeatherForecastSmallSetupView.State> {
        val renderEvent = WeatherForecastSmallSetupView.RenderEvent.UpdateSelectedLocalityText(locality = autoCompleteItem.locality)
        state = state.copy(
            autoCompleteItem = autoCompleteItem,
            renderEvent = renderEvent,
            searchText = String.empty(),
            locality = autoCompleteItem.locality
        )
        return state.asObservable()
    }

    private fun onSearchTextChanged(event: WeatherForecastSmallSetupView.Event.OnSearchTextChanged): Observable<WeatherForecastSmallSetupView.State> =
        getWidgetAutoCompleteService(event.text)
            .map { result: Either<Failure, GetWidgetAutoCompleteService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("onSearchTextChanged: $failure")
                        state
                    },
                    { data: GetWidgetAutoCompleteService.Data ->
                        val renderEvent: WeatherForecastSmallSetupView.RenderEvent.DisplayAutoComplete =
                            WeatherForecastSmallSetupView
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

    private fun onPhonePositionToggled(toggleEvent: Boolean): Observable<WeatherForecastSmallSetupView.State> {
        val renderEvent: WeatherForecastSmallSetupView.RenderEvent = if (toggleEvent) {
            WeatherForecastSmallSetupView.RenderEvent.DisableLocalitySearch
        } else {
            WeatherForecastSmallSetupView.RenderEvent.EnableLocalitySearch
        }

        state = state.copy(usePhonesPosition = toggleEvent, renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onSavedInstanceStateUpdated(): Observable<WeatherForecastSmallSetupView.State> {
        state = state.copy(renderEvent = WeatherForecastSmallSetupView.RenderEvent.None)
        return state.asObservable()
    }

    private fun updateSavedInstanceState(): Observable<WeatherForecastSmallSetupView.State> {
        val renderEvent: WeatherForecastSmallSetupView.RenderEvent = WeatherForecastSmallSetupView.RenderEvent.UpdateSavedInstanceState
        state = state.copy(renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onThemeSelected(position: Int): Observable<WeatherForecastSmallSetupView.State> =
        getWidgetThemeOptionTask(position)
            .flatMapObservable { result ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { theme: String ->
                        Timber.d("onThemeSelected: $theme")
                        val renderEvent: WeatherForecastSmallSetupView.RenderEvent = WeatherForecastSmallSetupView.RenderEvent.UpdateSavedInstanceState
                        state = state.copy(theme = theme, renderEvent = renderEvent)
                        state.asObservable()
                    }
                )
            }

    private fun onUpdateIntervalSelected(position: Int): Observable<WeatherForecastSmallSetupView.State> =
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
                            renderEvent = WeatherForecastSmallSetupView.RenderEvent.UpdateSavedInstanceState,
                            updateInterval = updateInterval
                        )
                        state.asObservable()
                    }
                )
            }

    private fun onWidgetSetupInitialised(appWidgetId: Int): Observable<WeatherForecastSmallSetupView.State> {
        val renderEvent: WeatherForecastSmallSetupView.RenderEvent = if (appWidgetId == INVALID_APPWIDGET_ID) {
            WeatherForecastSmallSetupView.RenderEvent.FinishActivity
        } else {
            WeatherForecastSmallSetupView.RenderEvent.None
        }
        Timber.d("onWidgetSetupInitialised: $appWidgetId")
        state = state.copy(renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onConfigurationConfirmed(appWidgetId: Int): Observable<WeatherForecastSmallSetupView.State> = when {
        (!state.usePhonesPosition && (state.locality == String.empty() || state.locality == null)) -> {

            Timber.d("STATE: $state")
            val error: Int = getErrorCode(WeatherForecastWidgetFailure.NoLocalitySelected)
            val renderEvent = WeatherForecastSmallSetupView.RenderEvent.DisplayError(error)

            state = state.copy(renderEvent = renderEvent)
            state.asObservable()
        }
        else -> {

            val config = WeatherForecastSmallConfigurationModel(
                appWidgetId = appWidgetId,
                theme = state.theme,
                updateInterval = state.updateInterval
            )
            setWidgetInitialisedKeyValueTask(appWidgetId, true)
                .flatMapObservable { _ ->
                    setWidgetLocationModeKeyValueTask(appWidgetId, state.usePhonesPosition)
                        .flatMapObservable { _ ->
                            setWidgetSmallConfigurationService(config)
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
                                                            val renderEvent: WeatherForecastSmallSetupView.RenderEvent.ConfirmConfiguration =
                                                                WeatherForecastSmallSetupView.RenderEvent.ConfirmConfiguration(updateIntervalMillis)
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

    private fun onCanceledClicked(): Observable<WeatherForecastSmallSetupView.State> {
        state = state.copy(renderEvent = WeatherForecastSmallSetupView.RenderEvent.FinishActivity)
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