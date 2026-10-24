package fi.kroon.vadret.radar.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  [valid] is when the image applies, as "2026-10-24 14:05" in UTC.
 */
@Serializable
internal data class RadarFileDto(
    @SerialName(value = "key")
    val key: String,
    @SerialName(value = "valid")
    val valid: String,
    @SerialName(value = "formats")
    val formats: List<RadarFormatDto>
)