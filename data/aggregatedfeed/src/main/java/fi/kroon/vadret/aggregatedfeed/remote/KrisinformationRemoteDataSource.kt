package fi.kroon.vadret.aggregatedfeed.remote

/**
 *  [counties] is a comma separated list of county codes.
 */
internal interface KrisinformationRemoteDataSource {
    suspend fun getNews(counties: String): List<KrisinformationFeedDto>
    suspend fun getVmas(counties: String): List<KrisinformationFeedDto>
}