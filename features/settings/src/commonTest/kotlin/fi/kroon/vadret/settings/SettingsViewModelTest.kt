package fi.kroon.vadret.settings

import fi.kroon.vadret.changelog.ChangelogRepository
import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.setting_city
import fi.kroon.vadret.core.resources.setting_county
import fi.kroon.vadret.core.resources.setting_latitude
import fi.kroon.vadret.core.resources.setting_longitude
import fi.kroon.vadret.core.resources.setting_municipality
import fi.kroon.vadret.library.LibraryRepository
import fi.kroon.vadret.library.model.Library
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.location.model.Location
import fi.kroon.vadret.location.model.LocationPreference
import fi.kroon.vadret.location.model.StoredLocation
import fi.kroon.vadret.model.AppInfo
import fi.kroon.vadret.settings.model.AboutRowHint
import fi.kroon.vadret.settings.model.LibraryGroup
import fi.kroon.vadret.settings.model.LocationRow
import fi.kroon.vadret.theme.ThemeRepository
import fi.kroon.vadret.theme.model.Theme
import io.github.sphrak.either.Either
import io.github.sphrak.either.extension.asLeft
import io.github.sphrak.either.extension.asRight
import kotlinx.collections.immutable.persistentListOf
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
    override fun observeLocationPreference(): Flow<Either<Failure, LocationPreference>> = error("Settings never observe the location")
    override suspend fun selectStoredLocation(storedLocation: StoredLocation): Either<Failure, Unit> = error("Settings never store a place")
}

private class FakeLibraryRepository : LibraryRepository {
    override fun getLibraries(): Either<Nothing, List<Library>> = listOf(ktor, coil, kotlinxDatetime).asRight()
}

private class FakeChangelogRepository(
    var changelog: Either<Failure, Changelog> = Changelog(markdown = "# Changelog").asRight()
) : ChangelogRepository {
    override suspend fun getChangelog(): Either<Failure, Changelog> = changelog
}

private fun library(
    author: String,
    title: String
): Library =
    Library(
        author = author,
        title = title,
        description = "$title description",
        projectUrl = "https://example.org/$title",
        sourceUrl = null,
        licenseUrl = "https://example.org/$title/license",
        license = "Apache 2.0"
    )

private val ktor: Library = library(author = "JetBrains", title = "Ktor")
private val coil: Library = library(author = "Coil Contributors", title = "Coil")
private val kotlinxDatetime: Library = library(author = "JetBrains", title = "kotlinx-datetime")

@OptIn(ExperimentalCoroutinesApi::class)
private val appInfo = AppInfo(
    applicationId = "fi.kroon.vadret",
    versionName = "3.1.0-test",
    isDebug = true
)

class SettingsViewModelTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val themeRepository = FakeThemeRepository()
    private val locationRepository = FakeLocationRepository()
    private val changelogRepository = FakeChangelogRepository()

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
            getChangelogTask = DefaultGetChangelogTask(changelogRepository = changelogRepository),
            appInfo = appInfo,
            coroutineDispatcher = testDispatcher
        )
        settingsViewModel.onEvent(event = SettingsEvent.OnStart)
        return settingsViewModel
    }

    @Test
    fun `settings and libraries are loaded`() =
        testScope.runTest {
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
            assertEquals(
                expected = listOf(
                    LibraryGroup(author = "JetBrains", libraries = persistentListOf(ktor, kotlinxDatetime)),
                    LibraryGroup(author = "Coil Contributors", libraries = persistentListOf(coil))
                ),
                actual = state.libraryGroups
            )
            assertEquals(
                expected = listOf(
                    LocationRow(titleResource = Res.string.setting_city, value = stockholm.locality),
                    LocationRow(titleResource = Res.string.setting_municipality, value = stockholm.municipality),
                    LocationRow(titleResource = Res.string.setting_county, value = stockholm.county),
                    LocationRow(titleResource = Res.string.setting_latitude, value = stockholm.latitude.toString()),
                    LocationRow(titleResource = Res.string.setting_longitude, value = stockholm.longitude.toString())
                ),
                actual = state.locationRows
            )
            assertEquals(expected = 4, actual = state.aboutRows.size)
            assertEquals(
                expected = AboutRowHint.Plain(text = appInfo.versionName),
                actual = state.aboutRows.first().hint
            )
        }

    @Test
    fun `settings that cannot be read are not shown`() =
        testScope.runTest {
            themeRepository.theme = Failure(errorCode = ErrorCode.UNKNOWN).asLeft()

            val settingsViewModel: SettingsViewModel = viewModel()
            advanceUntilIdle()

            assertNull(actual = settingsViewModel.state.value.settings)
        }

    @Test
    fun `turning the automatic location off is stored and shown`() =
        testScope.runTest {
            val settingsViewModel: SettingsViewModel = viewModel()
            advanceUntilIdle()

            settingsViewModel.onEvent(event = SettingsEvent.OnLocationModeToggled(isAutomaticLocation = false))
            advanceUntilIdle()

            assertEquals(expected = false, actual = settingsViewModel.state.value.settings?.isAutomaticLocation)
        }

    @Test
    fun `choosing a theme closes the dialog and shows the theme`() =
        testScope.runTest {
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
    fun `dismissing the theme dialog keeps the theme`() =
        testScope.runTest {
            val settingsViewModel: SettingsViewModel = viewModel()
            advanceUntilIdle()

            settingsViewModel.onEvent(event = SettingsEvent.OnThemeRowClicked)
            settingsViewModel.onEvent(event = SettingsEvent.OnThemeDialogDismissed)
            advanceUntilIdle()

            assertEquals(expected = false, actual = settingsViewModel.state.value.isThemeDialogVisible)
            assertEquals(expected = Theme.LIGHT, actual = settingsViewModel.state.value.settings?.theme)
        }

    @Test
    fun `a clicked library is shown until it is dismissed`() =
        testScope.runTest {
            val settingsViewModel: SettingsViewModel = viewModel()
            advanceUntilIdle()

            settingsViewModel.onEvent(event = SettingsEvent.OnLibraryClicked(library = coil))
            val shown: Library? = settingsViewModel.state.value.selectedLibrary
            settingsViewModel.onEvent(event = SettingsEvent.OnLibraryDismissed)

            assertEquals(expected = coil, actual = shown)
            assertNull(actual = settingsViewModel.state.value.selectedLibrary)
        }

    @Test
    fun `the changelog is shown until it is dismissed`() =
        testScope.runTest {
            val settingsViewModel: SettingsViewModel = viewModel()
            advanceUntilIdle()

            settingsViewModel.onEvent(event = SettingsEvent.OnChangelogClicked)
            val isShown: Boolean = settingsViewModel.state.value.isChangelogVisible
            settingsViewModel.onEvent(event = SettingsEvent.OnChangelogDismissed)

            assertEquals(expected = Changelog(markdown = "# Changelog"), actual = settingsViewModel.state.value.changelog)
            assertEquals(expected = true, actual = isShown)
            assertEquals(expected = false, actual = settingsViewModel.state.value.isChangelogVisible)
        }

    @Test
    fun `a changelog that cannot be read is never shown`() =
        testScope.runTest {
            changelogRepository.changelog = Failure(errorCode = ErrorCode.FILE_READ_FAILED).asLeft()
            val settingsViewModel: SettingsViewModel = viewModel()
            advanceUntilIdle()

            settingsViewModel.onEvent(event = SettingsEvent.OnChangelogClicked)

            assertNull(actual = settingsViewModel.state.value.changelog)
            assertEquals(expected = false, actual = settingsViewModel.state.value.isChangelogVisible)
        }
}