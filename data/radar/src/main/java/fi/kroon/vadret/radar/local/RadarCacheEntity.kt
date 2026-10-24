package fi.kroon.vadret.radar.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  @param date the UTC day the images are for, as ISO 8601.
 */
@Serializable
internal data class RadarCacheEntity(
    @SerialName(value = "fetched_at_epoch_millis")
    val fetchedAtEpochMillis: Long,
    @SerialName(value = "date")
    val date: String,
    @SerialName(value = "images")
    val images: List<RadarImageEntity>
)