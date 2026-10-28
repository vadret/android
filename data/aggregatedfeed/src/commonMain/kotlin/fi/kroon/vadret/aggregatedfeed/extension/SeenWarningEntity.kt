package fi.kroon.vadret.aggregatedfeed.extension

import fi.kroon.vadret.aggregatedfeed.local.SeenWarningEntity
import fi.kroon.vadret.aggregatedfeed.model.SeenWarning
import kotlin.time.Instant

internal fun SeenWarningEntity.toSeenWarning(): SeenWarning =
    SeenWarning(
        warningIdentifier = warningIdentifier,
        seenAt = Instant.fromEpochMilliseconds(epochMilliseconds = seenAtEpochMillis)
    )