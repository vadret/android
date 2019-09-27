package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetLocationInformationService @Inject constructor(
    private val setWidgetLatitudeKeyValueTask: SetWidgetLatitudeKeyValueTask,
    private val setWidgetLongitudeKeyValueTask: SetWidgetLongitudeKeyValueTask,
    private val setWidgetLocalityKeyValueTask: SetWidgetLocalityKeyValueTask,
    private val setWidgetCountyKeyValueTask: SetWidgetCountyKeyValueTask,
    private val setWidgetMunicipalityKeyValueTask: SetWidgetMunicipalityKeyValueTask
) {
    operator fun invoke(appWidgetId: Int, autoCompleteItem: AutoCompleteItem): Single<Either<Failure, Unit>> =
        with(autoCompleteItem) {
            Observable.mergeArray(
                setWidgetLatitudeKeyValueTask(appWidgetId = appWidgetId, value = latitude.toString())
                    .toObservable(),
                setWidgetLongitudeKeyValueTask(appWidgetId = appWidgetId, value = longitude.toString())
                    .toObservable(),
                setWidgetLocalityKeyValueTask(appWidgetId = appWidgetId, value = locality)
                    .toObservable(),
                setWidgetCountyKeyValueTask(appWidgetId = appWidgetId, value = county)
                    .toObservable(),
                setWidgetMunicipalityKeyValueTask(appWidgetId = appWidgetId, value = municipality)
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