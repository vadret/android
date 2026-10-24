package fi.kroon.vadret.aggregatedfeed.local

internal interface AggregatedFeedLocalDataSource {

    /**
     *  Null when nothing readable is cached.
     */
    suspend fun getAggregatedFeed(): AggregatedFeedCacheEntity?
    suspend fun setAggregatedFeed(aggregatedFeedCacheEntity: AggregatedFeedCacheEntity)
}