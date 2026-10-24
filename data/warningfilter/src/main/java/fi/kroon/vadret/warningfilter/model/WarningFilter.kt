package fi.kroon.vadret.warningfilter.model

/**
 *  Which districts and feed sources the warnings are shown for.
 */
data class WarningFilter(
    val districts: List<DistrictOption>,
    val feedSources: List<FeedSourceOption>
)