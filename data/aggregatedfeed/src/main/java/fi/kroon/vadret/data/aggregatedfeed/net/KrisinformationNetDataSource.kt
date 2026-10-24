package fi.kroon.vadret.data.aggregatedfeed.net

import fi.kroon.vadret.data.aggregatedfeed.model.KrisinformationFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *  [counties] is a comma separated list of county codes,
 *  national items are returned regardless.
 */
interface KrisinformationNetDataSource {

    @GET("news")
    suspend fun getNews(
        @Query("counties")
        counties: String
    ): Response<List<KrisinformationFeed>>

    @GET("vmas")
    suspend fun getVmas(
        @Query("counties")
        counties: String
    ): Response<List<KrisinformationFeed>>
}