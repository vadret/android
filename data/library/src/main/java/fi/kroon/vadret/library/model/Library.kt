package fi.kroon.vadret.library.model

/**
 *  Software or data the app is built with, for the credits.
 */
data class Library(
    val author: String,
    val title: String,
    val description: String,
    val projectUrl: String,
    val sourceUrl: String?,
    val licenseUrl: String,
    val license: String
)