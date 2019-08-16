package fi.kroon.vadret.data.aggregatedfeed.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import fi.kroon.vadret.util.extension.empty
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class AggregatedFeed(

    @Json(name = "Identifier")
    val identifier: String,

    @Json(name = "PushMessage")
    val pushMessage: String,

    @Json(name = "Updated")
    val updated: String,

    @Json(name = "Published")
    val published: String,

    @Json(name = "Headline")
    val headline: String,

    @Json(name = "Preamble")
    val preamble: String,

    @Json(name = "BodyText")
    val bodyText: String,

    @Json(name = "Area")
    val areaList: List<Area>,

    @Json(name = "Web")
    val web: String? = String.empty(),

    @Json(name = "Language")
    val language: String,

    @Json(name = "Event")
    val event: String,

    @Json(name = "SenderName")
    val senderName: String? = null,

    @Json(name = "Push")
    val push: Boolean,

    @Json(name = "BodyLinks")
    val bodyLinks: List<String>? = emptyList(),

    @Json(name = "SourceID")
    val sourceId: Int,

    @Json(name = "IsVma")
    val isVma: Boolean,

    @Json(name = "IsTestVma")
    val isTestVma: Boolean

) : Serializable