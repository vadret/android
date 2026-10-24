package fi.kroon.vadret.aggregatedfeed.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  A news item or VMA from Krisinformation's v3 API.
 */
@Serializable
internal data class KrisinformationFeedDto(
    @SerialName(value = "identifier")
    val identifier: String,
    @SerialName(value = "published")
    val published: String,
    @SerialName(value = "headline")
    val headline: String,
    @SerialName(value = "preamble")
    val preamble: String? = null,
    @SerialName(value = "bodyText")
    val bodyText: String? = null
)