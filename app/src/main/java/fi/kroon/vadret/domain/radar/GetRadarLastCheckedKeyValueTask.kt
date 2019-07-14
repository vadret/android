package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.radar.local.RadarLocalKeyValueDataSource
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import io.reactivex.Single
import javax.inject.Inject

class GetRadarLastCheckedKeyValueTask @Inject constructor(
    private val local: RadarLocalKeyValueDataSource
) {
    operator fun invoke(): Single<Either<Failure, Long>> =
        local.getLong(key = LAST_CHECKED_RADAR_KEY)
}