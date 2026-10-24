package fi.kroon.vadret.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetRadarDiskCacheTask @Inject constructor(
    private val cache: RadarCacheDataSource
) {
    suspend operator fun invoke(radar: Radar): Either<Failure, Radar> =
        cache
            .updateDiskCache(radar)
}