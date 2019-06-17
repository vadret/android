package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider

import fi.kroon.vadret.R
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetWeatherForecastService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetInitialisedKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetLastCheckedKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetThemeKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetUpdateIntervalMillisKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetLastCheckedKeyValueTask
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.di.WeatherForecastTinyFeatureScope
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.model.WeatherForecastTinyModel
import fi.kroon.vadret.util.DARK_THEME
import fi.kroon.vadret.util.LIGHT_THEME_NO_BACKGROUND
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@WeatherForecastTinyFeatureScope
class WeatherForecastTinyViewModel @Inject constructor(
    private var state: WeatherForecastTinyView.State,
    private val getWidgetWeatherForecastService: GetWidgetWeatherForecastService,
    private val getWidgetThemeKeyValueTask: GetWidgetThemeKeyValueTask,
    private val getWidgetUpdateIntervalMillisKeyValueTask: GetWidgetUpdateIntervalMillisKeyValueTask,
    private val getWidgetInitialisedKeyValueTask: GetWidgetInitialisedKeyValueTask,
    private val getWidgetLastCheckedKeyValueTask: GetWidgetLastCheckedKeyValueTask,
    private val setWidgetLastCheckedKeyValueTask: SetWidgetLastCheckedKeyValueTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WeatherForecastTinyView.Event,
        WeatherForecastTinyView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastTinyView.Event, WeatherForecastTinyView.State> { upstream: Observable<WeatherForecastTinyView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastTinyView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastTinyView.Event.OnWidgetInitialised::class.java),
                shared.ofType(WeatherForecastTinyView.Event.OnBootCompleted::class.java),
                shared.ofType(WeatherForecastTinyView.Event.OnWidgetUpdated::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WeatherForecastTinyView.Event, WeatherForecastTinyView.State> { upstream ->
        upstream.flatMap { event ->
            when (event) {
                is WeatherForecastTinyView.Event.OnWidgetInitialised -> onWidgetInitialisedEvent(
                    event.appWidgetId
                )
                WeatherForecastTinyView.Event.OnWidgetUpdated -> onWidgetUpdatedEvent()
                is WeatherForecastTinyView.Event.OnBootCompleted -> onBootCompletedEvent(
                    event.appWidgetId
                )
            }
        }
    }

    private fun onBootCompletedEvent(appWidgetId: Int): Observable<WeatherForecastTinyView.State> =
        getWidgetUpdateIntervalMillisKeyValueTask(appWidgetId = appWidgetId)
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
                    },
                    { updateIntervalMillis: Long ->
                        val renderEvent = WeatherForecastTinyView
                            .RenderEvent
                            .RestoreAppWidget(appWidgetId, updateIntervalMillis)
                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    }
                )
            }

    private fun onWidgetInitialisedEvent(appWidgetId: Int): Observable<WeatherForecastTinyView.State> =
        getWidgetLastCheckedKeyValueTask(appWidgetId)
            .flatMapObservable { result ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { lastChecked: Long ->
                        state = state.copy(timeStamp = lastChecked)
                        getInitialisedState(appWidgetId)
                    }
                )
            }

    private fun getInitialisedState(appWidgetId: Int) = getWidgetInitialisedKeyValueTask(appWidgetId)
        .flatMapObservable { result: Either<Failure, Boolean> ->
            result.either(
                { failure: Failure ->
                    Timber.e("Failure: $failure")
                    state = state.copy(renderEvent = WeatherForecastTinyView.RenderEvent.None)
                    state.asObservable()
                },
                { isInitialised: Boolean ->
                    when (isInitialised) {
                        true -> {
                            getWeatherForecast(appWidgetId)
                        }
                        false -> {
                            doNothing()
                        }
                    }
                }
            )
        }

    private fun doNothing(): Observable<WeatherForecastTinyView.State> {
        Timber.d("DO NOTHING")
        state = state.copy(renderEvent = WeatherForecastTinyView.RenderEvent.None)
        return state.asObservable()
    }

    /**
     *  [timeStamp] is null by default, so on first
     *  run it will instead pass [currentTimeMillis]
     *
     *  The state carrying timeStamp will be updated
     *  only if the response was not a fromCache response.
     */
    private fun getWeatherForecast(appWidgetId: Int): Observable<WeatherForecastTinyView.State> =
        getWidgetWeatherForecastService(
            timeStamp = state.timeStamp,
            forceNet = state.forceNet,
            appWidgetId = appWidgetId
        ).flatMapObservable { result: Either<Failure, GetWidgetWeatherForecastService.Data> ->
            result.either(
                { failure: Failure ->
                    Timber.e("$failure")
                    state = state.copy(renderEvent = WeatherForecastTinyView.RenderEvent.None)
                    state.asObservable()
                },
                { data: GetWidgetWeatherForecastService.Data ->
                    Timber.d("WEATHER: ${data.weather}")

                    val weatherForecastModelList: List<WeatherForecastTinyModel> = WeatherForecastTinyMapper(
                        data.weather!!.timeSeries!!.first(),
                        data.localityName
                    )

                    val renderEvent: WeatherForecastTinyView.RenderEvent = WeatherForecastTinyView.RenderEvent.UpdateAppWidget(
                        weather = weatherForecastModelList.first()
                    )

                    setWidgetLastCheckedKeyValueTask(appWidgetId = appWidgetId, value = data.timeStamp)
                        .flatMapObservable { result ->
                            result.either(
                                { failure: Failure ->
                                    Timber.e("Failure: $failure")
                                    state.asObservable()
                                },
                                {
                                    state = state.copy(
                                        renderEvent = renderEvent,
                                        appWidgetId = appWidgetId,
                                        timeStamp = data.timeStamp
                                    )
                                    state.asObservable()
                                }
                            )
                        }
                }
            )
        }

    private fun onWidgetUpdatedEvent(): Observable<WeatherForecastTinyView.State> {
        state = state.copy(renderEvent = WeatherForecastTinyView.RenderEvent.None)
        return state.asObservable()
    }

    fun getAppWidgetTheme(appWidgetId: Int): Single<Either<Failure, Int>> =
        getWidgetThemeKeyValueTask(appWidgetId)
            .map { result: Either<Failure, String> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        R.layout.weather_forecast_widget_tiny_light
                            .asRight()
                    },
                    { theme: String ->
                        when (theme) {
                            DARK_THEME -> R.layout.weather_forecast_widget_tiny_dark
                                .asRight()
                            LIGHT_THEME_NO_BACKGROUND -> R.layout.weather_forecast_widget_tiny_light_transparent
                                .asRight()
                            else -> R.layout.weather_forecast_widget_tiny_light
                                .asRight()
                        }
                    }
                )
            }
}