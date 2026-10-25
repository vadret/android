package fi.kroon.vadret.aggregatedfeed

import fi.kroon.vadret.aggregatedfeed.local.DefaultAggregatedFeedLocalDataSource
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedDetail
import fi.kroon.vadret.aggregatedfeed.model.FeedDetailKind
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationFeedDto
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationRemoteDataSource
import fi.kroon.vadret.aggregatedfeed.remote.SmhiAffectedAreaDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiDescriptionDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiTextDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningAreaDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningRemoteDataSource
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.testing.FakeDiskCache
import fi.kroon.vadret.testing.FixedClock
import fi.kroon.vadret.testing.LogLine
import fi.kroon.vadret.testing.withRecordedLogs
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import java.io.IOException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private val now: Instant = Instant.parse(input = "2026-10-24T14:00:00Z")

private class FakeSmhiWarningRemoteDataSource(
    var smhiWarningDtos: List<SmhiWarningDto> = emptyList()
) : SmhiWarningRemoteDataSource {

    var requestCount: Int = 0

    override suspend fun getWarnings(): List<SmhiWarningDto> {
        requestCount++
        return smhiWarningDtos
    }
}

private class FakeKrisinformationRemoteDataSource(
    var news: () -> List<KrisinformationFeedDto> = { emptyList() },
    var vmas: () -> List<KrisinformationFeedDto> = { emptyList() }
) : KrisinformationRemoteDataSource {

    val requestedCounties = mutableListOf<String>()

    override suspend fun getNews(counties: String): List<KrisinformationFeedDto> {
        requestedCounties.add(counties)
        return news()
    }

    override suspend fun getVmas(counties: String): List<KrisinformationFeedDto> = vmas()
}

private fun text(
    sv: String,
    code: String? = null
): SmhiTextDto =
    SmhiTextDto(
        sv = sv,
        code = code
    )

private fun warningArea(
    id: Int,
    published: String,
    districtIds: List<Int>,
    warningLevel: SmhiTextDto = text(sv = "Gul", code = "YELLOW"),
    eventDescription: String = "Mycket regn"
): SmhiWarningAreaDto =
    SmhiWarningAreaDto(
        id = id,
        published = published,
        areaName = text(sv = "Västra Götaland"),
        warningLevel = warningLevel,
        eventDescription = text(sv = eventDescription),
        affectedAreas = districtIds.map { districtId: Int -> SmhiAffectedAreaDto(id = districtId) },
        descriptions = listOf(
            SmhiDescriptionDto(
                title = text(sv = "Var?", code = "WHERE"),
                text = text(sv = "Kusten")
            )
        )
    )

private fun news(
    identifier: String,
    published: String,
    headline: String = "Nyhet $identifier",
    preamble: String? = "Ingress",
    bodyText: String? = null
): KrisinformationFeedDto =
    KrisinformationFeedDto(
        identifier = identifier,
        published = published,
        headline = headline,
        preamble = preamble,
        bodyText = bodyText
    )

private val smhiDetails: List<FeedDetail> = listOf(
    FeedDetail(
        kind = FeedDetailKind.WHERE,
        title = "Var?",
        text = "Kusten"
    ),
    FeedDetail(
        kind = FeedDetailKind.OTHER,
        title = "Vad",
        text = "Skyfall"
    )
)

class DefaultAggregatedFeedRepositoryTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val clock = FixedClock(now = now)
    private val smhiWarningRemoteDataSource = FakeSmhiWarningRemoteDataSource()
    private val krisinformationRemoteDataSource = FakeKrisinformationRemoteDataSource()
    private val diskCache = FakeDiskCache()
    private val aggregatedFeedRepository: AggregatedFeedRepository = DefaultAggregatedFeedRepository(
        smhiWarningRemoteDataSource = smhiWarningRemoteDataSource,
        krisinformationRemoteDataSource = krisinformationRemoteDataSource,
        aggregatedFeedLocalDataSource = DefaultAggregatedFeedLocalDataSource(
            diskCache = diskCache,
            coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
        ),
        clock = clock
    )

    private suspend fun getFeed(
        feedSources: Set<FeedSource> = FeedSource.entries.toSet(),
        districtIds: Set<Int> = setOf(14),
        isRefresh: Boolean = false
    ): Either<Failure, List<AggregatedFeed>> =
        aggregatedFeedRepository.getAggregatedFeed(
            feedSources = feedSources,
            districtIds = districtIds,
            isRefresh = isRefresh
        )

    private fun Either<Failure, List<AggregatedFeed>>.identifiers(): List<String>? = (this as? Either.Right)?.b?.map { aggregatedFeed: AggregatedFeed -> aggregatedFeed.identifier }

    @Test
    fun `smhi warning areas become their own items`() =
        testScope.runTest {
            smhiWarningRemoteDataSource.smhiWarningDtos = listOf(
                SmhiWarningDto(
                    id = 7,
                    event = text(sv = "Regn"),
                    descriptions = listOf(
                        SmhiDescriptionDto(
                            title = text(sv = "Vad"),
                            text = text(sv = "Skyfall")
                        )
                    ),
                    warningAreas = listOf(
                        warningArea(
                            id = 1,
                            published = "2026-10-24T09:00:00Z",
                            districtIds = listOf(14)
                        ),
                        warningArea(
                            id = 2,
                            published = "2026-10-24T10:00:00Z",
                            districtIds = listOf(1, 14),
                            eventDescription = "Regn"
                        ),
                        warningArea(
                            id = 3,
                            published = "2026-10-24T11:00:00Z",
                            districtIds = listOf(14),
                            warningLevel = text(sv = "Meddelande", code = "MESSAGE")
                        ),
                        warningArea(
                            id = 4,
                            published = "2026-10-24T12:00:00Z",
                            districtIds = listOf(1)
                        )
                    )
                )
            )

            assertEquals<Either<Failure, List<AggregatedFeed>>>(
                expected = listOf(
                    AggregatedFeed(
                        identifier = "smhi-7-3",
                        headline = "Mycket regn",
                        areaName = "Västra Götaland",
                        warningLevel = WarningLevel.MESSAGE,
                        details = smhiDetails,
                        source = FeedSource.SMHI,
                        publishedAt = Instant.parse(input = "2026-10-24T11:00:00Z")
                    ),
                    AggregatedFeed(
                        identifier = "smhi-7-2",
                        headline = "Regn",
                        areaName = "Västra Götaland",
                        warningLevel = WarningLevel.YELLOW,
                        details = smhiDetails,
                        source = FeedSource.SMHI,
                        publishedAt = Instant.parse(input = "2026-10-24T10:00:00Z")
                    ),
                    AggregatedFeed(
                        identifier = "smhi-7-1",
                        headline = "Mycket regn",
                        areaName = "Västra Götaland",
                        warningLevel = WarningLevel.YELLOW,
                        details = smhiDetails,
                        source = FeedSource.SMHI,
                        publishedAt = Instant.parse(input = "2026-10-24T09:00:00Z")
                    )
                ).asRight(),
                actual = getFeed(feedSources = setOf(FeedSource.SMHI))
            )
        }

    @Test
    fun `news and vmas are merged newest first and a vma listed as news shows once`() =
        testScope.runTest {
            smhiWarningRemoteDataSource.smhiWarningDtos = listOf(
                SmhiWarningDto(
                    id = 7,
                    event = text(sv = "Regn"),
                    warningAreas = listOf(
                        warningArea(
                            id = 1,
                            published = "2026-10-24T10:00:00Z",
                            districtIds = listOf(14)
                        )
                    )
                )
            )
            krisinformationRemoteDataSource.news = {
                listOf(
                    news(identifier = "a", published = "2026-10-24T08:00:00+02:00"),
                    news(identifier = "vma", published = "2026-10-24T13:30:00+02:00")
                )
            }
            krisinformationRemoteDataSource.vmas = {
                listOf(news(identifier = "vma", published = "2026-10-24T13:30:00+02:00"))
            }

            assertEquals(
                expected = listOf("krisinformation-vma", "smhi-7-1", "krisinformation-a"),
                actual = getFeed().identifiers()
            )
        }

    @Test
    fun `every smhi warning level and description code is read`() =
        testScope.runTest {
            smhiWarningRemoteDataSource.smhiWarningDtos = listOf(
                SmhiWarningDto(
                    id = 8,
                    event = text(sv = "Vind"),
                    warningAreas = listOf("MESSAGE", "YELLOW", "ORANGE", "RED").mapIndexed { index: Int, code: String ->
                        warningArea(
                            id = index,
                            published = "2026-10-24T0$index:00:00Z",
                            districtIds = listOf(14),
                            warningLevel = text(sv = code, code = code)
                        ).copy(
                            descriptions = listOf("HAPPENS", "INCIDENT", "WHERE", "AFFECT", "COMMENTS", "NEW").map { descriptionCode: String ->
                                SmhiDescriptionDto(
                                    title = text(sv = descriptionCode, code = descriptionCode),
                                    text = text(sv = "Text")
                                )
                            }
                        )
                    }
                )
            )

            val feed: List<AggregatedFeed> = (getFeed(feedSources = setOf(FeedSource.SMHI)) as Either.Right).b

            assertEquals(
                expected = listOf(WarningLevel.RED, WarningLevel.ORANGE, WarningLevel.YELLOW, WarningLevel.MESSAGE),
                actual = feed.map { aggregatedFeed: AggregatedFeed -> aggregatedFeed.warningLevel }
            )
            assertEquals(
                expected = listOf(
                    FeedDetailKind.WHAT_HAPPENS,
                    FeedDetailKind.DESCRIPTION,
                    FeedDetailKind.WHERE,
                    FeedDetailKind.ADVICE,
                    FeedDetailKind.COMMENT,
                    FeedDetailKind.OTHER
                ),
                actual = feed.first().details.map { feedDetail: FeedDetail -> feedDetail.kind }
            )
        }

    @Test
    fun `an unknown smhi warning level is logged and left out`() =
        testScope.runTest {
            smhiWarningRemoteDataSource.smhiWarningDtos = listOf(
                SmhiWarningDto(
                    id = 9,
                    event = text(sv = "Vind"),
                    warningAreas = listOf(
                        warningArea(
                            id = 1,
                            published = "2026-10-24T09:00:00Z",
                            districtIds = listOf(14),
                            warningLevel = text(sv = "Lila", code = "PURPLE")
                        )
                    )
                )
            )

            withRecordedLogs { recordingLogSink ->
                val feed: List<AggregatedFeed> = (getFeed(feedSources = setOf(FeedSource.SMHI)) as Either.Right).b

                assertEquals(
                    expected = listOf(null),
                    actual = feed.map { aggregatedFeed: AggregatedFeed -> aggregatedFeed.warningLevel }
                )
                assertEquals(expected = LogLevel.WARN, actual = recordingLogSink.lines.single().level)
            }
        }

    @Test
    fun `krisinformation keeps its preamble and body as untitled details`() =
        testScope.runTest {
            krisinformationRemoteDataSource.news = {
                listOf(
                    news(
                        identifier = "a",
                        published = "2026-10-24T08:00:00Z",
                        preamble = " Ingress ",
                        bodyText = "Brödtext"
                    ),
                    news(
                        identifier = "b",
                        published = "2026-10-24T07:00:00Z",
                        preamble = " ",
                        bodyText = null
                    )
                )
            }

            assertEquals<Either<Failure, List<AggregatedFeed>>>(
                expected = listOf(
                    AggregatedFeed(
                        identifier = "krisinformation-a",
                        headline = "Nyhet a",
                        areaName = null,
                        warningLevel = null,
                        details = listOf(
                            FeedDetail(kind = FeedDetailKind.SUMMARY, title = "", text = "Ingress"),
                            FeedDetail(kind = FeedDetailKind.BODY, title = "", text = "Brödtext")
                        ),
                        source = FeedSource.KRISINFORMATION,
                        publishedAt = Instant.parse(input = "2026-10-24T08:00:00Z")
                    ),
                    AggregatedFeed(
                        identifier = "krisinformation-b",
                        headline = "Nyhet b",
                        areaName = null,
                        warningLevel = null,
                        details = emptyList(),
                        source = FeedSource.KRISINFORMATION,
                        publishedAt = Instant.parse(input = "2026-10-24T07:00:00Z")
                    )
                ).asRight(),
                actual = getFeed(feedSources = setOf(FeedSource.KRISINFORMATION))
            )
        }

    @Test
    fun `a feed cached in the old format is dropped and fetched again`() =
        testScope.runTest {
            diskCache.entries["aggregatedfeed.json"] = """
                {"fetched_at_epoch_millis":${now.toEpochMilliseconds()},"feed_sources":["KRISINFORMATION","SMHI"],"district_ids":[14],
                "items":[{"identifier":"smhi-1-1","headline":"Gul varning: Regn","preamble":"Kusten","body_text":"Text","source":"SMHI","published_at_epoch_millis":0}]}
            """.trimIndent()

            withRecordedLogs { recordingLogSink ->
                assertEquals(expected = emptyList(), actual = getFeed().identifiers())
                assertEquals(expected = 1, actual = smhiWarningRemoteDataSource.requestCount)
                assertEquals(
                    expected = listOf(LogLevel.WARN),
                    actual = recordingLogSink.lines.map { line: LogLine -> line.level }
                )
            }
        }

    @Test
    fun `only enabled sources are asked`() =
        testScope.runTest {
            getFeed(feedSources = setOf(FeedSource.KRISINFORMATION), districtIds = setOf(14, 1))

            assertEquals(expected = 0, actual = smhiWarningRemoteDataSource.requestCount)
            assertEquals(expected = listOf("1,14"), actual = krisinformationRemoteDataSource.requestedCounties)
        }

    @Test
    fun `items with an unreadable publish time are skipped`() =
        testScope.runTest {
            krisinformationRemoteDataSource.news = {
                listOf(
                    news(identifier = "a", published = "yesterday"),
                    news(identifier = "b", published = "2026-10-24T08:00:00Z")
                )
            }

            withRecordedLogs { recordingLogSink ->
                assertEquals(expected = listOf("krisinformation-b"), actual = getFeed().identifiers())
                assertEquals(expected = LogLevel.WARN, actual = recordingLogSink.lines.single().level)
            }
        }

    @Test
    fun `a failing vma feed still shows news`() =
        testScope.runTest {
            krisinformationRemoteDataSource.news = {
                listOf(news(identifier = "a", published = "2026-10-24T08:00:00Z"))
            }
            krisinformationRemoteDataSource.vmas = { throw IOException("reset") }

            withRecordedLogs { recordingLogSink ->
                assertEquals(expected = listOf("krisinformation-a"), actual = getFeed().identifiers())
                assertEquals(
                    expected = listOf("Skipping VMAs: IOException: reset"),
                    actual = recordingLogSink.lines.map { line: LogLine -> line.message }
                )
            }
        }

    @Test
    fun `a failing news feed fails the whole feed`() =
        testScope.runTest {
            krisinformationRemoteDataSource.news = { throw UnknownHostException("api.krisinformation.se") }

            withRecordedLogs { recordingLogSink ->
                assertEquals(
                    expected = Failure(errorCode = ErrorCode.NETWORK_UNAVAILABLE).asLeft(),
                    actual = getFeed()
                )
                assertEquals(
                    expected = listOf(LogLevel.WARN),
                    actual = recordingLogSink.lines.map { line: LogLine -> line.level }
                )
            }
        }

    @Test
    fun `an unexpected failure is unknown and logged as an error`() =
        testScope.runTest {
            krisinformationRemoteDataSource.news = { throw IllegalStateException("unexpected") }

            withRecordedLogs { recordingLogSink ->
                assertEquals(
                    expected = Failure.unknown().asLeft(),
                    actual = getFeed()
                )
                assertEquals(
                    expected = listOf(LogLevel.ERROR),
                    actual = recordingLogSink.lines.map { line: LogLine -> line.level }
                )
            }
        }

    @Test
    fun `the feed is reused for the same filter within five minutes`() =
        testScope.runTest {
            getFeed(districtIds = setOf(14, 1))
            clock.now = now + 4.minutes
            getFeed(districtIds = setOf(1, 14))

            assertEquals(expected = 1, actual = smhiWarningRemoteDataSource.requestCount)
        }

    @Test
    fun `a refresh, another filter or an old feed is fetched again`() =
        testScope.runTest {
            getFeed()
            getFeed(isRefresh = true)
            getFeed(districtIds = setOf(1))
            getFeed(districtIds = setOf(1), feedSources = setOf(FeedSource.SMHI))
            clock.now = now + 5.minutes
            getFeed(districtIds = setOf(1), feedSources = setOf(FeedSource.SMHI))

            assertEquals(expected = 5, actual = smhiWarningRemoteDataSource.requestCount)
        }
}