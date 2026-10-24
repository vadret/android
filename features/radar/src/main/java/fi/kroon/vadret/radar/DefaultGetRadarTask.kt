package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.model.Radar
import io.github.sphrak.either.Either
import javax.inject.Inject

internal class DefaultGetRadarTask @Inject constructor(
    private val radarRepository: RadarRepository
) : GetRadarTask {

    override suspend fun invoke(): Either<Failure, Radar> =
        radarRepository.getRadar()
}