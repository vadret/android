package fi.kroon.vadret.aggregatedfeed.local

internal interface AggregatedFeedLocalDataSource {

    suspend fun getAggregatedFeed(): AggregatedFeedCacheEntity?
    suspend fun setAggregatedFeed(aggregatedFeedCacheEntity: AggregatedFeedCacheEntity)
}