package fi.kroon.vadret.data.feedsource.net

import fi.kroon.vadret.data.feedsource.model.FeedSource
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedSourceNetDataSource {

    @GET("feedsources/")
    fun getFeedSource(
        @Query("format")
        format: String = "json"
    ): Single<Response<List<FeedSource>>>
}