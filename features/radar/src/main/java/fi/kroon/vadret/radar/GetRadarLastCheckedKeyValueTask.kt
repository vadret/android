package fi.kroon.vadret.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.local.RadarLocalKeyValueDataSource
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetRadarLastCheckedKeyValueTask @Inject constructor(
    private val local: RadarLocalKeyValueDataSource
) {
    suspend operator fun invoke(): Either<Failure, Long> =
        local.getLong(key = LAST_CHECKED_RADAR_KEY)
}