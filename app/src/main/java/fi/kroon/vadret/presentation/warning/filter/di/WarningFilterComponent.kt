package fi.kroon.vadret.presentation.warning.filter.di

import dagger.Subcomponent
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.presentation.warning.filter.WarningFilterAdapter
import fi.kroon.vadret.presentation.warning.filter.WarningFilterDialogFragment
import fi.kroon.vadret.presentation.warning.filter.WarningFilterView
import fi.kroon.vadret.presentation.warning.filter.WarningFilterViewModel
import fi.kroon.vadret.util.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WarningFilterModule::class
    ]
)
@WarningFilterScope
interface WarningFilterComponent {

    fun inject(warningFilterDialogFragment: WarningFilterDialogFragment)

    /**
     *  ViewModel
     */
    fun provideWarningFilterViewModel(): WarningFilterViewModel

    /**
     *  PublishSubject
     */
    fun provideOnFeedSourceItemSelectedSubject(): PublishSubject<FeedSourceOptionEntity>
    fun provideOnDistrictItemSelectedSubject(): PublishSubject<DistrictOptionEntity>
    fun provideOnViewInitialised(): PublishSubject<WarningFilterView.Event.OnViewInitialised>
    fun provideOnFilterOptionsDisplayed(): PublishSubject<WarningFilterView.Event.OnFilterOptionsDisplayed>

    /**
     *  Adapter
     */
    fun provideWarningFilterAdapter(): WarningFilterAdapter

    /**
     *  Composite Disposable
     */
    fun provideCompositeDisposable(): CompositeDisposable

    /**
     *  Scheduler
     */
    fun provideScheduler(): Scheduler

    @Subcomponent.Builder
    interface Builder {
        fun warningFilterModule(module: WarningFilterModule): Builder
        fun build(): WarningFilterComponent
    }
}