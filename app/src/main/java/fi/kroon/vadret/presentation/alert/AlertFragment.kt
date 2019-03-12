package fi.kroon.vadret.presentation.alert

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.BaseFragment
import fi.kroon.vadret.presentation.alert.di.AlertComponent
import fi.kroon.vadret.utils.Schedulers
import fi.kroon.vadret.utils.extensions.appComponent
import fi.kroon.vadret.utils.extensions.snack
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toObservable
import fi.kroon.vadret.utils.extensions.toVisible
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.alert_fragment.*
import timber.log.Timber
import javax.inject.Inject

class AlertFragment : BaseFragment() {

    companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_KEY"
        const val RESTORABLE_SCROLL_POSITION_KEY: String = "RESTORABLE_SCROLL_POSITION_KEY"
    }

    private var isConfigChangeOrProcessDeath = false
    private var stateParcel: AlertView.StateParcel? = null
    private var bundle: Bundle? = null

    private val cmp: AlertComponent by lazy {
        appComponent()
            .alertComponentBuilder()
            .build()
    }

    @Inject
    lateinit var viewModel: AlertViewModel

    @Inject
    lateinit var onViewInitialisedSubject: PublishSubject<AlertView.Event.OnViewInitialised>

    @Inject
    lateinit var onStartShimmerEffectSubject: PublishSubject<AlertView.Event.OnShimmerEffectStarted>

    @Inject
    lateinit var onShimmerEffectStoppedSubject: PublishSubject<AlertView.Event.OnShimmerEffectStopped>

    @Inject
    lateinit var onProgressBarEffectStartedSubject: PublishSubject<AlertView.Event.OnProgressBarEffectStarted>

    @Inject
    lateinit var onProgressBarEffectStoppedSubject: PublishSubject<AlertView.Event.OnProgressBarEffectStopped>

    @Inject
    lateinit var onSwipedToRefreshSubject: PublishSubject<AlertView.Event.OnSwipedToRefresh>

    @Inject
    lateinit var onAlertListDisplayedSubject: PublishSubject<AlertView.Event.OnAlertListDisplayed>

    @Inject
    lateinit var onScrollPositionRestoredSubject: PublishSubject<AlertView.Event.OnScrollPositionRestored>

    @Inject
    lateinit var onStateParcelUpdatedSubject: PublishSubject<AlertView.Event.OnStateParcelUpdated>

    @Inject
    lateinit var onFailureHandledSubject: PublishSubject<AlertView.Event.OnFailureHandled>

    @Inject
    lateinit var alertAdapter: AlertAdapter

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var subscriptions: CompositeDisposable

    override fun layoutId(): Int = R.layout.alert_fragment

    override fun renderError(errorCode: Int) {
        snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        onFailureHandledSubject
            .onNext(
                AlertView
                    .Event
                    .OnFailureHandled
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
            /*putParcelable(RESTORABLE_SCROLL_POSITION_KEY,
                (alertRecyclerView.layoutManager as LinearLayoutManager)
                    .onSaveInstanceState()
            )*/
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(STATE_PARCEL_KEY, stateParcel)
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")
        bundle?.apply {
            putParcelable(RESTORABLE_SCROLL_POSITION_KEY,
                (alertRecyclerView.layoutManager as LinearLayoutManager)
                    .onSaveInstanceState()
            )
        }

        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscriptions.clear()
        alertRecyclerView.apply {
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
        alertRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alertAdapter
            hasFixedSize()
        }
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {
            Observable.mergeArray(
                onViewInitialisedSubject
                    .toObservable(),
                onStartShimmerEffectSubject
                    .toObservable(),
                onShimmerEffectStoppedSubject
                    .toObservable(),
                onProgressBarEffectStartedSubject
                    .toObservable(),
                onProgressBarEffectStoppedSubject
                    .toObservable(),
                onFailureHandledSubject
                    .toObservable(),
                onSwipedToRefreshSubject
                    .toObservable(),
                onAlertListDisplayedSubject
                    .toObservable(),
                onScrollPositionRestoredSubject
                    .toObservable(),
                onStateParcelUpdatedSubject,
                alertSwipeRefreshView
                    .refreshes()
                    .map {
                        AlertView
                            .Event
                            .OnSwipedToRefresh
                    }
            ).observeOn(
                schedulers.io()
            ).compose(
                viewModel()
            ).observeOn(
                schedulers.ui()
            ).subscribe(
                ::render
            ).addTo(
                subscriptions
            )

            onViewInitialisedSubject
                .onNext(
                    AlertView
                        .Event
                        .OnViewInitialised(
                            stateParcel = bundle?.getParcelable(
                                STATE_PARCEL_KEY
                            )
                        )
                )
        }
    }

    private fun render(viewState: AlertView.State) =
        when (viewState.renderEvent) {
            AlertView.RenderEvent.None -> Unit
            AlertView.RenderEvent.StartShimmerEffect -> startShimmerEffect()
            AlertView.RenderEvent.StartProgressBarEffect -> startProgressBarEffect()
            AlertView.RenderEvent.StopShimmerEffect -> stopShimmerEffect()
            AlertView.RenderEvent.StopProgressBarEffect -> stopProgressBarEffect()
            AlertView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
            AlertView.RenderEvent.RestoreScrollPosition -> restoreScrollPosition()
            is AlertView.RenderEvent.DisplayAlert -> displayAlertList(viewState.renderEvent)
            is AlertView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
        }

    private fun startShimmerEffect() {
        Timber.d("startShimmerEffect")
        alertShimmerEffect.apply {
            startShimmer()
            toVisible()
        }

        onStartShimmerEffectSubject.onNext(
            AlertView
                .Event
                .OnShimmerEffectStarted
        )
    }

    private fun startProgressBarEffect() {
        Timber.d("startProgressBarEffect")
        alertLoadingProgressBar.apply {
            toVisible()
        }

        onProgressBarEffectStartedSubject.onNext(
            AlertView
                .Event
                .OnProgressBarEffectStarted
        )
    }

    private fun stopShimmerEffect() {
        Timber.d("stopShimmerEffect")
        alertShimmerEffect.apply {
            stopShimmer()
            toGone()
        }

        onShimmerEffectStoppedSubject.onNext(
            AlertView
                .Event
                .OnShimmerEffectStopped
        )
    }

    private fun stopProgressBarEffect() {
        Timber.d("stopProgressBarEffect")

        alertLoadingProgressBar.apply {
            toGone()
        }

        alertSwipeRefreshView.apply {
            isRefreshing = false
        }

        onProgressBarEffectStoppedSubject.onNext(
            AlertView
                .Event
                .OnProgressBarEffectStopped
        )
    }

    private fun updateStateParcel(state: AlertView.State) {
        stateParcel = AlertView.StateParcel(
            forceNet = state.forceNet,
            startLoading = state.startLoading,
            startRefreshing = state.startRefreshing,
            stopLoading = state.stopLoading,
            stopRefreshing = state.stopRefreshing,
            timeStamp = state.timeStamp
        )

        onStateParcelUpdatedSubject.onNext(
            AlertView
                .Event
                .OnStateParcelUpdated
        )
    }

    private fun restoreScrollPosition() {
        Timber.d("restoreScrollPosition")
        bundle?.run {
            (alertRecyclerView.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(
                    getParcelable(RESTORABLE_SCROLL_POSITION_KEY)
                )
        }

        onScrollPositionRestoredSubject.onNext(
            AlertView
                .Event
                .OnScrollPositionRestored
        )
    }

    private fun displayAlertList(renderEvent: AlertView.RenderEvent.DisplayAlert) {
        alertAdapter.updateList(renderEvent.list)

        onAlertListDisplayedSubject.onNext(
            AlertView
                .Event
                .OnAlertListDisplayed
        )
    }
}