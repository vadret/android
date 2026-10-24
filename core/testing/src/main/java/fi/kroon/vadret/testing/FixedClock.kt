package fi.kroon.vadret.testing

import kotlin.time.Clock
import kotlin.time.Instant

/**
 *  A clock that stays at [now] until a test moves it.
 */
class FixedClock(
    @Volatile
    var now: Instant
) : Clock {
    override fun now(): Instant = now
}