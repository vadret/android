package fi.kroon.vadret.presentation.weatherforecastwidget.small.provider

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
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.di.WeatherForecastSmallFeatureScope
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.model.WeatherForecastSmallModel
import fi.kroon.vadret.utils.DARK_THEME
import fi.kroon.vadret.utils.LIGHT_THEME_NO_BACKGROUND
import fi.kroon.vadret.utils.extensions.asObservable
import fi.kroon.vadret.utils.extensions.asRight
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@WeatherForecastSmallFeatureScope
class WeatherForecastSmallViewModel @Inject constructor(
    private var state: WeatherForecastSmallView.State,
    private val getWidgetWeatherForecastService: GetWidgetWeatherForecastService,
    private val getWidgetThemeKeyValueTask: GetWidgetThemeKeyValueTask,
    private val getWidgetUpdateIntervalMillisKeyValueTask: GetWidgetUpdateIntervalMillisKeyValueTask,
    private val getWidgetInitialisedKeyValueTask: GetWidgetInitialisedKeyValueTask,
    private val getWidgetLastCheckedKeyValueTask: GetWidgetLastCheckedKeyValueTask,
    private val setWidgetLastCheckedKeyValueTask: SetWidgetLastCheckedKeyValueTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WeatherForecastSmallView.Event,
        WeatherForecastSmallView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastSmallView.Event, WeatherForecastSmallView.State> { upstream: Observable<WeatherForecastSmallView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastSmallView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastSmallView.Event.OnWidgetInitialised::class.java),
                shared.ofType(WeatherForecastSmallView.Event.OnBootCompleted::class.java),
                shared.ofType(WeatherForecastSmallView.Event.OnWidgetUpdated::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WeatherForecastSmallView.Event, WeatherForecastSmallView.State> { upstream ->
        upstream.flatMap { event ->
            when (event) {
                is WeatherForecastSmallView.Event.OnWidgetInitialised -> onWidgetInitialisedEvent(
                    event.appWidgetId
                )
                WeatherForecastSmallView.Event.OnWidgetUpdated -> onWidgetUpdatedEvent()
                is WeatherForecastSmallView.Event.OnBootCompleted -> onBootCompletedEvent(
                    event.appWidgetId
                )
            }
        }
    }

    private fun onBootCompletedEvent(appWidgetId: Int): Observable<WeatherForecastSmallView.State> =
        getWidgetUpdateIntervalMillisKeyValueTask(appWidgetId = appWidgetId)
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
                    },
                    { updateIntervalMillis: Long ->
                        val renderEvent = WeatherForecastSmallView
                            .RenderEvent
                            .RestoreAppWidget(appWidgetId, updateIntervalMillis)
                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    }
                )
            }

    private fun onWidgetInitialisedEvent(appWidgetId: Int): Observable<WeatherForecastSmallView.State> =
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
                    state = state.copy(renderEvent = WeatherForecastSmallView.RenderEvent.None)
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

    private fun doNothing(): Observable<WeatherForecastSmallView.State> {
        Timber.d("DO NOTHING")
        state = state.copy(renderEvent = WeatherForecastSmallView.RenderEvent.None)
        return state.asObservable()
    }

    private fun getWeatherForecast(appWidgetId: Int): Observable<WeatherForecastSmallView.State> =
        getWidgetWeatherForecastService(
            timeStamp = state.timeStamp,
            forceNet = state.forceNet,
            appWidgetId = appWidgetId
        ).flatMapObservable { result: Either<Failure, GetWidgetWeatherForecastService.Data> ->
            result.either(
                { failure: Failure ->
                    Timber.e("$failure")
                    state = state.copy(renderEvent = WeatherForecastSmallView.RenderEvent.None)
                    state.asObservable()
                },
                { data: GetWidgetWeatherForecastService.Data ->
                    Timber.d("WEATHER: ${data.weather}")
                    val weatherForecastModelList: List<WeatherForecastSmallModel> = WeatherForecastSmallMapper(
                        data.weather!!.timeSeries!!.first(),
                        data.localityName
                    )

                    val renderEvent: WeatherForecastSmallView.RenderEvent = WeatherForecastSmallView.RenderEvent.UpdateAppWidget(
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

    private fun onWidgetUpdatedEvent(): Observable<WeatherForecastSmallView.State> {
        state = state.copy(renderEvent = WeatherForecastSmallView.RenderEvent.None)
        return state.asObservable()
    }

    fun getAppWidgetTheme(appWidgetId: Int): Single<Either<Failure, Int>> =
        getWidgetThemeKeyValueTask(appWidgetId)
            .map { result: Either<Failure, String> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        R.layout.weather_forecast_widget_small_light
                            .asRight()
                    },
                    { theme: String ->
                        when (theme) {
                            DARK_THEME -> R.layout.weather_forecast_widget_small_dark
                                .asRight()
                            LIGHT_THEME_NO_BACKGROUND -> R.layout.weather_forecast_widget_small_light_transparent
                                .asRight()
                            else -> R.layout.weather_forecast_widget_small_light
                                .asRight()
                        }
                    }
                )
            }
}