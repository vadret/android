package fi.kroon.vadret.aggregatedfeed.remote

internal interface KrisinformationRemoteDataSource {
    suspend fun getNews(counties: String): List<KrisinformationFeedDto>
    suspend fun getVmas(counties: String): List<KrisinformationFeedDto>
}