package fi.kroon.vadret.radar

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.defaultJson
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.radar.local.DefaultRadarLocalDataSource
import fi.kroon.vadret.radar.local.RadarCacheEntity
import fi.kroon.vadret.radar.local.RadarImageEntity
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.model.RadarImage
import fi.kroon.vadret.radar.remote.DefaultRadarRemoteDataSource
import fi.kroon.vadret.radar.remote.RadarDto
import fi.kroon.vadret.radar.remote.RadarFileDto
import fi.kroon.vadret.radar.remote.RadarFormatDto
import fi.kroon.vadret.radar.remote.RadarRemoteDataSource
import fi.kroon.vadret.testing.FakeDiskCache
import fi.kroon.vadret.testing.FixedClock
import fi.kroon.vadret.testing.LogLine
import fi.kroon.vadret.testing.mockHttpClient
import fi.kroon.vadret.testing.withRecordedLogs
import io.github.sphrak.either.Either
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private const val CACHE_KEY = "radar.json"

private val now: Instant = Instant.parse(input = "2026-10-24T14:00:00Z")

private class FakeRadarRemoteDataSource(
    private val radarDto: RadarDto
) : RadarRemoteDataSource {

    val requestedDates = mutableListOf<LocalDate>()

    override suspend fun getRadar(date: LocalDate): RadarDto {
        requestedDates.add(date)
        return radarDto
    }
}

private fun file(
    valid: String,
    link: String?
): RadarFileDto =
    RadarFileDto(
        key = "radar_$valid",
        valid = valid,
        formats = if (link != null) {
            listOf(RadarFormatDto(link = link))
        } else {
            emptyList()
        }
    )

class DefaultRadarRepositoryTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val diskCache = FakeDiskCache()
    private val clock = FixedClock(now = now)
    private val radarRemoteDataSource = FakeRadarRemoteDataSource(
        radarDto = RadarDto(
            files = listOf(
                file(valid = "2026-10-24 13:55", link = "https://smhi.se/1355.png"),
                file(valid = "2026-10-24 13:50", link = "https://smhi.se/1350.png"),
                file(valid = "soon", link = "https://smhi.se/soon.png"),
                file(valid = "2026-10-24 13:45", link = null)
            )
        )
    )
    private val radarRepository: RadarRepository = DefaultRadarRepository(
        radarRemoteDataSource = radarRemoteDataSource,
        radarLocalDataSource = DefaultRadarLocalDataSource(
            diskCache = diskCache,
            coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
        ),
        clock = clock
    )

    private val fetchedRadar = Radar(
        images = listOf(
            RadarImage(imageUrl = "https://smhi.se/1350.png", validAt = Instant.parse(input = "2026-10-24T13:50:00Z")),
            RadarImage(imageUrl = "https://smhi.se/1355.png", validAt = Instant.parse(input = "2026-10-24T13:55:00Z"))
        )
    )

    private fun cache(
        fetchedAt: Instant,
        date: String = "2026-10-24",
        images: List<RadarImageEntity> = listOf(
            RadarImageEntity(imageUrl = "https://smhi.se/cached.png", validAtEpochMillis = now.toEpochMilliseconds())
        )
    ) {
        diskCache.write(
            key = CACHE_KEY,
            value = defaultJson.encodeToString(
                value = RadarCacheEntity(
                    fetchedAtEpochMillis = fetchedAt.toEpochMilliseconds(),
                    date = date,
                    images = images
                )
            )
        )
    }

    @Test
    fun `without a cache today's radar is fetched with unreadable files skipped`() = testScope.runTest {
        withRecordedLogs { recordingLogSink ->
            assertEquals<Either<Failure, Radar>>(expected = Either.Right(b = fetchedRadar), actual = radarRepository.getRadar())
            assertEquals(expected = listOf(LocalDate(year = 2026, month = 10, day = 24)), actual = radarRemoteDataSource.requestedDates)
            assertEquals(
                expected = listOf(LogLevel.WARN, LogLevel.WARN),
                actual = recordingLogSink.lines.map { line: LogLine -> line.level }
            )
        }
    }

    @Test
    fun `a fetched radar is served from the cache for fifteen minutes`() = testScope.runTest {
        radarRepository.getRadar()
        clock.now = now + 14.minutes

        assertEquals<Either<Failure, Radar>>(expected = Either.Right(b = fetchedRadar), actual = radarRepository.getRadar())
        assertEquals(expected = 1, actual = radarRemoteDataSource.requestedDates.size)
    }

    @Test
    fun `a stale, empty, future or yesterday's cache is fetched again`() = testScope.runTest {
        val caches: List<() -> Unit> = listOf(
            { cache(fetchedAt = now - 15.minutes) },
            { cache(fetchedAt = now - 1.minutes, images = emptyList()) },
            { cache(fetchedAt = now + 1.minutes) },
            { cache(fetchedAt = now - 1.minutes, date = "2026-10-23") }
        )

        val results: List<Either<Failure, Radar>> = caches.map { writeCache: () -> Unit ->
            writeCache()
            radarRepository.getRadar()
        }

        assertEquals(expected = List(size = caches.size) { Either.Right(b = fetchedRadar) }, actual = results)
        assertEquals(expected = caches.size, actual = radarRemoteDataSource.requestedDates.size)
    }

    @Test
    fun `an unreadable cache is dropped and fetched again`() = testScope.runTest {
        diskCache.write(key = CACHE_KEY, value = "serialized java object")

        withRecordedLogs {
            assertEquals<Either<Failure, Radar>>(expected = Either.Right(b = fetchedRadar), actual = radarRepository.getRadar())
        }
        assertEquals(expected = 1, actual = radarRemoteDataSource.requestedDates.size)
    }

    @Test
    fun `the radar of the UTC day is asked for as png in GMT`() = testScope.runTest {
        val requests = mutableListOf<HttpRequestData>()
        clock.now = Instant.parse(input = "2026-01-05T23:30:00Z")
        val remoteRepository: RadarRepository = DefaultRadarRepository(
            radarRemoteDataSource = DefaultRadarRemoteDataSource(
                httpClient = lazy {
                    mockHttpClient { request: HttpRequestData ->
                        requests.add(request)
                        respond(content = """{"files":[]}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
                    }
                }
            ),
            radarLocalDataSource = DefaultRadarLocalDataSource(
                diskCache = FakeDiskCache(),
                coroutineDispatcher = StandardTestDispatcher(testScheduler)
            ),
            clock = clock
        )

        assertEquals<Either<Failure, Radar>>(expected = Either.Right(b = Radar(images = emptyList())), actual = remoteRepository.getRadar())
        assertEquals(
            expected = "https://opendata-download-radar.smhi.se/api/version/latest/area/sweden/product/comp/2026/1/5?format=png&timeZone=GMT",
            actual = requests.single().url.toString()
        )
    }

    @Test
    fun `a day without radar is told apart from other failures`() = testScope.runTest {
        val statuses: Map<HttpStatusCode, ErrorCode> = mapOf(
            HttpStatusCode.NotFound to ErrorCode.RADAR_NOT_AVAILABLE,
            HttpStatusCode.InternalServerError to ErrorCode.HTTP_INTERNAL_SERVER_ERROR
        )

        withRecordedLogs {
            val actual: Map<HttpStatusCode, Either<Failure, Radar>> = statuses.keys.associateWith { status: HttpStatusCode ->
                DefaultRadarRepository(
                    radarRemoteDataSource = DefaultRadarRemoteDataSource(httpClient = lazy { mockHttpClient { respond(content = "", status = status) } }),
                    radarLocalDataSource = DefaultRadarLocalDataSource(
                        diskCache = FakeDiskCache(),
                        coroutineDispatcher = StandardTestDispatcher(testScheduler)
                    ),
                    clock = clock
                ).getRadar()
            }

            assertEquals(
                expected = statuses.mapValues { (_, errorCode: ErrorCode) -> Either.Left(a = Failure(errorCode = errorCode)) },
                actual = actual
            )
        }
    }
}