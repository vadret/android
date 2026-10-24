package fi.kroon.vadret.radar.di

import fi.kroon.vadret.radar.DefaultGetRadarTask
import fi.kroon.vadret.radar.DefaultRadarImageLoader
import fi.kroon.vadret.radar.GetRadarTask
import fi.kroon.vadret.radar.RadarImageLoader
import fi.kroon.vadret.radar.RadarViewModel
import fi.kroon.vadret.radar.projection.DefaultRadarReprojection
import fi.kroon.vadret.radar.projection.RadarReprojection
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val radarModule: Module = module {
    factoryOf(::DefaultGetRadarTask) { bind<GetRadarTask>() }
    single<RadarReprojection> { DefaultRadarReprojection() }
    single<RadarImageLoader> {
        DefaultRadarImageLoader(
            context = get(),
            radarReprojection = get(),
            httpClient = inject()
        )
    }
    factoryOf(::RadarViewModel)
}