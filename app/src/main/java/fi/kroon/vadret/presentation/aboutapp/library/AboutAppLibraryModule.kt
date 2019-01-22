package fi.kroon.vadret.presentation.aboutapp.library

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.library.local.LibraryEntity
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppScope
import io.reactivex.subjects.PublishSubject
import javax.inject.Named

@Module
object AboutAppLibraryModule {

    @Provides
    @JvmStatic
    @AboutAppScope
    fun provideOnInitEventSubject(): PublishSubject<AboutAppLibraryView.Event.OnInit> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppScope
    @Named("projectUrl")
    fun provideOnProjectUrlClickSubject(): PublishSubject<LibraryEntity> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppScope
    @Named("sourceUrl")
    fun provideOnSourceUrlClickSubject(): PublishSubject<LibraryEntity> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppScope
    @Named("licenseUrl")
    fun provideOnLicenseUrlClickSubject(): PublishSubject<LibraryEntity> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @AboutAppScope
    fun provideViewState(): AboutAppLibraryView.State = AboutAppLibraryView.State()
}