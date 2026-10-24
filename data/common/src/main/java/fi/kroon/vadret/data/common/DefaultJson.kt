package fi.kroon.vadret.data.common

import kotlinx.serialization.json.Json

/**
 *  Shared parser configuration. Like Moshi before it, unknown
 *  keys are ignored and missing nullable fields become null.
 */
val defaultJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}