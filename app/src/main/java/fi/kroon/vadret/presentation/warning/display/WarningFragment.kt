package fi.kroon.vadret.presentation.warning.display

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding3.view.clicks
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.shared.BaseFragment
import fi.kroon.vadret.presentation.warning.display.di.WarningComponent
import fi.kroon.vadret.presentation.warning.display.di.WarningScope
import fi.kroon.vadret.presentation.warning.display.model.IWarningModel
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.snack
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toObservable
import fi.kroon.vadret.util.extension.toVisible
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.warning_display_fragment.*
import timber.log.Timber

@WarningScope
class WarningFragment : BaseFragment() {

    companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
    }

    private var recyclerViewParcelable: Parcelable? = null
    private var stateParcel: WarningView.StateParcel? = null
    private var bundle: Bundle? = null

    private val cmp: WarningComponent by lazy(LazyThreadSafetyMode.NONE) {
        appComponent()
            .warningComponentBuilder()
            .build()
    }

    private val viewModel: WarningViewModel by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideWarningViewModel()
    }

    private val onViewInitialisedSubject: PublishSubject<WarningView.Event.OnViewInitialised> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnViewInitialised()
    }

    private val onProgressBarEffectStartedSubject: PublishSubject<WarningView.Event.OnProgressBarEffectStarted> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnProgressBarEffectStarted()
    }

    private val onProgressBarEffectStoppedSubject: PublishSubject<WarningView.Event.OnProgressBarEffectStopped> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnProgressBarEffectStopped()
    }

    private val onSwipedToRefreshSubject: PublishSubject<WarningView.Event.OnSwipedToRefresh> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnSwipedToRefresh()
    }

    private val onWarningListDisplayedSubject: PublishSubject<WarningView.Event.OnWarningListDisplayed> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnWarningListDisplayed()
    }

    private val onScrollPositionRestoredSubject: PublishSubject<WarningView.Event.OnScrollPositionRestored> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnScrollPositionRestored()
    }

    private val onStateParcelUpdatedSubject: PublishSubject<WarningView.Event.OnStateParcelUpdated> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnStateParcelUpdated()
    }

    private val onFailureHandledSubject: PublishSubject<WarningView.Event.OnFailureHandled> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnFailureHandled()
    }

    private val onNoWarningsIssuedDisplayedSubject: PublishSubject<WarningView.Event.OnNoWarningsIssuedDisplayed> by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideOnNoWarningsIssuedDisplayed()
    }

    private val warningAdapter: WarningAdapter by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideWarningAdapter()
    }

    private val subscriptions: CompositeDisposable by lazy(LazyThreadSafetyMode.NONE) {
        cmp.provideCompositeDisposable()
    }

    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController()
    }

    override fun layoutId(): Int = R.layout.warning_display_fragment

    override fun renderError(errorCode: Int) {
        snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        onFailureHandledSubject
            .onNext(
                WarningView.Event.OnFailureHandled
            )
    }

    override fun onAttach(context: Context) {
        Timber.d("-----BEGIN-----")
        Timber.d("ON ATTACH")
        cmp.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ON CREATE")

        savedInstanceState?.let { restoredBundle ->
            if (bundle == null) {
                Timber.d("savedInstanceState restored: $restoredBundle")
                bundle = restoredBundle
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVEINSTANCESTATE")
        outState.apply {
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(STATE_PARCEL_KEY, stateParcel)

            recyclerViewParcelable?.run {
                putParcelable(SCROLL_POSITION_KEY, this)
            } ?: warningRecyclerView?.layoutManager?.run {
                putParcelable(
                    SCROLL_POSITION_KEY,
                    (this as LinearLayoutManager)
                        .onSaveInstanceState()
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")
        bundle?.apply {
            putParcelable(
                SCROLL_POSITION_KEY,
                (warningRecyclerView.layoutManager as LinearLayoutManager)
                    .onSaveInstanceState()
            )
        }

        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscriptions.clear()
        warningRecyclerView.apply {
            adapter = null
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("ON RESUME")
        if (isConfigChangeOrProcessDeath) {
            setupEvents()
            isConfigChangeOrProcessDeath = false
        }
    }

    private fun setup() {
        setupEvents()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        warningRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = warningAdapter
            hasFixedSize()
        }
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {
            Observable.mergeArray(
                onViewInitialisedSubject
                    .toObservable(),
                onProgressBarEffectStartedSubject
                    .toObservable(),
                onProgressBarEffectStoppedSubject
                    .toObservable(),
                onFailureHandledSubject
                    .toObservable(),
                onSwipedToRefreshSubject
                    .toObservable(),
                onWarningListDisplayedSubject
                    .toObservable(),
                onScrollPositionRestoredSubject
                    .toObservable(),
                onNoWarningsIssuedDisplayedSubject
                    .toObservable(),
                onStateParcelUpdatedSubject,
                warningSwipeRefreshView
                    .refreshes()
                    .map {
                        WarningView
                            .Event
                            .OnSwipedToRefresh
                    },
                warningFilterButton
                    .clicks()
                    .map {
                        WarningView
                            .Event
                            .OnFilterButtonToggled
                    }
            ).observeOn(
                scheduler.io()
            ).compose(
                viewModel()
            ).observeOn(
                scheduler.ui()
            ).subscribe(
                ::render
            ).addTo(
                subscriptions
            )

            onViewInitialisedSubject
                .onNext(
                    WarningView.Event.OnViewInitialised(
                        stateParcel = bundle?.getParcelable(
                            STATE_PARCEL_KEY
                        )
                    )
                )
        }
    }

    private fun render(viewState: WarningView.State) =
        when (viewState.renderEvent) {
            WarningView.RenderEvent.None -> Unit
            WarningView.RenderEvent.StartProgressBarEffect -> startProgressBarEffect()
            WarningView.RenderEvent.StopProgressBarEffect -> stopProgressBarEffect()
            WarningView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
            WarningView.RenderEvent.RestoreScrollPosition -> restoreScrollPosition()
            is WarningView.RenderEvent.DisplayAggregatedFeed -> displayWarningList(viewState.renderEvent.list)
            is WarningView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
            WarningView.RenderEvent.NavigateToFilter -> navigateToFilterView()
            WarningView.RenderEvent.DisplayNoWarningsIssued -> displayNoWarningsIssued()
        }

    private fun navigateToFilterView() {
        navController
            .navigate(R.id.warningFilterDialogFragment)
    }

    private fun displayNoWarningsIssued() {
        warningDisplayNoWarningsIssued.toVisible()

        onNoWarningsIssuedDisplayedSubject
            .onNext(
                WarningView
                    .Event
                    .OnNoWarningsIssuedDisplayed
            )
    }

    private fun startProgressBarEffect() {
        Timber.d("START PROGRESS BAR")
        warningLoadingProgressBar.apply {
            toVisible()
        }

        onProgressBarEffectStartedSubject.onNext(
            WarningView.Event.OnProgressBarEffectStarted
        )
    }

    private fun stopProgressBarEffect() {
        Timber.d("STOP PROGRESS BAR")

        warningLoadingProgressBar.apply {
            toGone()
        }

        warningSwipeRefreshView.apply {
            isRefreshing = false
        }

        onProgressBarEffectStoppedSubject.onNext(
            WarningView.Event.OnProgressBarEffectStopped
        )
    }

    private fun updateStateParcel(state: WarningView.State) {
        stateParcel = WarningView.StateParcel(
            forceNet = state.forceNet,
            startRefreshing = state.startRefreshing,
            stopRefreshing = state.stopRefreshing
        )

        onStateParcelUpdatedSubject.onNext(
            WarningView.Event.OnStateParcelUpdated
        )
    }

    private fun restoreScrollPosition() {
        Timber.d("restoreScrollPosition")
        bundle?.run {
            (warningRecyclerView.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(
                    getParcelable(SCROLL_POSITION_KEY)
                )
        }

        onScrollPositionRestoredSubject.onNext(
            WarningView.Event.OnScrollPositionRestored
        )
    }

    private fun displayWarningList(list: MutableList<IWarningModel>) {
        Timber.d("DISPLAY WARNING LIST")
        warningDisplayNoWarningsIssued
            .toGone()
        warningAdapter.updateList(list)
        onWarningListDisplayedSubject.onNext(
            WarningView.Event.OnWarningListDisplayed
        )
    }
}