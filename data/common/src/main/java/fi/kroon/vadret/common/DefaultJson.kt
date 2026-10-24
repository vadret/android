package fi.kroon.vadret.common

import kotlinx.serialization.json.Json

/**
 *  For API responses and cache entries alike. Unknown keys are
 *  ignored and missing nullable fields become null.
 */
val defaultJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}