package fi.kroon.vadret.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class GetRadarMemoryCacheTask @Inject constructor(
    private val cache: RadarCacheDataSource
) {
    operator fun invoke(): Single<Either<Failure, Radar>> =
        cache
            .getMemoryCache()
}