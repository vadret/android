package fi.kroon.vadret.presentation.aboutapp.library

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import io.reactivex.subjects.PublishSubject
import javax.inject.Named

@Module
object AboutAppLibraryModule {

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    fun provideOnInitEventSubject(): PublishSubject<AboutAppLibraryView.Event.OnInit> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    @Named("projectUrl")
    fun provideOnProjectUrlClickSubject(): PublishSubject<Library> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    @Named("sourceUrl")
    fun provideOnSourceUrlClickSubject(): PublishSubject<Library> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    @Named("licenseUrl")
    fun provideOnLicenseUrlClickSubject(): PublishSubject<Library> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppFeatureScope
    fun provideViewState(): AboutAppLibraryView.State = AboutAppLibraryView.State()
}