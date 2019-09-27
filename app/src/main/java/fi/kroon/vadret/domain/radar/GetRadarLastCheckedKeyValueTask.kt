package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.local.RadarLocalKeyValueDataSource
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetRadarLastCheckedKeyValueTask @Inject constructor(
    private val local: RadarLocalKeyValueDataSource
) {
    operator fun invoke(): Single<Either<Failure, Long>> =
        local.getLong(key = LAST_CHECKED_RADAR_KEY)
}