package fi.kroon.vadret.radar

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.local.RadarLocalKeyValueDataSource
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetRadarLastCheckedKeyValueTask @Inject constructor(
    private val local: RadarLocalKeyValueDataSource
) {
    suspend operator fun invoke(value: Long): Either<Failure, Unit> =
        local
            .putLong(
                key = LAST_CHECKED_RADAR_KEY,
                value = value
            )
}