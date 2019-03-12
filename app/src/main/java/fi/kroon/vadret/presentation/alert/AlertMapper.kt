package fi.kroon.vadret.presentation.alert

import fi.kroon.vadret.R
import fi.kroon.vadret.data.alert.model.EventCode
import fi.kroon.vadret.data.alert.model.Info
import fi.kroon.vadret.data.alert.model.Warning
import fi.kroon.vadret.presentation.alert.model.BaseWarningItemModel
import fi.kroon.vadret.presentation.alert.model.WarningItemItemModel
import fi.kroon.vadret.utils.common.DateTimeStringUtil
import fi.kroon.vadret.utils.extensions.empty
import fi.kroon.vadret.utils.extensions.splitToList
import java.util.Locale

object AlertMapper {

    fun toWarningModelList(warnings: List<Warning>): List<BaseWarningItemModel> = when {
        warnings.isNotEmpty() -> {
            val baseWarningItemModelList: List<BaseWarningItemModel> = mapToBaseWarningModel(warnings)
            baseWarningItemModelList
        }
        else -> {
            emptyList()
        }
    }

    private fun mapToBaseWarningModel(warnings: List<Warning>): List<BaseWarningItemModel> =
        warnings.map { warning: Warning ->

            with(warning) {

                val (diffMillis: Long, resourceId: Int) = DateTimeStringUtil.toMomentsAgo(sent)

                WarningItemItemModel(
                    eventLevelTitle = setEventLevel(info),
                    issuedAtStringResource = resourceId,
                    diffInMillis = diffMillis,
                    backgroundColorResource = handleEventCode(info),
                    description = info.description,
                    headlineItems = info.headline.splitToList(),
                    timeStamp = sent
                )
            }
        }.toList()

    private fun handleEventCode(info: Info): Int {
        var systemEventPriority: Int = 0
        for (eventCode: EventCode in info.eventCode) {
            when (eventCode.valueName) {
                "system_event_priority" -> systemEventPriority = getWarningColor(eventCode.value)
            }
        }
        return systemEventPriority
    }

    private fun setEventLevel(info: Info) =
        when (Locale.getDefault().displayLanguage) {
            "English" -> setEvent("system_event_level", info)
            else -> setEvent("system_event_level_sv-SE", info)
        }

    private fun setEvent(key: String, info: Info): String {
        var event: String = String.empty()
        for (eventCode: EventCode in info.eventCode) {
            when (eventCode.valueName) {
                key -> event = eventCode.value
            }
        }
        return event
    }

    private fun getWarningColor(level: String): Int =
        when (level) {
            "1" -> R.color.warning_class_1
            "2" -> R.color.warning_class_2
            "3" -> R.color.warning_class_3
            "4" -> R.color.risk_for_very_difficult_weather
            "5" -> R.color.risk_for_fire
            "6" -> R.color.message_high_temperature
            else -> R.color.light_purple_shade
        }
}