package fi.kroon.vadret.location.extension

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.extension.toKnownFailure
import fi.kroon.vadret.common.extension.toUnknownFailure
import fi.kroon.vadret.location.exception.LocationNotAvailableException
import fi.kroon.vadret.location.exception.LocationProviderDisabledException
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.coroutines.cancellation.CancellationException

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.location")

/**
 *  A stored coordinate that is not a number counts as broken storage.
 */
internal fun Exception.toFailure(): Failure =
    when (this) {
        is CancellationException -> throw this
        is LocationProviderDisabledException -> toKnownFailure(
            errorCode = ErrorCode.LOCATION_PROVIDER_DISABLED,
            logger = logger
        )
        is LocationNotAvailableException -> toKnownFailure(
            errorCode = ErrorCode.LOCATION_NOT_AVAILABLE,
            logger = logger
        )
        is SecurityException -> toKnownFailure(
            errorCode = ErrorCode.LOCATION_PERMISSION_MISSING,
            logger = logger
        )
        is NumberFormatException -> toKnownFailure(
            errorCode = ErrorCode.STORAGE_FAILED,
            logger = logger
        )
        else -> toUnknownFailure(logger = logger)
    }