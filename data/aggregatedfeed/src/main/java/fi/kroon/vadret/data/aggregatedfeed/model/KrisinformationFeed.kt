package fi.kroon.vadret.data.aggregatedfeed.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *  A news item or VMA from Krisinformation's v3 API.
 */
@Serializable
data class KrisinformationFeed(

    @SerialName("identifier")
    val identifier: String,

    @SerialName("updated")
    val updated: String?,

    @SerialName("published")
    val published: String,

    @SerialName("headline")
    val headline: String,

    @SerialName("preamble")
    val preamble: String?,

    @SerialName("bodyText")
    val bodyText: String?,

    @SerialName("area")
    val areaList: List<KrisinformationArea>?,

    @SerialName("links")
    val links: List<KrisinformationLink>?,

    @SerialName("web")
    val web: String?,

    @SerialName("language")
    val language: String?,

    @SerialName("event")
    val event: String?
)