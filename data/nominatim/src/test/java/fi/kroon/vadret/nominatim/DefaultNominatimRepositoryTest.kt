package fi.kroon.vadret.nominatim

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.nominatim.remote.DefaultNominatimRemoteDataSource
import fi.kroon.vadret.testing.mockHttpClient
import fi.kroon.vadret.testing.withRecordedLogs
import io.github.sphrak.either.Either
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals

private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

class DefaultNominatimRepositoryTest {

    private val requests = mutableListOf<HttpRequestData>()

    private fun repository(handler: MockRequestHandler): NominatimRepository =
        DefaultNominatimRepository(
            nominatimRemoteDataSource = DefaultNominatimRemoteDataSource(
                httpClient = lazy {
                    mockHttpClient { request: HttpRequestData ->
                        requests.add(request)
                        handler(request)
                    }
                }
            )
        )

    private fun respondingWith(body: String): NominatimRepository =
        repository { respond(content = body, headers = jsonHeaders) }

    @Test
    fun `the reverse lookup asks for the coordinates at city zoom`() = runTest {
        respondingWith(body = """{"address":{"city":"Göteborg"}}""").getLocality(
            latitude = 57.7089,
            longitude = 11.9746
        )

        val url = requests.single().url
        assertEquals(expected = "https://nominatim.openstreetmap.org/reverse", actual = url.toString().substringBefore('?'))
        assertEquals(
            expected = mapOf("format" to "json", "lat" to "57.7089", "lon" to "11.9746", "zoom" to "16"),
            actual = url.parameters.names().associateWith { name: String -> url.parameters[name] }
        )
    }

    @Test
    fun `the most specific place in the address names the locality`() = runTest {
        val bodies: Map<String, String?> = mapOf(
            """{"address":{"city":"Göteborg","hamlet":"Lilla","village":"By"}}""" to "Göteborg",
            """{"address":{"hamlet":"Lilla","village":"By"}}""" to "Lilla",
            """{"address":{"village":"By","road":"Storgatan"}}""" to "By",
            """{"address":{"road":"Storgatan"}}""" to null,
            """{"error":"Unable to geocode"}""" to null
        )

        val actual: Map<String, Either<Failure, Locality>> = bodies.keys.associateWith { body: String ->
            respondingWith(body = body).getLocality(
                latitude = 57.0,
                longitude = 12.0
            )
        }

        assertEquals(
            expected = bodies.mapValues { (_, name: String?) -> Either.Right(b = Locality(name = name)) },
            actual = actual
        )
    }

    @Test
    fun `http and network failures are told apart`() = runTest {
        withRecordedLogs {
            val forbidden: Either<Failure, Locality> = repository { respond(content = "", status = HttpStatusCode.Forbidden) }
                .getLocality(latitude = 57.0, longitude = 12.0)
            val offline: Either<Failure, Locality> = repository { throw UnknownHostException("nominatim.openstreetmap.org") }
                .getLocality(latitude = 57.0, longitude = 12.0)

            assertEquals(expected = Either.Left(a = Failure(errorCode = ErrorCode.HTTP_FORBIDDEN)), actual = forbidden)
            assertEquals(expected = Either.Left(a = Failure(errorCode = ErrorCode.NETWORK_UNAVAILABLE)), actual = offline)
        }
    }
}