package fi.kroon.vadret.testing

import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlin.time.Instant

class FixedClock(
    @Volatile
    var now: Instant
) : Clock {
    override fun now(): Instant = now
}