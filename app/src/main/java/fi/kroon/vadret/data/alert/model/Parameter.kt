package fi.kroon.vadret.data.alert.model

import java.io.Serializable

data class Parameter(
    val valueName: String,
    val value: String
) : Serializable