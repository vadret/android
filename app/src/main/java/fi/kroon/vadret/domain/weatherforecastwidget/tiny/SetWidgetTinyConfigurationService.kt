package fi.kroon.vadret.domain.weatherforecastwidget.tiny

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetThemeKeyValueTask
import fi.kroon.vadret.domain.weatherforecastwidget.shared.SetWidgetUpdateIntervalKeyValueTask
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.model.WeatherForecastTinyConfigurationModel
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetTinyConfigurationService @Inject constructor(
    private val setWidgetThemeKeyValueTask: SetWidgetThemeKeyValueTask,
    private val setWidgetUpdateIntervalKeyValueTask: SetWidgetUpdateIntervalKeyValueTask
) {
    operator fun invoke(config: WeatherForecastTinyConfigurationModel): Single<Either<Failure, Unit>> = with(config) {
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