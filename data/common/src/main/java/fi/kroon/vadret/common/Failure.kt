package fi.kroon.vadret.common

/**
 *  What went wrong, for the screen to turn into a message.
 */
data class Failure(val errorCode: ErrorCode) {

    companion object {
        fun unknown(): Failure = Failure(errorCode = ErrorCode.UNKNOWN)
    }
}