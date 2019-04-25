package fi.kroon.vadret.domain.weatherforecastwidget.small

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetThemeKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetUpdateIntervalKeyValueTask
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.model.WeatherForecastSmallConfigurationModel
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetSmallConfigurationService @Inject constructor(
    private val setWidgetThemeKeyValueTask: SetWidgetThemeKeyValueTask,
    private val setWidgetUpdateIntervalKeyValueTask: SetWidgetUpdateIntervalKeyValueTask

) {
    operator fun invoke(config: WeatherForecastSmallConfigurationModel): Single<Either<Failure, Unit>> = with(config) {
        Observable.mergeArray(
            setWidgetThemeKeyValueTask(appWidgetId = appWidgetId, value = theme)
                .toObservable(),
            setWidgetUpdateIntervalKeyValueTask(appWidgetId = appWidgetId, value = updateInterval)
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
}