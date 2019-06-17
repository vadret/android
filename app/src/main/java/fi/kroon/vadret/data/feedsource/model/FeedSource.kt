package fi.kroon.vadret.data.feedsource.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedSource(

    @Json(name = "Id")
    val id: Int,

    @Json(name = "Name")
    val name: String,

    @Json(name = "Url")
    val url: String?,

    @Json(name = "Description")
    val description: String,

    @Json(name = "Type")
    val type: Int,

    @Json(name = "HeadlineRowLimit")
    val headLineRowLimit: Int

)