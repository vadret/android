package fi.kroon.vadret.data.nominatim.net

import fi.kroon.vadret.data.nominatim.model.Nominatim
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimNetDataSource {

    @GET("search")
    suspend fun getNominatim(
        @Query("city") city: String,
        @Query("format") format: String,
        @Query("countrycodes") countryCodes: String,
        @Query("limit") limit: Int,
        @Query("namedetails") nameDetails: Int,
        @Query("addressdetails") addressDetails: Int
    ): Response<List<Nominatim>>

    @GET("reverse")
    suspend fun getNominatimReverse(
        @Query("format") format: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("zoom") zoom: Int
    ): Response<Nominatim>
}