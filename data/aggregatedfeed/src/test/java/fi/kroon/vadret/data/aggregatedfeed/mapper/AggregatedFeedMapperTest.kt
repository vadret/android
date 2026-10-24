package fi.kroon.vadret.data.aggregatedfeed.mapper

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.aggregatedfeed.model.KrisinformationFeed
import fi.kroon.vadret.data.aggregatedfeed.model.SmhiWarning
import fi.kroon.vadret.data.common.defaultJson
import fi.kroon.vadret.util.FEED_SOURCE_KRISINFORMATION_ID
import fi.kroon.vadret.util.FEED_SOURCE_SMHI_ID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *  Fixtures are trimmed responses from SMHI's IBWW v1
 *  and Krisinformation's v3 APIs.
 */
class AggregatedFeedMapperTest {

    private companion object {
        const val STOCKHOLMS_LAN = 1
        const val GOTLANDS_LAN = 9
        const val SKANE_LAN = 12
    }

    private val smhiWarningList: List<SmhiWarning> = parse("smhi_warning.json")

    private val newsList: List<KrisinformationFeed> = parse("krisinformation_news.json")

    private inline fun <reified T> parse(fileName: String): List<T> =
        defaultJson.decodeFromString(
            javaClass.classLoader!!.getResource(fileName).readText()
        )

    @Test
    fun `smhi warning areas are kept only when they affect a selected district`() {
        val feedList: List<AggregatedFeed> = AggregatedFeedMapper.fromSmhi(smhiWarningList, listOf(STOCKHOLMS_LAN))

        assertEquals(listOf("smhi-3378-10696"), feedList.map { it.identifier })
        assertEquals(2, AggregatedFeedMapper.fromSmhi(smhiWarningList, listOf(STOCKHOLMS_LAN, GOTLANDS_LAN)).size)
        assertTrue(AggregatedFeedMapper.fromSmhi(smhiWarningList, listOf(SKANE_LAN)).isEmpty())
    }

    @Test
    fun `smhi warning area maps to a feed item`() {
        val feed: AggregatedFeed = AggregatedFeedMapper.fromSmhi(smhiWarningList, listOf(STOCKHOLMS_LAN)).single()

        assertEquals("Gul varning: Vind", feed.headline)
        assertEquals("Östra delarna av Stockholms län och Sörmlands län", feed.preamble)
        assertEquals("2026-10-23T09:11:36.941Z", feed.published)
        assertEquals(FEED_SOURCE_SMHI_ID, feed.sourceId)
        assertEquals(listOf("Södermanlands län", "Stockholms län"), feed.areaList.map { it.description })
        assertTrue(feed.bodyText.startsWith("Vad händer?\n"))
    }

    @Test
    fun `krisinformation news maps to a feed item`() {
        val feed: AggregatedFeed = AggregatedFeedMapper.fromKrisinformation(newsList, isVma = false).single()

        assertEquals("krisinformation-1211", feed.identifier)
        assertEquals("Varning för kraftiga vindar i södra och mellersta Sverige", feed.headline)
        assertEquals(FEED_SOURCE_KRISINFORMATION_ID, feed.sourceId)
        assertEquals(3, feed.bodyLinks?.size)
        assertEquals(false, feed.isVma)
    }
}