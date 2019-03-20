package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SetRadarDiskCacheTask @Inject constructor(
    private val repo: RadarCacheDataSource
) {

    operator fun invoke(radar: Radar): Single<Either<Failure, Radar>> =
        repo
            .updateDiskCache(radar)
            .doOnError {
                Timber.e("$it")
            }
}