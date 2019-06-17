package fi.kroon.vadret.presentation.warning.filter

import fi.kroon.vadret.R
import timber.log.Timber

object WarningFilterUtil {

    private const val LAND = "land"
    private const val SEA = "sea"

    private const val SMHI = "SMHI"
    private const val KRISINFORMATION = "Krisinformation"
    private const val TRAFIKVERKET = "Trafikverket"

    fun getChipDistrictBackgroundColor(category: String): Int {
        return when (category) {
            LAND -> R.attr.warningFilterChipDistrictLandColor
            SEA -> R.attr.warningFilterChipDistrictSeaColor
            else -> {
                Timber.d("GET DISTRICT COLOR")
                R.attr.warningFilterChipDefaultColor
            }
        }
    }

    fun getChipDistrictStrokeColor(category: String): Int =
        when (category) {
            LAND -> R.attr.warningFilterChipDistrictLandStrokeColor
            SEA -> R.attr.warningFilterChipDistrictSeaStrokeColor
            else -> {
                Timber.d("GET DISTRICT STROKE COLOR")
                R.attr.warningFilterChipDefaultStrokeColor
            }
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
}