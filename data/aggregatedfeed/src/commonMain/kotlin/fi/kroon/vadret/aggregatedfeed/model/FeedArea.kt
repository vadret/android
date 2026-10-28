package fi.kroon.vadret.aggregatedfeed.model

data class FeedArea(
    val polygons: List<GeoPolygon>,
    val lines: List<GeoLine>,
    val regions: List<FeedRegion>
)