package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetRadarMemoryCacheTask @Inject constructor(
    private val cache: RadarCacheDataSource
) {
    operator fun invoke(): Single<Either<Failure, Radar>> =
        cache
            .getMemoryCache()
}