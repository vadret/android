package fi.kroon.vadret.radar.di

import dagger.Binds
import dagger.Module
import fi.kroon.vadret.radar.DefaultGetRadarTask
import fi.kroon.vadret.radar.DefaultRadarImageLoader
import fi.kroon.vadret.radar.GetRadarTask
import fi.kroon.vadret.radar.RadarImageLoader

@Module
internal interface RadarModule {

    @Binds
    fun bindGetRadarTask(defaultGetRadarTask: DefaultGetRadarTask): GetRadarTask

    @Binds
    fun bindRadarImageLoader(defaultRadarImageLoader: DefaultRadarImageLoader): RadarImageLoader
}