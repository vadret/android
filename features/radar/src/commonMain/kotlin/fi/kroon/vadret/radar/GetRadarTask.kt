package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.model.Radar
import io.github.sphrak.either.Either

internal interface GetRadarTask {
    suspend operator fun invoke(imageUrl: String?): Either<Failure, Radar>
}