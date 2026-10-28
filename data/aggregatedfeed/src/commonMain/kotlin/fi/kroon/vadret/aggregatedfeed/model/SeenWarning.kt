package fi.kroon.vadret.aggregatedfeed.model

import kotlin.time.Instant

data class SeenWarning(
    val warningIdentifier: String,
    val seenAt: Instant
)