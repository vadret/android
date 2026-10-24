package fi.kroon.vadret.testing

import fi.kroon.vadret.common.defaultJson
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

/**
 *  A client configured like the app's, answering with [handler]
 *  instead of the network.
 */
fun mockHttpClient(handler: MockRequestHandler): HttpClient =
    HttpClient(MockEngine(handler)) {
        install(ContentNegotiation) {
            json(json = defaultJson)
        }
    }