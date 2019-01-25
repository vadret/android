package fi.kroon.vadret.data.library.model

data class Library(

    /**
     *  Library class for external libraries
     *  used within this project.
     */

    val author: String,
    val title: String,
    val projectUrl: String,
    val sourceUrl: String,
    val licenseUrl: String,
    val license: String,
    val description: String
)