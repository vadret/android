package fi.kroon.vadret.data.radar.local

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.preference.Prefs
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class RadarLocalKeyValueDataSource @Inject constructor(
    private val prefs: Prefs,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {

    fun putLong(key: String, value: Long): Either<Failure, Unit> =
        when (key) {
            LAST_CHECKED_RADAR_KEY -> {
                prefs.long(key = LAST_CHECKED_RADAR_KEY)
                    .set(value = value)
                    .asRight()
            }
            else -> getLocalKeyValueWriteError(key = key, value = value)
        }

    fun getLong(key: String): Either<Failure, Long> =
        when (key) {
            LAST_CHECKED_RADAR_KEY -> {
                prefs.long(
                    key = LAST_CHECKED_RADAR_KEY,
                    defaultValue = System.currentTimeMillis()
                ).get()
                    .asRight()
            }
            else -> getLocalKeyValueReadError(key = key)
        }
}