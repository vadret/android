package fi.kroon.vadret.domain.weatherforecastwidget.medium

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetThemeKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetUpdateIntervalKeyValueTask
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.model.WeatherForecastMediumConfigurationModel
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetMediumConfigurationService @Inject constructor(
    private val setWidgetThemeKeyValueTask: SetWidgetThemeKeyValueTask,
    private val setWidgetUpdateIntervalKeyValueTask: SetWidgetUpdateIntervalKeyValueTask,
    private val setWidgetForecastFormatKeyValueTask: SetWidgetForecastFormatKeyValueTask
) {
    operator fun invoke(config: WeatherForecastMediumConfigurationModel): Single<Either<Failure, Unit>> = with(config) {

        val stepSize: Int = getStepSize(forecastFormat)

        Observable.mergeArray(
            setWidgetThemeKeyValueTask(appWidgetId = appWidgetId, value = theme)
                .toObservable(),
            setWidgetUpdateIntervalKeyValueTask(appWidgetId = appWidgetId, value = updateInterval)
                .toObservable(),
            setWidgetForecastFormatKeyValueTask(appWidgetId = appWidgetId, value = stepSize)
                .toObservable()
        ).toList()
            .flatMapObservable { eitherList: List<Either<Failure, Unit>> ->
                Observable.fromIterable(eitherList)
                    .scan { previous, next ->
                        when {
                            previous.isLeft -> previous
                            next.isLeft -> next
                            else -> next
                        }
                    }.takeLast(1)
            }.singleOrError()
    }

    private fun getStepSize(pos: Int): Int = when (pos) {
        0 -> 1
        1 -> 2
        2 -> 3
        3 -> 6
        4 -> 12
        else -> 1
    }
}