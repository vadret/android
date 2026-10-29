package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.radar.model.Radar
import io.github.sphrak.either.Either

internal class DefaultGetRadarTask(
    private val radarRepository: RadarRepository
) : GetRadarTask {

    override suspend fun invoke(imageUrl: String?): Either<Failure, Radar> =
        if (imageUrl == null) {
            radarRepository.getRadar()
        } else {
            radarRepository.getRadarContaining(imageUrl = imageUrl)
        }
}