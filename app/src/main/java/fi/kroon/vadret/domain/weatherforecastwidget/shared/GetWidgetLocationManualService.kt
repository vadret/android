package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.flatMap
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.toCoordinate
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import javax.inject.Inject

class GetWidgetLocationManualService @Inject constructor(
    private val getWidgetLatitudeKeyValueTask: GetWidgetLatitudeKeyValueTask,
    private val getWidgetLongitudeKeyValueTask: GetWidgetLongitudeKeyValueTask,
    private val getWidgetLocalityKeyValueTask: GetWidgetLocalityKeyValueTask
) {
    operator fun invoke(appWidgetId: Int): Single<Either<Failure, WeatherOut>> =
        Singles.zip(
            getWidgetLatitudeKeyValueTask(appWidgetId),
            getWidgetLongitudeKeyValueTask(appWidgetId),
            getWidgetLocalityKeyValueTask(appWidgetId)
        ).map { triple: Triple<Either<Failure, String>, Either<Failure, String>, Either<Failure, String>> ->
            val (latitudeEither: Either<Failure, String>, longitudeEither: Either<Failure, String>, localityEither: Either<Failure, String>) = triple

            latitudeEither.flatMap { latitude: String ->
                longitudeEither.flatMap { longitude: String ->
                    localityEither.flatMap { locality: String ->
                        WeatherOut(
                            localityName = locality,
                            latitude = latitude.toCoordinate(),
                            longitude = longitude.toCoordinate()
                        ).asRight()
                    }
                }
            }
        }
}