package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.weatherforecast.model.LocationChoices
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

internal interface ObserveLocationChoicesTask {

    operator fun invoke(): Flow<Either<Failure, LocationChoices>>
}