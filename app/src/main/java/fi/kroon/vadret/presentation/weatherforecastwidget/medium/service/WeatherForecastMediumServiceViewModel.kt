package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.weatherforecastwidget.medium.GetWidgetForecastFormatKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetLastCheckedKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.GetWidgetWeatherForecastService
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetLastCheckedKeyValueTask
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.di.WeatherForecastMediumServiceScope
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.model.WeatherForecastMediumServiceModel
import fi.kroon.vadret.util.extension.asObservable
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import javax.inject.Inject
import timber.log.Timber

@WeatherForecastMediumServiceScope
class WeatherForecastMediumServiceViewModel @Inject constructor(
    private var state: WeatherForecastMediumServiceView.State,
    private val getWidgetWeatherForecastService: GetWidgetWeatherForecastService,
    private val getWidgetLastCheckedKeyValueTask: GetWidgetLastCheckedKeyValueTask,
    private val setWidgetLastCheckedKeyValueTask: SetWidgetLastCheckedKeyValueTask,
    private val getWidgetForecastFormatKeyValueTask: GetWidgetForecastFormatKeyValueTask
) {
    operator fun invoke(): ObservableTransformer<WeatherForecastMediumServiceView.Event,
        WeatherForecastMediumServiceView.State> = onEvent

    private val onEvent = ObservableTransformer<WeatherForecastMediumServiceView.Event, WeatherForecastMediumServiceView.State> { upstream: Observable<WeatherForecastMediumServiceView.Event> ->
        upstream.publish { shared: Observable<WeatherForecastMediumServiceView.Event> ->
            Observable.mergeArray(
                shared.ofType(WeatherForecastMediumServiceView.Event.OnInitialised::class.java)
            ).compose(
                eventToViewState
            )
        }
    }

    private val eventToViewState = ObservableTransformer<WeatherForecastMediumServiceView.Event, WeatherForecastMediumServiceView.State> { upstream ->
        upstream.flatMap { event: WeatherForecastMediumServiceView.Event ->
            when (event) {
                is WeatherForecastMediumServiceView.Event.OnInitialised -> onInitialisedEvent(event.appWidgetId)
            }
        }
    }

    private fun onInitialisedEvent(appWidgetId: Int): Observable<WeatherForecastMediumServiceView.State> =
        getWidgetLastCheckedKeyValueTask(appWidgetId = appWidgetId)
            .flatMapObservable { result: Either<Failure, Long> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    { timeStamp: Long ->

                        Timber.d("ON INITIALISED EVENT: $appWidgetId")
                        getWidgetForecastFormatKeyValueTask(appWidgetId = appWidgetId)
                            .flatMapObservable { result: Either<Failure, Int> ->
                                result.either(
                                    { failure: Failure ->
                                        Timber.e("Failure: $failure")
                                        state.asObservable()
                                    },
                                    { stepSize: Int ->
                                        getWidgetWeatherForecastService(
                                            timeStamp = timeStamp,
                                            forceNet = state.forceNet,
                                            appWidgetId = appWidgetId
                                        ).flatMapObservable { result: Either<Failure, GetWidgetWeatherForecastService.Data> ->
                                            result.either(
                                                { failure: Failure ->
                                                    Timber.e("Failure: $failure")
                                                    state.asObservable()
                                                },
                                                { data: GetWidgetWeatherForecastService.Data ->
                                                    val weatherForecastMediumServiceModelList: List<WeatherForecastMediumServiceModel> =
                                                        WeatherForecastMediumServiceMapper(
                                                            timeSerieList = data.weather!!.timeSeries!!,
                                                            stepSize = stepSize
                                                        )

                                                    val renderEvent = WeatherForecastMediumServiceView
                                                        .RenderEvent
                                                        .UpdateWeatherForecastList(weatherForecastMediumServiceModelList = weatherForecastMediumServiceModelList)

                                                    state = state.copy(renderEvent = renderEvent)
                                                    updateLastChecked(
                                                        appWidgetId = appWidgetId,
                                                        timeStamp = data.timeStamp
                                                    )
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                    }
                )
            }

    private fun updateLastChecked(appWidgetId: Int, timeStamp: Long): Observable<WeatherForecastMediumServiceView.State> =
        setWidgetLastCheckedKeyValueTask(appWidgetId = appWidgetId, value = timeStamp)
            .flatMapObservable { result: Either<Failure, Unit> ->
                result.either(
                    { failure: Failure ->
                        Timber.e("Failure: $failure")
                        state.asObservable()
                    },
                    {
                        Timber.d("LAST CHECKED UPDATED")
                        state.asObservable()
                    }
                )
            }
}