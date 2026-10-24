package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *  A news item or VMA from Krisinformation's v3 API.
 */
@JsonClass(generateAdapter = true)
data class KrisinformationFeed(

    @Json(name = "identifier")
    val identifier: String,

    @Json(name = "updated")
    val updated: String?,

    @Json(name = "published")
    val published: String,

    @Json(name = "headline")
    val headline: String,

    @Json(name = "preamble")
    val preamble: String?,

    @Json(name = "bodyText")
    val bodyText: String?,

    @Json(name = "area")
    val areaList: List<KrisinformationArea>?,

    @Json(name = "links")
    val links: List<KrisinformationLink>?,

    @Json(name = "web")
    val web: String?,

    @Json(name = "language")
    val language: String?,

    @Json(name = "event")
    val event: String?
)