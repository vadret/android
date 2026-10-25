package fi.kroon.vadret.aggregatedfeed.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AggregatedFeedEntity(
    @SerialName(value = "identifier")
    val identifier: String,
    @SerialName(value = "headline")
    val headline: String,
    @SerialName(value = "area_name")
    val areaName: String? = null,
    @SerialName(value = "warning_level")
    val warningLevel: String? = null,
    @SerialName(value = "details")
    val details: List<FeedDetailEntity>,
    @SerialName(value = "source")
    val source: String,
    @SerialName(value = "published_at_epoch_millis")
    val publishedAtEpochMillis: Long
)