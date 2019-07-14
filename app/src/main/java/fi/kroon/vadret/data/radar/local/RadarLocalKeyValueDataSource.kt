package fi.kroon.vadret.data.radar.local

import com.afollestad.rxkprefs.RxkPrefs
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.LAST_CHECKED_RADAR_KEY
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single
import javax.inject.Inject

class RadarLocalKeyValueDataSource @Inject constructor(
    private val rxkPrefs: RxkPrefs,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {

    fun putLong(key: String, value: Long): Single<Either<Failure, Unit>> =
        when (key) {
            LAST_CHECKED_RADAR_KEY -> {
                rxkPrefs.long(key = LAST_CHECKED_RADAR_KEY)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            else -> getLocalKeyValueWriteError(key = key, value = value)
        }

    fun getLong(key: String): Single<Either<Failure, Long>> =
        when (key) {
            LAST_CHECKED_RADAR_KEY -> {
                rxkPrefs.long(
                    key = LAST_CHECKED_RADAR_KEY,
                    defaultValue = System.currentTimeMillis()
                ).get()
                    .asRight()
                    .asSingle()
            }
            else -> getLocalKeyValueReadError(key = key)
        }
}