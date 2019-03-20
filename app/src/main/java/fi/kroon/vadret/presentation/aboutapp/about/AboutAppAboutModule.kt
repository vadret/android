package fi.kroon.vadret.presentation.aboutapp.about

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import io.reactivex.subjects.PublishSubject

@Module
object AboutAppAboutModule {

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    fun provideOnInitEventSubject(): PublishSubject<AboutAppAboutView.Event.OnInit> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    fun provideOnAboutAppAboutInfoItemClickSubject(): PublishSubject<AboutInfo> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    fun provideViewState(): AboutAppAboutView.State = AboutAppAboutView.State()
}