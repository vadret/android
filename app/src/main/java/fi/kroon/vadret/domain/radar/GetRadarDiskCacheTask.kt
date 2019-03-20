package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetRadarDiskCacheTask @Inject constructor(
    private val repo: RadarCacheDataSource
) {
    operator fun invoke(): Single<Either<Failure, Radar>> =
        repo
            .getDiskCache()
            .doOnError {
                Timber.e("$it")
            }
}