package fi.kroon.vadret.data.library.local

data class LibraryEntity(

    /**
     *  LibraryEntity class for external libraries
     *  used within this project.
     */

    val author: String,
    val title: String,
    val projectUrl: String,
    val sourceUrl: String,
    val licenseUrl: String,
    val description: String
)