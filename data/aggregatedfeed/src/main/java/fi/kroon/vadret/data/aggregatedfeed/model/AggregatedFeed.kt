package fi.kroon.vadret.data.aggregatedfeed.model

import fi.kroon.vadret.util.extension.empty
import java.io.Serializable

/**
 *  A warning or news item from any feed source,
 *  see [fi.kroon.vadret.data.aggregatedfeed.mapper.AggregatedFeedMapper].
 */
data class AggregatedFeed(
    val identifier: String,
    val pushMessage: String,
    val updated: String,
    val published: String,
    val headline: String,
    val preamble: String,
    val bodyText: String,
    val areaList: List<Area>,
    val web: String? = String.empty(),
    val language: String,
    val event: String,
    val senderName: String? = null,
    val push: Boolean,
    val bodyLinks: List<String>? = emptyList(),
    val sourceId: Int,
    val isVma: Boolean,
    val isTestVma: Boolean
) : Serializable