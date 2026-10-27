package fi.kroon.vadret.radar.di

import fi.kroon.vadret.radar.DefaultGetDeviceLocationTask
import fi.kroon.vadret.radar.DefaultGetRadarTask
import fi.kroon.vadret.radar.DefaultGetWindForecastTask
import fi.kroon.vadret.radar.DefaultRadarImageLoader
import fi.kroon.vadret.radar.GetDeviceLocationTask
import fi.kroon.vadret.radar.GetRadarTask
import fi.kroon.vadret.radar.GetWindForecastTask
import fi.kroon.vadret.radar.RadarImageLoader
import fi.kroon.vadret.radar.RadarViewModel
import fi.kroon.vadret.radar.projection.DefaultRadarReprojection
import fi.kroon.vadret.radar.projection.RadarReprojection
import fi.kroon.vadret.radar.wind.DefaultWindFieldRenderer
import fi.kroon.vadret.radar.wind.WindFieldRenderer
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val radarModule: Module = module {
    factoryOf(::DefaultGetDeviceLocationTask) { bind<GetDeviceLocationTask>() }
    factoryOf(::DefaultGetRadarTask) { bind<GetRadarTask>() }
    factoryOf(::DefaultGetWindForecastTask) { bind<GetWindForecastTask>() }
    single<WindFieldRenderer> { DefaultWindFieldRenderer(coroutineDispatcher = get()) }
    single<RadarReprojection> { DefaultRadarReprojection() }
    single<RadarImageLoader> {
        DefaultRadarImageLoader(
            platformContext = get(),
            radarReprojection = get(),
            httpClient = inject()
        )
    }
    factoryOf(::RadarViewModel)
}