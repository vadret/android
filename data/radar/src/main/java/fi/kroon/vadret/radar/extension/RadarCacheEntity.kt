package fi.kroon.vadret.radar.extension

import fi.kroon.vadret.radar.local.RadarCacheEntity
import fi.kroon.vadret.radar.local.RadarImageEntity
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarImage
import kotlin.time.Instant

internal fun RadarCacheEntity.toRadar(): Radar =
    Radar(
        images = images.map { radarImageEntity: RadarImageEntity ->
            RadarImage(
                imageUrl = radarImageEntity.imageUrl,
                validAt = Instant.fromEpochMilliseconds(epochMilliseconds = radarImageEntity.validAtEpochMillis)
            )
        }
    )