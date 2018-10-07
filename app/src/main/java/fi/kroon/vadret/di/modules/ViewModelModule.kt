package fi.kroon.vadret.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import fi.kroon.vadret.presentation.viewmodel.AboutViewModel
import fi.kroon.vadret.presentation.viewmodel.AlertViewModel
import fi.kroon.vadret.presentation.viewmodel.LocationViewModel
import fi.kroon.vadret.presentation.viewmodel.RadarViewModel
import fi.kroon.vadret.presentation.viewmodel.WeatherViewModel

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AlertViewModel::class)
    abstract fun bindsAlertViewModel(alertViewModel: AlertViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RadarViewModel::class)
    abstract fun bindsRadarViewModel(radarViewModel: RadarViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    abstract fun bindsWeatherViewModel(weatherViewModel: WeatherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocationViewModel::class)
    abstract fun bindsLocationViewModel(locationViewModel: LocationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    abstract fun bindsAboutViewModel(aboutViewModel: AboutViewModel): ViewModel
}