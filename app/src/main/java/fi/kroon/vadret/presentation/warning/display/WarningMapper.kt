package fi.kroon.vadret.presentation.warning.display

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.aggregatedfeed.model.Area
import fi.kroon.vadret.presentation.warning.display.WarningUtil.getSenderNameColor
import fi.kroon.vadret.presentation.warning.display.WarningUtil.getSourceIdentifier
import fi.kroon.vadret.presentation.warning.display.model.AreaModel
import fi.kroon.vadret.presentation.warning.display.model.ElapsedTime
import fi.kroon.vadret.presentation.warning.display.model.IWarningModel
import fi.kroon.vadret.presentation.warning.display.model.WarningModel
import fi.kroon.vadret.util.common.DateTimeStringUtil.toElapsedTime

object WarningMapper {

    operator fun invoke(entityList: List<AggregatedFeed>): MutableList<IWarningModel> =
        entityList.map { aggregatedFeed: AggregatedFeed ->
            with(aggregatedFeed) {

                val areaList: List<AreaModel> = toModel(areaList = areaList)
                val elapsedTime: ElapsedTime = toElapsedTime(published = published)
                val senderName: String = getSourceIdentifier(sourceId = sourceId)
                val backgroundResourceId: Int = getSenderNameColor(sourceId = sourceId)

                WarningModel(
                    identifier = identifier,
                    pushMessage = pushMessage,
                    updated = updated,
                    published = published,
                    headline = headline,
                    preamble = preamble.replace(",", ", "),
                    bodyText = bodyText,
                    areaList = areaList,
                    web = web,
                    language = language,
                    event = event,
                    senderName = senderName,
                    push = push,
                    bodyLinks = bodyLinks,
                    sourceId = sourceId,
                    isVma = isVma,
                    isTestVma = isTestVma,
                    backgroundResourceId = backgroundResourceId,
                    elapsedTime = elapsedTime
                )
            }
        }.toMutableList()

    private fun toModel(areaList: List<Area>): List<AreaModel> = areaList.map {
        AreaModel(
            type = it.type,
            description = it.description,
            coordinate = it.coordinate
        )
    }
}