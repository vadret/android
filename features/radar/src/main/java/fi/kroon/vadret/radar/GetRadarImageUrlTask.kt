package fi.kroon.vadret.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetRadarImageUrlTask @Inject constructor(
    private val repo: RadarRepository
) {
    suspend operator fun invoke(radarRequest: RadarRequest): Either<Failure, Radar> =
        repo(radarRequest)
}