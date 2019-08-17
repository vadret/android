package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup

import android.appwidget.AppWidgetManager
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
import fi.kroon.vadret.domain.weatherforecastwidget.tiny.SetWidgetTinyConfigurationService
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.di.WeatherForecastTinySetupScope
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.model.WeatherForecastTinyConfigurationModel
import fi.kroon.vadret.util.AUTOCOMPLETE_DEBOUNCE_MILLIS
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.empty
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

@WeatherForecastTinySetupScope
class WeatherForecastTinySetupViewModel @Inject constructor(
    private var state: WeatherForecastTinySetupView.State,
    private val getWidgetThemeOptionTask: GetWidgetThemeOptionTask,
    private val getWidgetWidgetIntervalOptionTask: GetWidgetUpdateIntervalOptionTask,
    private val getWidgetAutoCompleteService: GetWidgetAutoCompleteService,
    private val setWidgetLocationModeKeyValueTask: SetWidgetLocationModeKeyValueTask,
    private val setWidgetTinyConfigurationService: SetWidgetTinyConfigurationService,
    private val setWidgetLocationInformationService: SetWidgetLocationInformationService,
    private val getThemeModeTask: GetThemeModeTask,
    private val setWidgetInitialisedKeyValueTask: SetWidgetInitialisedKeyValueTask,
    private val getWidgetUpdateIntervalMillisKeyValueTask: GetWidgetUpdateIntervalMillisKeyValueTask
) : IViewModel {

    companion object {
        const val invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    }

    operator fun invoke(): ObservableTransformer<WeatherForecastTinySetupView.Event,
        WeatherForecastTinySetupView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastTinySetupView.Event,
        WeatherForecastTinySetupView.State> { upstream: Observable<WeatherForecastTinySetupView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastTinySetupView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastTinySetupView.Event.OnSetupInitialised::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnConfigurationConfirmed::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnSavedInstanceStateUpdated::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnCanceledClicked::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnThemeSelected::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnUpdateIntervalSelected::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnLocalitySearchDisabled::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnLocalitySearchEnabled::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnPhonePositionToggled::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnSearchViewDismissed::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnSearchButtonSubmitted::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnAutoCompleteItemClicked::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnLocalityTextUpdated::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnLocationPermissionDenied::class.java),
                shared.ofType(WeatherForecastTinySetupView.Event.OnSearchTextChanged::class.java)
                    .compose(applyOperatorOnSearchTextChanged)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WeatherForecastTinySetupView.Event, WeatherForecastTinySetupView.State> { upstream ->
        upstream.flatMap { event: WeatherForecastTinySetupView.Event ->
            when (event) {
                is WeatherForecastTinySetupView.Event.OnSetupInitialised -> onWidgetSetupInitialised(event.appWidgetId)
                is WeatherForecastTinySetupView.Event.OnConfigurationConfirmed -> onConfigurationConfirmed(event.appWidgetId)
                WeatherForecastTinySetupView.Event.OnSavedInstanceStateUpdated -> onSavedInstanceStateUpdated()
                WeatherForecastTinySetupView.Event.OnCanceledClicked -> onCanceledClicked()
                is WeatherForecastTinySetupView.Event.OnThemeSelected -> onThemeSelected(event.position)
                is WeatherForecastTinySetupView.Event.OnUpdateIntervalSelected -> onUpdateIntervalSelected(event.position)
                is WeatherForecastTinySetupView.Event.OnPhonePositionToggled -> onPhonePositionToggled(event.toggled)
                WeatherForecastTinySetupView.Event.OnLocalitySearchEnabled -> updateSavedInstanceState()
                WeatherForecastTinySetupView.Event.OnLocalitySearchDisabled -> updateSavedInstanceState()
                is WeatherForecastTinySetupView.Event.OnSearchButtonSubmitted -> onSearchButtonSubmitted(event.text)
                is WeatherForecastTinySetupView.Event.OnAutoCompleteItemClicked -> onAutoCompleteItemClicked(event.autoCompleteItem)
                is WeatherForecastTinySetupView.Event.OnSearchTextChanged -> onSearchTextChanged(event)
                WeatherForecastTinySetupView.Event.OnSearchViewDismissed -> onSearchViewDismissed()
                WeatherForecastTinySetupView.Event.OnLocalityTextUpdated -> onLocalityTextUpdated()
                WeatherForecastTinySetupView.Event.OnLocationPermissionDenied -> onLocationPermissionDenied()
            }
        }
    }

    private val applyOperatorOnSearchTextChanged =
        ObservableTransformer<WeatherForecastTinySetupView.Event.OnSearchTextChanged,
            WeatherForecastTinySetupView.Event> { upstream: Observable<WeatherForecastTinySetupView.Event.OnSearchTextChanged> ->
            upstream.debounce(AUTOCOMPLETE_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .map { event: WeatherForecastTinySetupView.Event.OnSearchTextChanged ->
                    Timber.d("Search text: ${event.text}")
                    event.copy(text = event.text.trim())
                }
                .distinctUntilChanged { previousEvent, currentEvent ->
                    previousEvent.text == currentEvent.text
                }
                .map { it }
        }

    private fun onSearchButtonSubmitted(text: String): Observable<WeatherForecastTinySetupView.State> =
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

    private fun onLocationPermissionDenied(): Observable<WeatherForecastTinySetupView.State> {
        Timber.i("Location permission denied")
        state = state.copy(renderEvent = WeatherForecastTinySetupView.RenderEvent.TurnOffPhonePositionSwitch)
        return state.asObservable()
    }

    private fun onLocalityTextUpdated(): Observable<WeatherForecastTinySetupView.State> {
        state = state.copy(
            renderEvent = WeatherForecastTinySetupView.RenderEvent.ResetLocalitySearch,
            searchText = String.empty()
        )
        return state.asObservable()
    }

    private fun onSearchViewDismissed(): Observable<WeatherForecastTinySetupView.State> {
        state = state.copy(renderEvent = WeatherForecastTinySetupView.RenderEvent.ResetLocalitySearch, searchText = String.empty())
        return state.asObservable()
    }

    private fun onAutoCompleteItemClicked(autoCompleteItem: AutoCompleteItem): Observable<WeatherForecastTinySetupView.State> {
        val renderEvent = WeatherForecastTinySetupView.RenderEvent.UpdateSelectedLocalityText(locality = autoCompleteItem.locality)
        state = state.copy(
            autoCompleteItem = autoCompleteItem,
            renderEvent = renderEvent,
            searchText = String.empty(),
            locality = autoCompleteItem.locality
        )
        return state.asObservable()
    }

    private fun onSearchTextChanged(event: WeatherForecastTinySetupView.Event.OnSearchTextChanged): Observable<WeatherForecastTinySetupView.State> =
        getWidgetAutoCompleteService(event.text)
            .map { result: Either<Failure, GetWidgetAutoCompleteService.Data> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("onSearchTextChanged: $failure")
                        state
                    },
                    { data: GetWidgetAutoCompleteService.Data ->
                        val renderEvent: WeatherForecastTinySetupView.RenderEvent.DisplayAutoComplete =
                            WeatherForecastTinySetupView
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

    private fun onPhonePositionToggled(toggleEvent: Boolean): Observable<WeatherForecastTinySetupView.State> {
        val renderEvent: WeatherForecastTinySetupView.RenderEvent = if (toggleEvent) {
            WeatherForecastTinySetupView.RenderEvent.DisableLocalitySearch
        } else {
            WeatherForecastTinySetupView.RenderEvent.EnableLocalitySearch
        }

        state = state.copy(usePhonesPosition = toggleEvent, renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onSavedInstanceStateUpdated(): Observable<WeatherForecastTinySetupView.State> {
        state = state.copy(renderEvent = WeatherForecastTinySetupView.RenderEvent.None)
        return state.asObservable()
    }

    private fun updateSavedInstanceState(): Observable<WeatherForecastTinySetupView.State> {
        val renderEvent: WeatherForecastTinySetupView.RenderEvent = WeatherForecastTinySetupView.RenderEvent.UpdateSavedInstanceState
        state = state.copy(renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onThemeSelected(position: Int): Observable<WeatherForecastTinySetupView.State> =
        getWidgetThemeOptionTask(position)
            .flatMapObservable { result ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { theme: String ->
                        Timber.d("onThemeSelected: $theme")
                        val renderEvent: WeatherForecastTinySetupView.RenderEvent = WeatherForecastTinySetupView.RenderEvent.UpdateSavedInstanceState
                        state = state.copy(theme = theme, renderEvent = renderEvent)
                        state.asObservable()
                    }
                )
            }

    private fun onUpdateIntervalSelected(position: Int): Observable<WeatherForecastTinySetupView.State> =
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
                            renderEvent = WeatherForecastTinySetupView.RenderEvent.UpdateSavedInstanceState,
                            updateInterval = updateInterval
                        )
                        state.asObservable()
                    }
                )
            }

    private fun onWidgetSetupInitialised(appWidgetId: Int): Observable<WeatherForecastTinySetupView.State> {
        val renderEvent: WeatherForecastTinySetupView.RenderEvent = if (appWidgetId == invalidAppWidgetId) {
            WeatherForecastTinySetupView.RenderEvent.FinishActivity
        } else {
            WeatherForecastTinySetupView.RenderEvent.None
        }
        Timber.d("onWidgetSetupInitialised: $appWidgetId")
        state = state.copy(renderEvent = renderEvent)
        return state.asObservable()
    }

    private fun onConfigurationConfirmed(appWidgetId: Int): Observable<WeatherForecastTinySetupView.State> = when {
        (!state.usePhonesPosition && (state.locality == String.empty() || state.locality == null)) -> {

            Timber.d("STATE: $state")
            val error: Int = getErrorCode(WeatherForecastWidgetFailure.NoLocalitySelected)
            val renderEvent = WeatherForecastTinySetupView.RenderEvent.DisplayError(error)

            state = state.copy(renderEvent = renderEvent)
            state.asObservable()
        }
        else -> {

            val config = WeatherForecastTinyConfigurationModel(
                appWidgetId = appWidgetId,
                theme = state.theme,
                updateInterval = state.updateInterval
            )
            setWidgetInitialisedKeyValueTask(appWidgetId, true)
                .flatMapObservable { _ ->
                    setWidgetLocationModeKeyValueTask(appWidgetId, state.usePhonesPosition)
                        .flatMapObservable { _ ->
                            setWidgetTinyConfigurationService(config)
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
                                                            val renderEvent: WeatherForecastTinySetupView.RenderEvent.ConfirmConfiguration =
                                                                WeatherForecastTinySetupView.RenderEvent.ConfirmConfiguration(updateIntervalMillis)
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

    private fun onCanceledClicked(): Observable<WeatherForecastTinySetupView.State> {
        state = state.copy(renderEvent = WeatherForecastTinySetupView.RenderEvent.FinishActivity)
        return state.asObservable()
    }

    /**
     *  @Workaround
     *  This needs to be exposed
     *  to allow for checking/applying theme synchronously
     *  before setContentView has executed in [WeatherForecastTinySetup] onCreate.
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