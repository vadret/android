package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetRadarImageUrlTask @Inject constructor(
    private val repo: RadarRepository
) {
    operator fun invoke(radarRequest: RadarRequest): Single<Either<Failure, Radar>> =
        repo(radarRequest)
            .doOnError {
                Timber.e("$it")
            }
}