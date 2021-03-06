package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class SetRadarMemoryCacheTask @Inject constructor(
    private val cache: RadarCacheDataSource
) {
    operator fun invoke(radar: Radar): Single<Either<Failure, Radar>> =
        cache
            .updateMemoryCache(radar)
}