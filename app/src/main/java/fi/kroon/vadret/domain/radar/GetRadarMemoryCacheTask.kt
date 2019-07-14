package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import io.reactivex.Single
import javax.inject.Inject

class GetRadarMemoryCacheTask @Inject constructor(
    private val cache: RadarCacheDataSource
) {
    operator fun invoke(): Single<Either<Failure, Radar>> =
        cache
            .getMemoryCache()
}