package fi.kroon.vadret.util.extension

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

/**
 *  The parsed body of a successful response, null otherwise.
 */
suspend inline fun <reified T> HttpResponse.bodyOrNull(): T? =
    if (status.isSuccess()) {
        body<T>()
    } else {
        null
    }