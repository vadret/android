package fi.kroon.vadret.common.extension

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.Logger
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.ContentConvertException
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.UnknownHostException

/**
 *  The error code every API call shares, null when this is not a
 *  network failure. A body that is not the JSON expected, such as a
 *  captive portal's login page, is an invalid response.
 */
fun Exception.networkErrorCode(): ErrorCode? =
    when (this) {
        is ResponseException -> response.status.value.toHttpErrorCode()
        is UnknownHostException -> ErrorCode.NETWORK_UNAVAILABLE
        is IOException -> ErrorCode.NETWORK_FAILURE
        is ContentConvertException,
        is NoTransformationFoundException,
        is SerializationException -> ErrorCode.INVALID_RESPONSE
        else -> null
    }

/**
 *  For failures a module expects, logged without the stack trace.
 */
fun Exception.toKnownFailure(
    errorCode: ErrorCode,
    logger: Logger
): Failure {
    logger.warn { "$errorCode: ${javaClass.simpleName}: $message" }
    return Failure(errorCode = errorCode)
}

fun Exception.toUnknownFailure(logger: Logger): Failure {
    logger.error { stackTraceToString() }
    return Failure.unknown()
}

/**
 *  A network failure when it is one, otherwise unknown.
 */
fun Exception.toNetworkFailure(logger: Logger): Failure {
    val errorCode: ErrorCode? = networkErrorCode()
    return if (errorCode != null) {
        toKnownFailure(
            errorCode = errorCode,
            logger = logger
        )
    } else {
        toUnknownFailure(logger = logger)
    }
}