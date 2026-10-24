package fi.kroon.vadret.data.aggregatedfeed.mapper

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.aggregatedfeed.model.Area
import fi.kroon.vadret.data.aggregatedfeed.model.KrisinformationArea
import fi.kroon.vadret.data.aggregatedfeed.model.KrisinformationFeed
import fi.kroon.vadret.data.aggregatedfeed.model.SmhiAffectedArea
import fi.kroon.vadret.data.aggregatedfeed.model.SmhiDescription
import fi.kroon.vadret.data.aggregatedfeed.model.SmhiWarning
import fi.kroon.vadret.data.aggregatedfeed.model.SmhiWarningArea
import fi.kroon.vadret.util.FEED_SOURCE_KRISINFORMATION_ID
import fi.kroon.vadret.util.FEED_SOURCE_SMHI_ID

object AggregatedFeedMapper {

    private const val LANGUAGE = "sv"
    private const val SMHI_SENDER_NAME = "SMHI"
    private const val KRISINFORMATION_SENDER_NAME = "Krisinformation"
    private const val SMHI_WARNINGS_URL = "https://www.smhi.se/vader/prognoser-och-varningar/varningar-och-meddelanden/varningar"
    private const val SMHI_MESSAGE_LEVEL = "MESSAGE"
    private const val DISTRICT_AREA_TYPE = "District"

    /**
     *  Each warning area of an SMHI warning becomes its
     *  own item, kept when it affects any of [districtIds].
     */
    fun fromSmhi(warningList: List<SmhiWarning>, districtIds: List<Int>): List<AggregatedFeed> =
        warningList.flatMap { warning: SmhiWarning ->
            warning.warningAreas
                .orEmpty()
                .filter { warningArea: SmhiWarningArea ->
                    warningArea.affectedAreas
                        .orEmpty()
                        .any { affectedArea: SmhiAffectedArea -> affectedArea.id in districtIds }
                }.map { warningArea: SmhiWarningArea ->
                    toAggregatedFeed(warning = warning, warningArea = warningArea)
                }
        }

    fun fromKrisinformation(feedList: List<KrisinformationFeed>, isVma: Boolean): List<AggregatedFeed> =
        feedList.map { feed: KrisinformationFeed ->
            with(feed) {
                AggregatedFeed(
                    identifier = "krisinformation-$identifier",
                    pushMessage = headline,
                    updated = updated ?: published,
                    published = published,
                    headline = headline,
                    preamble = preamble.orEmpty(),
                    bodyText = bodyText.orEmpty(),
                    areaList = areaList.orEmpty().map { area: KrisinformationArea ->
                        Area(
                            type = area.type.orEmpty(),
                            description = area.description.orEmpty(),
                            coordinate = area.coordinate
                        )
                    },
                    web = web,
                    language = language ?: LANGUAGE,
                    event = event.orEmpty(),
                    senderName = KRISINFORMATION_SENDER_NAME,
                    push = isVma,
                    bodyLinks = links.orEmpty().map { it.url },
                    sourceId = FEED_SOURCE_KRISINFORMATION_ID,
                    isVma = isVma,
                    isTestVma = false
                )
            }
        }

    private fun toAggregatedFeed(warning: SmhiWarning, warningArea: SmhiWarningArea): AggregatedFeed {
        val event: String = warning.event.sv
        val headline: String = when (warningArea.warningLevel.code) {
            SMHI_MESSAGE_LEVEL -> "${warningArea.warningLevel.sv}: $event"
            else -> "${warningArea.warningLevel.sv} varning: $event"
        }
        val preamble: String = listOfNotNull(
            warningArea.areaName.sv,
            warningArea.eventDescription.sv.takeIf { it != event }
        ).joinToString(" – ")
        val bodyText: String = (warningArea.descriptions.orEmpty() + warning.descriptions.orEmpty())
            .joinToString("\n\n") { description: SmhiDescription ->
                "${description.title.sv}\n${description.text.sv}"
            }

        return AggregatedFeed(
            identifier = "smhi-${warning.id}-${warningArea.id}",
            pushMessage = headline,
            updated = warningArea.published,
            published = warningArea.published,
            headline = headline,
            preamble = preamble,
            bodyText = bodyText,
            areaList = warningArea.affectedAreas.orEmpty().map { affectedArea: SmhiAffectedArea ->
                Area(
                    type = DISTRICT_AREA_TYPE,
                    description = affectedArea.sv,
                    coordinate = null
                )
            },
            web = SMHI_WARNINGS_URL,
            language = LANGUAGE,
            event = event,
            senderName = SMHI_SENDER_NAME,
            push = warningArea.pushNotice,
            bodyLinks = emptyList(),
            sourceId = FEED_SOURCE_SMHI_ID,
            isVma = false,
            isTestVma = false
        )
    }
}