package fi.kroon.vadret.warningfilter.model

data class WarningFilter(
    val districts: List<DistrictOption>,
    val feedSources: List<FeedSourceOption>
)