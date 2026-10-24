package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.nominatim.NominatimRepository
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.weatherforecast.model.Forecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asRight
import kotlin.time.Instant

internal val stockholm = StoredLocation(
    locality = "Stockholm",
    municipality = "Stockholm",
    county = "Stockholm",
    latitude = 59.3293,
    longitude = 18.0686
)

internal val kiruna = AutoCompleteItem(
    locality = "Kiruna",
    municipality = "Kiruna",
    county = "Norrbotten",
    latitude = 67.8558,
    longitude = 20.2253
)

internal val noon = Forecast(
    validAt = Instant.parse(input = "2026-10-24T12:00:00Z"),
    airTemperature = 4.0,
    windFromDirection = 270.0,
    windSpeed = 3.0,
    relativeHumidity = 70,
    precipitationType = 0,
    symbolCode = 1
)

internal class FakeLocationRepository(
    var isAutomatic: Either<Failure, Boolean> = false.asRight(),
    var deviceLocation: Either<Failure, Location> = Location(latitude = 57.7, longitude = 11.97).asRight(),
    var storedLocation: Either<Failure, StoredLocation> = stockholm.asRight(),
    var setStoredLocationResult: Either<Failure, Unit> = Unit.asRight()
) : LocationRepository {

    val writes = mutableListOf<String>()

    override suspend fun getDeviceLocation(): Either<Failure, Location> = deviceLocation

    override suspend fun isAutomatic(): Either<Failure, Boolean> = isAutomatic

    override suspend fun setAutomatic(isAutomatic: Boolean): Either<Failure, Unit> {
        writes.add("automatic=$isAutomatic")
        this.isAutomatic = isAutomatic.asRight()
        return Unit.asRight()
    }

    override suspend fun getStoredLocation(): Either<Failure, StoredLocation> = storedLocation

    override suspend fun setStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit> {
        writes.add("stored=${storedLocation.locality}")
        if (setStoredLocationResult.isRight) {
            this.storedLocation = storedLocation.asRight()
        }
        return setStoredLocationResult
    }
}

internal class FakeWeatherForecastRepository(
    var forecasts: Either<Failure, List<Forecast>> = listOf(noon).asRight()
) : WeatherForecastRepository {

    val requestedCoordinates = mutableListOf<Pair<Double, Double>>()

    override suspend fun getForecasts(
        latitude: Double,
        longitude: Double
    ): Either<Failure, List<Forecast>> {
        requestedCoordinates.add(latitude to longitude)
        return forecasts
    }
}

internal class FakeNominatimRepository(
    var locality: Either<Failure, Locality> = Locality(name = "Göteborg").asRight()
) : NominatimRepository {
    override suspend fun getLocality(
        latitude: Double,
        longitude: Double
    ): Either<Failure, Locality> = locality
}

internal class FakeAutoCompleteRepository(
    var items: List<AutoCompleteItem> = listOf(kiruna)
) : AutoCompleteRepository {

    val searches = mutableListOf<Pair<String, Int>>()

    override suspend fun search(
        prefix: String,
        limit: Int
    ): Either<Failure, List<AutoCompleteItem>> {
        searches.add(prefix to limit)
        return items.filter { autoCompleteItem: AutoCompleteItem -> autoCompleteItem.locality.startsWith(prefix = prefix, ignoreCase = true) }.asRight()
    }
}