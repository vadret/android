package fi.kroon.vadret.settings

import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.library.LibraryRepository
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.theme.ThemeRepository
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val stockholm = StoredLocation(
    locality = "Stockholm",
    municipality = "Stockholm",
    county = "Stockholm",
    latitude = 59.3293,
    longitude = 18.0686
)

private class FakeThemeRepository(
    var theme: Either<Failure, Theme> = Theme.LIGHT.asRight()
) : ThemeRepository {
    override suspend fun getTheme(): Either<Failure, Theme> = theme
    override fun observeTheme(): Flow<Either<Failure, Theme>> = emptyFlow()
    override suspend fun setTheme(theme: Theme): Either<Failure, Unit> {
        this.theme = theme.asRight()
        return Unit.asRight()
    }
}

private class FakeLocationRepository(
    var isAutomatic: Either<Failure, Boolean> = true.asRight()
) : LocationRepository {
    override suspend fun getDeviceLocation(): Either<Failure, Location> = error("Settings never ask for the device location")
    override suspend fun isAutomatic(): Either<Failure, Boolean> = isAutomatic
    override suspend fun setAutomatic(isAutomatic: Boolean): Either<Failure, Unit> {
        this.isAutomatic = isAutomatic.asRight()
        return Unit.asRight()
    }
    override suspend fun getStoredLocation(): Either<Failure, StoredLocation> = stockholm.asRight()
    override suspend fun setStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit> = error("Settings never store a place")
}

private class FakeLibraryRepository : LibraryRepository {
    override fun getLibraries(): Either<Nothing, List<Library>> =
        listOf(
            Library(
                author = "JetBrains",
                title = "Ktor",
                description = "HTTP client",
                projectUrl = "https://ktor.io/",
                sourceUrl = null,
                licenseUrl = "https://ktor.io/license",
                license = "Apache License, Version 2.0"
            )
        ).asRight()
}

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val themeRepository = FakeThemeRepository()
    private val locationRepository = FakeLocationRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): SettingsViewModel {
        val settingsViewModel = SettingsViewModel(
            getSettingsTask = DefaultGetSettingsTask(
                locationRepository = locationRepository,
                themeRepository = themeRepository
            ),
            setLocationModeTask = DefaultSetLocationModeTask(locationRepository = locationRepository),
            setThemeTask = DefaultSetThemeTask(themeRepository = themeRepository),
            getLibrariesTask = DefaultGetLibrariesTask(libraryRepository = FakeLibraryRepository()),
            coroutineDispatcher = testDispatcher
        )
        settingsViewModel.onEvent(event = SettingsEvent.OnStart)
        return settingsViewModel
    }

    @Test
    fun `settings and libraries are loaded`() = testScope.runTest {
        val settingsViewModel: SettingsViewModel = viewModel()
        advanceUntilIdle()

        val state: SettingsState = settingsViewModel.state.value
        assertEquals(
            expected = Settings(
                isAutomaticLocation = true,
                theme = Theme.LIGHT,
                storedLocation = stockholm
            ),
            actual = state.settings
        )
        assertEquals(expected = listOf("Ktor"), actual = state.libraries.map { library: Library -> library.title })
        assertEquals(expected = 4, actual = state.aboutRows.size)
    }

    @Test
    fun `settings that cannot be read are not shown`() = testScope.runTest {
        themeRepository.theme = Failure(errorCode = ErrorCode.UNKNOWN).asLeft()

        val settingsViewModel: SettingsViewModel = viewModel()
        advanceUntilIdle()

        assertNull(actual = settingsViewModel.state.value.settings)
    }

    @Test
    fun `turning the automatic location off is stored and shown`() = testScope.runTest {
        val settingsViewModel: SettingsViewModel = viewModel()
        advanceUntilIdle()

        settingsViewModel.onEvent(event = SettingsEvent.OnLocationModeToggled(isAutomaticLocation = false))
        advanceUntilIdle()

        assertEquals(expected = false, actual = settingsViewModel.state.value.settings?.isAutomaticLocation)
    }

    @Test
    fun `choosing a theme closes the dialog and shows the theme`() = testScope.runTest {
        val settingsViewModel: SettingsViewModel = viewModel()
        advanceUntilIdle()

        settingsViewModel.onEvent(event = SettingsEvent.OnThemeRowClicked)
        val isOpened: Boolean = settingsViewModel.state.value.isThemeDialogVisible
        settingsViewModel.onEvent(event = SettingsEvent.OnThemeSelected(theme = Theme.AMOLED))
        advanceUntilIdle()

        assertEquals(expected = true, actual = isOpened)
        assertEquals(expected = false, actual = settingsViewModel.state.value.isThemeDialogVisible)
        assertEquals(expected = Theme.AMOLED, actual = settingsViewModel.state.value.settings?.theme)
    }

    @Test
    fun `dismissing the theme dialog keeps the theme`() = testScope.runTest {
        val settingsViewModel: SettingsViewModel = viewModel()
        advanceUntilIdle()

        settingsViewModel.onEvent(event = SettingsEvent.OnThemeRowClicked)
        settingsViewModel.onEvent(event = SettingsEvent.OnThemeDialogDismissed)
        advanceUntilIdle()

        assertEquals(expected = false, actual = settingsViewModel.state.value.isThemeDialogVisible)
        assertEquals(expected = Theme.LIGHT, actual = settingsViewModel.state.value.settings?.theme)
    }
}