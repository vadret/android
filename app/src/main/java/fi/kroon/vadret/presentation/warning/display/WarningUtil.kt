package fi.kroon.vadret.presentation.warning.display

import fi.kroon.vadret.R
import fi.kroon.vadret.util.extension.empty

object WarningUtil {

    private const val SMHI = "SMHI"
    private const val KRISINFORMATION = "Krisinformation"
    private const val TRAFIKVERKET = "Trafikverket"

    fun getSourceIdentifier(sourceId: Int): String = when (sourceId) {
        0 -> KRISINFORMATION
        1 -> SMHI
        2 -> TRAFIKVERKET
        else -> String.empty()
    }

    fun getSenderNameColor(sourceId: Int): Int = when (sourceId) {
        0 -> R.color.feed_source_color_0
        1 -> R.color.feed_source_color_1
        2 -> R.color.feed_source_color_2
        else -> R.color.feed_source_unknown_color
    }

    fun getChipFeedSourceStrokeColor(name: String): Int =
        when (name) {
            KRISINFORMATION -> R.attr.warningFilterChipFeedSource0StrokeColor
            SMHI -> R.attr.warningFilterChipFeedSource1StrokeColor
            TRAFIKVERKET -> R.attr.warningFilterChipFeedSource2StrokeColor
            else -> R.attr.warningFilterChipDefaultStrokeColor
        }

    fun getChipFeedSourceBackgroundColor(name: String): Int =
        when (name) {
            KRISINFORMATION -> R.attr.warningFilterChipFeedSource0Color
            SMHI -> R.attr.warningFilterChipFeedSource1Color
            TRAFIKVERKET -> R.attr.warningFilterChipFeedSource2Color
            else -> R.attr.warningFilterChipDefaultColor
        }

    fun getWarningSeverityColor(level: String): Int =
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