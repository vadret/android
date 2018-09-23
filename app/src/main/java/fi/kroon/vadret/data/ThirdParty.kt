package fi.kroon.vadret.data

data class ThirdParty(

    /**
     *  ThirdParty class for external libraries
     *  used within this project.
     */

    val author: String,
    val title: String,
    val page: String,
    val source: String,
    val license: String,
    val description: String
)