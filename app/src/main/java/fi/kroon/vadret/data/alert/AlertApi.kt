package fi.kroon.vadret.data.alert

import fi.kroon.vadret.data.API_ALERT_URL
import fi.kroon.vadret.data.alert.model.Alert
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface AlertApi {

    @GET()
    fun get(
        @Url url: String = API_ALERT_URL
    ): Single<Response<Alert>>
}