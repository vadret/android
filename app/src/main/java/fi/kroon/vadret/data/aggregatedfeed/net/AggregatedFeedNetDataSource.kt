package fi.kroon.vadret.data.aggregatedfeed.net

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AggregatedFeedNetDataSource {

    @GET("/v2/aggregatedfeed")
    fun getAggregatedFeed(

        @Query("days")
        days: Int = 7,

        @Query("feeds")
        feeds: String,

        @Query("counties")
        counties: String,

        @Query("format")
        format: String = "json"

    ): Single<Response<List<AggregatedFeed>>>
}