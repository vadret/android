package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.model.Location
import io.github.sphrak.either.Either

internal interface GetDeviceLocationTask {

    suspend operator fun invoke(): Either<Failure, Location>
}