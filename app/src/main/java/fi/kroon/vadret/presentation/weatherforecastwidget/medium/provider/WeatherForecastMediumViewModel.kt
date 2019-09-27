package fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider

import fi.kroon.vadret.R
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.weatherforecastwidget.medium.GetWidgetForecastFormatKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetInitialisedKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetLastCheckedKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetThemeKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetUpdateIntervalMillisKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetWeatherForecastService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetLastCheckedKeyValueTask
import fi.kroon.vadret.presentation.shared.IViewModel
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.di.WeatherForecastMediumFeatureScope
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.model.WeatherForecastMediumModel
import fi.kroon.vadret.util.DARK_THEME
import fi.kroon.vadret.util.LIGHT_THEME_NO_BACKGROUND
import fi.kroon.vadret.util.extension.asObservable
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

@WeatherForecastMediumFeatureScope
class WeatherForecastMediumViewModel @Inject constructor(
    private var state: WeatherForecastMediumView.State,
    private val getWidgetWeatherForecastService: GetWidgetWeatherForecastService,
    private val getWidgetThemeKeyValueTask: GetWidgetThemeKeyValueTask,
    private val getWidgetForecastFormatKeyValueTask: GetWidgetForecastFormatKeyValueTask,
    private val getWidgetUpdateIntervalMillisKeyValueTask: GetWidgetUpdateIntervalMillisKeyValueTask,
    private val getWidgetInitialisedKeyValueTask: GetWidgetInitialisedKeyValueTask,
    private val getWidgetLastCheckedKeyValueTask: GetWidgetLastCheckedKeyValueTask,
    private val setWidgetLastCheckedKeyValueTask: SetWidgetLastCheckedKeyValueTask
) : IViewModel {

    operator fun invoke(): ObservableTransformer<WeatherForecastMediumView.Event,
        WeatherForecastMediumView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastMediumView.Event, WeatherForecastMediumView.State> { upstream: Observable<WeatherForecastMediumView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastMediumView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastMediumView.Event.OnWidgetInitialised::class.java),
                shared.ofType(WeatherForecastMediumView.Event.OnBootCompleted::class.java),
                shared.ofType(WeatherForecastMediumView.Event.OnWidgetUpdated::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WeatherForecastMediumView.Event, WeatherForecastMediumView.State> { upstream ->
        upstream.flatMap { event ->
            when (event) {
                is WeatherForecastMediumView.Event.OnWidgetInitialised -> onWidgetInitialisedEvent(
                    event.appWidgetId
                )
                WeatherForecastMediumView.Event.OnWidgetUpdated -> onWidgetUpdatedEvent()
                is WeatherForecastMediumView.Event.OnBootCompleted -> onBootCompletedEvent(
                    event.appWidgetId
                )
            }
        }
    }

    private fun onBootCompletedEvent(appWidgetId: Int): Observable<WeatherForecastMediumView.State> =
        getWidgetUpdateIntervalMillisKeyValueTask(appWidgetId = appWidgetId)
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("failure: $failure")
                        state.asObservable()
                    },
                    { updateIntervalMillis: Long ->
                        val renderEvent = WeatherForecastMediumView
                            .RenderEvent
                            .RestoreAppWidget(appWidgetId, updateIntervalMillis)
                        state = state.copy(renderEvent = renderEvent)
                        state.asObservable()
                    }
                )
            }

    private fun onWidgetInitialisedEvent(appWidgetId: Int): Observable<WeatherForecastMediumView.State> =
        getWidgetInitialisedKeyValueTask(appWidgetId)
            .flatMapObservable { result: Either<Failure, Boolean> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state = state.copy(renderEvent = WeatherForecastMediumView.RenderEvent.None)
                        state.asObservable()
                    },
                    { isInitialised: Boolean ->
                        when (isInitialised) {
                            true -> {
                                getStepSize(appWidgetId)
                            }
                            false -> {
                                doNothing()
                            }
                        }
                    }
                )
            }

    private fun getStepSize(appWidgetId: Int) =
        getWidgetForecastFormatKeyValueTask(appWidgetId)
            .flatMapObservable { result ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state = state.copy(renderEvent = WeatherForecastMediumView.RenderEvent.None)
                        state.asObservable()
                    },
                    { stepSize: Int ->
                        state = state.copy(stepSize = stepSize)
                        getWeatherForecast(appWidgetId)
                    }
                )
            }

    private fun doNothing(): Observable<WeatherForecastMediumView.State> {
        Timber.d("DO NOTHING")
        state = state.copy(renderEvent = WeatherForecastMediumView.RenderEvent.None)
        return state.asObservable()
    }

    private fun getWeatherForecast(appWidgetId: Int): Observable<WeatherForecastMediumView.State> =
        getWidgetLastCheckedKeyValueTask(appWidgetId = appWidgetId)
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state = state.copy(renderEvent = WeatherForecastMediumView.RenderEvent.None)
                        state.asObservable()
                    },
                    { timeStamp: Long ->
                        getWidgetWeatherForecastService(
                            timeStamp = timeStamp,
                            forceNet = state.forceNet,
                            appWidgetId = appWidgetId
                        ).flatMapObservable { result: Either<Failure, GetWidgetWeatherForecastService.Data> ->
                            result.either(
                                { failure: Failure ->
                                    Timber.e("$failure")
                                    state = state.copy(renderEvent = WeatherForecastMediumView.RenderEvent.None)
                                    state.asObservable()
                                },
                                { data: GetWidgetWeatherForecastService.Data ->

                                    val weatherForecastModelList: List<WeatherForecastMediumModel> = WeatherForecastMediumMapper(
                                        data.weather!!.timeSeries!!,
                                        data.localityName
                                    )

                                    val renderEvent: WeatherForecastMediumView.RenderEvent = WeatherForecastMediumView.RenderEvent.UpdateAppWidget(
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
                                                        appWidgetId = appWidgetId
                                                    )
                                                    state.asObservable()
                                                }
                                            )
                                        }
                                }
                            )
                        }
                    }
                )
            }

    private fun onWidgetUpdatedEvent(): Observable<WeatherForecastMediumView.State> {
        state = state.copy(renderEvent = WeatherForecastMediumView.RenderEvent.None)
        return state.asObservable()
    }

    fun getAppWidgetTheme(appWidgetId: Int): Single<Either<Failure, Int>> =
        getWidgetThemeKeyValueTask(appWidgetId)
            .map { result: Either<Failure, String> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        R.layout.weather_forecast_widget_medium_light
                            .asRight()
                    },
                    { theme: String ->
                        when (theme) {
                            DARK_THEME -> R.layout.weather_forecast_widget_medium_dark
                                .asRight()
                            LIGHT_THEME_NO_BACKGROUND -> R.layout.weather_forecast_widget_medium_light_transparent
                                .asRight()
                            else -> R.layout.weather_forecast_widget_medium_light
                                .asRight()
                        }
                    }
                )
            }
}