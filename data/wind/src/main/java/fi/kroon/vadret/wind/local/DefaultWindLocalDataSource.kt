package fi.kroon.vadret.wind.local

import java.util.concurrent.atomic.AtomicReference

internal class DefaultWindLocalDataSource : WindLocalDataSource {

    private val windForecastEntity: AtomicReference<WindForecastEntity?> = AtomicReference(null)

    override fun getWindForecast(): WindForecastEntity? = windForecastEntity.get()

    override fun setWindForecast(windForecastEntity: WindForecastEntity) {
        this.windForecastEntity.set(windForecastEntity)
    }
}