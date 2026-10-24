package fi.kroon.vadret.autocomplete.extension

import android.content.res.Resources
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.extension.toKnownFailure
import fi.kroon.vadret.common.extension.toUnknownFailure
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.autocomplete")

/**
 *  A missing file, a column the CSV lacks and a coordinate that
 *  is not a number all mean the bundled places could not be read.
 */
internal fun Exception.toFailure(): Failure =
    when (this) {
        is CancellationException -> throw this
        is IOException,
        is Resources.NotFoundException,
        is IllegalArgumentException,
        is IllegalStateException -> toKnownFailure(
            errorCode = ErrorCode.FILE_READ_FAILED,
            logger = logger
        )
        else -> toUnknownFailure(logger = logger)
    }