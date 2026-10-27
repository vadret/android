package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.theme.ThemeRepository
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map

internal class DefaultGetSettingsTask(
    private val locationRepository: LocationRepository,
    private val themeRepository: ThemeRepository
) : GetSettingsTask {

    override suspend fun invoke(): Either<Failure, Settings> =
        locationRepository
            .isAutomatic()
            .flatMap { isAutomaticLocation: Boolean ->
                themeRepository
                    .getTheme()
                    .flatMap { theme: Theme ->
                        locationRepository
                            .getStoredLocation()
                            .map { storedLocation: StoredLocation ->
                                Settings(
                                    isAutomaticLocation = isAutomaticLocation,
                                    theme = theme,
                                    storedLocation = storedLocation
                                )
                            }
                    }
            }
}