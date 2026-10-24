package fi.kroon.vadret.aggregatedfeed.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  @param source the name of a [fi.kroon.vadret.aggregatedfeed.model.FeedSource].
 */
@Serializable
internal data class AggregatedFeedEntity(
    @SerialName(value = "identifier")
    val identifier: String,
    @SerialName(value = "headline")
    val headline: String,
    @SerialName(value = "preamble")
    val preamble: String,
    @SerialName(value = "body_text")
    val bodyText: String,
    @SerialName(value = "source")
    val source: String,
    @SerialName(value = "published_at_epoch_millis")
    val publishedAtEpochMillis: Long
)