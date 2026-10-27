package fi.kroon.vadret.di

import fi.kroon.vadret.forecastwarning.WarningViewModel
import fi.kroon.vadret.main.MainViewModel
import fi.kroon.vadret.model.AppInfo
import fi.kroon.vadret.radar.RadarViewModel
import fi.kroon.vadret.radar.di.iosRadarModule
import fi.kroon.vadret.radar.di.radarModule
import fi.kroon.vadret.settings.SettingsViewModel
import fi.kroon.vadret.weatherforecast.WeatherForecastViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class IosModulesTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private val appInfo = AppInfo(
        applicationId = "fi.kroon.vadret",
        versionName = "3.1.0-test",
        isDebug = true
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun koin(): Koin =
        koinApplication {
            modules(
                modules = sharedModules + listOf(iosPlatformModule(appInfo = appInfo), radarModule, iosRadarModule) + module {
                    factory<CoroutineDispatcher> { testDispatcher }
                }
            )
        }.koin

    @Test
    fun `every view model can be built from the ios modules`() {
        val koin: Koin = koin()

        assertNotNull(actual = koin.get<MainViewModel>())
        assertNotNull(actual = koin.get<RadarViewModel>())
        assertNotNull(actual = koin.get<SettingsViewModel>())
        assertNotNull(actual = koin.get<WarningViewModel>())
        assertNotNull(actual = koin.get<WeatherForecastViewModel>())
    }
}