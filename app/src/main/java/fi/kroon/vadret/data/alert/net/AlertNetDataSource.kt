package fi.kroon.vadret.data.alert.net

import fi.kroon.vadret.utils.SMHI_API_ALERT_URL
import fi.kroon.vadret.data.alert.model.Alert
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface AlertNetDataSource {
    @GET
    fun get(@Url url: String = SMHI_API_ALERT_URL): Single<Response<Alert>>
}