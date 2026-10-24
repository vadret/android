package fi.kroon.vadret.theme.extension

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.extension.toUnknownFailure
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import kotlin.coroutines.cancellation.CancellationException

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.theme")

/**
 *  Preferences fail in no way this module expects.
 */
internal fun Exception.toFailure(): Failure =
    when (this) {
        is CancellationException -> throw this
        else -> toUnknownFailure(logger = logger)
    }