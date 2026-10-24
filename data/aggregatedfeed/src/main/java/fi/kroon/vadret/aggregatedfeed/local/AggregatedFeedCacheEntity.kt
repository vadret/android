package fi.kroon.vadret.aggregatedfeed.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AggregatedFeedCacheEntity(
    @SerialName(value = "fetched_at_epoch_millis")
    val fetchedAtEpochMillis: Long,
    @SerialName(value = "feed_sources")
    val feedSources: List<String>,
    @SerialName(value = "district_ids")
    val districtIds: List<Int>,
    @SerialName(value = "items")
    val items: List<AggregatedFeedEntity>
)