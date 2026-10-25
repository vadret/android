package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.favorite.FavoriteRepository
import fi.kroon.vadret.favorite.model.Favorite
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.nominatim.NominatimRepository
import fi.kroon.vadret.nominatim.model.Locality
import fi.kroon.vadret.weatherforecast.model.Forecast
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import io.github.sphrak.either.extension.successOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger
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

internal val kirunaFavorite = Favorite(
    favoriteId = "favorite-1",
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
    isAutomatic: Either<Failure, Boolean> = false.asRight(),
    var deviceLocation: Either<Failure, Location> = Location(latitude = 57.7, longitude = 11.97).asRight(),
    var selectStoredLocationResult: Either<Failure, Unit> = Unit.asRight()
) : LocationRepository {

    val writes = mutableListOf<String>()

    val locationPreference: MutableStateFlow<LocationPreference> = MutableStateFlow(
        value = LocationPreference(
            isAutomatic = isAutomatic.successOrNull ?: false,
            storedLocation = stockholm
        )
    )

    var isAutomatic: Either<Failure, Boolean> = isAutomatic
        set(value) {
            field = value
            val automatic: Boolean? = value.successOrNull
            if (automatic != null) {
                locationPreference.update { preference: LocationPreference -> preference.copy(isAutomatic = automatic) }
            }
        }

    override suspend fun getDeviceLocation(): Either<Failure, Location> = deviceLocation

    override suspend fun isAutomatic(): Either<Failure, Boolean> = isAutomatic

    override suspend fun setAutomatic(isAutomatic: Boolean): Either<Failure, Unit> {
        writes.add("automatic=$isAutomatic")
        this.isAutomatic = isAutomatic.asRight()
        return Unit.asRight()
    }

    override suspend fun getStoredLocation(): Either<Failure, StoredLocation> = locationPreference.value.storedLocation.asRight()

    override fun observeLocationPreference(): Flow<Either<Failure, LocationPreference>> = locationPreference.map { preference: LocationPreference -> preference.asRight() }

    override suspend fun selectStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit> {
        writes.add("selected=${storedLocation.locality}")
        if (selectStoredLocationResult.isRight) {
            this.isAutomatic = false.asRight()
            locationPreference.value = LocationPreference(
                isAutomatic = false,
                storedLocation = storedLocation
            )
        }
        return selectStoredLocationResult
    }
}

internal class FakeFavoriteRepository(
    var failure: Failure? = null
) : FavoriteRepository {

    val favorites: MutableStateFlow<List<Favorite>> = MutableStateFlow(value = emptyList())

    private val nextFavoriteNumber: AtomicInteger = AtomicInteger(1)

    override fun observeFavorites(): Flow<Either<Failure, List<Favorite>>> =
        favorites.map { favoriteList: List<Favorite> ->
            val currentFailure: Failure? = failure
            if (currentFailure != null) {
                currentFailure.asLeft()
            } else {
                favoriteList.asRight()
            }
        }

    override suspend fun addFavorite(
        locality: String,
        municipality: String,
        county: String,
        latitude: Double,
        longitude: Double
    ): Either<Failure, Favorite> {
        val currentFailure: Failure? = failure
        if (currentFailure != null) {
            return currentFailure.asLeft()
        }
        val storedFavorite: Favorite? = favorites.value.firstOrNull { favorite: Favorite ->
            favorite.locality == locality && favorite.municipality == municipality && favorite.county == county
        }
        if (storedFavorite != null) {
            return storedFavorite.asRight()
        }
        val favorite = Favorite(
            favoriteId = "favorite-${nextFavoriteNumber.getAndIncrement()}",
            locality = locality,
            municipality = municipality,
            county = county,
            latitude = latitude,
            longitude = longitude
        )
        favorites.update { favoriteList: List<Favorite> -> favoriteList + favorite }
        return favorite.asRight()
    }

    override suspend fun removeFavorite(favoriteId: String): Either<Failure, Unit> {
        val currentFailure: Failure? = failure
        if (currentFailure != null) {
            return currentFailure.asLeft()
        }
        favorites.update { favoriteList: List<Favorite> -> favoriteList.filterNot { favorite: Favorite -> favorite.favoriteId == favoriteId } }
        return Unit.asRight()
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