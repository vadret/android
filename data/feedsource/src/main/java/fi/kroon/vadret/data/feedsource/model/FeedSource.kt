package fi.kroon.vadret.data.feedsource.model

data class FeedSource(
    val id: Int,
    val name: String,
    val url: String?,
    val description: String,
    val type: Int,
    val headLineRowLimit: Int
)