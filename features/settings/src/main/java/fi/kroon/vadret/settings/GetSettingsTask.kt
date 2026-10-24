package fi.kroon.vadret.settings

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.theme.ThemeRepository
import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_KEY
import fi.kroon.vadret.util.COUNTY_KEY
import fi.kroon.vadret.util.LATITUDE_KEY
import fi.kroon.vadret.util.LOCALITY_KEY
import fi.kroon.vadret.util.LONGITUDE_KEY
import fi.kroon.vadret.util.MUNICIPALITY_KEY
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import javax.inject.Inject

class GetSettingsTask @Inject constructor(
    private val weatherForecastLocalKeyValueDataSource: WeatherForecastLocalKeyValueDataSource,
    private val themeRepository: ThemeRepository
) {

    operator fun invoke(): Either<Failure, Settings> =
        weatherForecastLocalKeyValueDataSource
            .getBoolean(key = AUTOMATIC_LOCATION_MODE_KEY)
            .flatMap { isGpsEnabled: Boolean ->
                themeRepository
                    .get()
                    .flatMap { theme: Theme ->
                        getStoredLocation().map { storedLocation: StoredLocation ->
                            Settings(
                                isGpsEnabled = isGpsEnabled,
                                theme = theme,
                                storedLocation = storedLocation
                            )
                        }
                    }
            }

    private fun getStoredLocation(): Either<Failure, StoredLocation> =
        getString(key = LOCALITY_KEY).flatMap { city: String ->
            getString(key = MUNICIPALITY_KEY).flatMap { municipality: String ->
                getString(key = COUNTY_KEY).flatMap { county: String ->
                    getString(key = LATITUDE_KEY).flatMap { latitude: String ->
                        getString(key = LONGITUDE_KEY).map { longitude: String ->
                            StoredLocation(
                                city = city,
                                municipality = municipality,
                                county = county,
                                latitude = latitude,
                                longitude = longitude
                            )
                        }
                    }
                }
            }
        }

    private fun getString(key: String): Either<Failure, String> =
        weatherForecastLocalKeyValueDataSource.getString(key = key)
}