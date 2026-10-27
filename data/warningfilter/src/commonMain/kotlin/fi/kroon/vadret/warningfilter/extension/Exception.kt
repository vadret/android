package fi.kroon.vadret.warningfilter.extension

import androidx.sqlite.SQLiteException
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.extension.toKnownFailure
import fi.kroon.vadret.common.extension.toNetworkFailure
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import fi.kroon.vadret.warningfilter.exception.DistrictsNotAvailableException
import kotlin.coroutines.cancellation.CancellationException

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.warningfilter")

internal fun Exception.toFailure(): Failure =
    when (this) {
        is CancellationException -> throw this

        is DistrictsNotAvailableException -> toKnownFailure(
            errorCode = ErrorCode.DISTRICTS_NOT_AVAILABLE,
            logger = logger
        )

        is SQLiteException -> toKnownFailure(
            errorCode = ErrorCode.STORAGE_FAILED,
            logger = logger
        )

        else -> toNetworkFailure(logger = logger)
    }