package fi.kroon.vadret.presentation.weatherforecast

import android.Manifest
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding3.view.clicks
import fi.kroon.vadret.R
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.shared.BaseFragment
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastComponent
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastFeatureScope
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.snack
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toInvisible
import fi.kroon.vadret.util.extension.toObservable
import fi.kroon.vadret.util.extension.toVisible
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.weather_forecast_fragment.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions
@WeatherForecastFeatureScope
class WeatherForecastFragment : BaseFragment() {

    companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
    }

    private var stateParcel: WeatherForecastView.StateParcel? = null
    private var bundle: Bundle? = null
    private var recyclerViewParcelable: Parcelable? = null

    private val cmp: WeatherForecastComponent by lazy {
        appComponent()
            .weatherForecastComponentBuilder()
            .build()
    }

    private val viewModel: WeatherForecastViewModel by lazy {
        cmp.provideWeatherForecastViewModel()
    }

    private val onViewInitialisedSubject: PublishSubject<WeatherForecastView.Event.OnViewInitialised> by lazy {
        cmp.provideOnViewInitialisedSubject()
    }

    private val onLocationPermissionDeniedSubject: PublishSubject<WeatherForecastView.Event.OnLocationPermissionDenied> by lazy {
        cmp.provideOnLocationPermissionDeniedSubject()
    }

    private val onLocationPermissionDeniedNeverAskAgainSubject: PublishSubject<WeatherForecastView.Event.OnLocationPermissionDeniedNeverAskAgain> by lazy {
        cmp.provideOnLocationPermissionDeniedNeverAskAgainSubject()
    }

    private val onLocationPermissionGrantedSubject: PublishSubject<WeatherForecastView.Event.OnLocationPermissionGranted> by lazy {
        cmp.provideOnLocationPermissionGrantedSubject()
    }

    private val onProgressBarEffectStartedSubject: PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStarted> by lazy {
        cmp.provideOnProgressBarEffectStarted()
    }

    private val onProgressBarEffectStoppedSubject: PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStopped> by lazy {
        cmp.provideOnProgressBarEffectStopped()
    }

    private val onAutoCompleteItemClickedSubject: PublishSubject<AutoCompleteItem> by lazy {
        cmp.provideOnAutoCompleteItemClickedSubject()
    }

    private val onSearchViewDismissedSubject: PublishSubject<WeatherForecastView.Event.OnSearchViewDismissed> by lazy {
        cmp.provideOnSearchViewDismissed()
    }

    private val onFailureHandledSubject: PublishSubject<WeatherForecastView.Event.OnFailureHandled> by lazy {
        cmp.provideOnFailureHandled()
    }

    private val onWeatherListDisplayedSubject: PublishSubject<WeatherForecastView.Event.OnWeatherListDisplayed> by lazy {
        cmp.provideOnWeatherListDisplayed()
    }

    private val onScrollPositionRestoredSubject: PublishSubject<WeatherForecastView.Event.OnScrollPositionRestored> by lazy {
        cmp.provideOnScrollPositionRestored()
    }

    private val onStateParcelUpdatedSubject: PublishSubject<WeatherForecastView.Event.OnStateParcelUpdated> by lazy {
        cmp.provideOnStateParcelUpdated()
    }

    private val weatherForecastAdapter: WeatherForecastAdapter by lazy {
        cmp.provideWeatherForecastAdapter()
    }

    private val autoCompleteAdapter: AutoCompleteAdapter by lazy {
        cmp.provideAutoCompleteAdapter()
    }

    private val subscriptions: CompositeDisposable by lazy {
        cmp.provideCompositeDisposable()
    }

    private val itemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(context, RecyclerView.VERTICAL)
    }

    private val drawable: Drawable? by lazy {
        context?.getDrawable(R.drawable.search_item_divider)
    }

    override fun layoutId(): Int = R.layout.weather_forecast_fragment

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

        Timber.d("onActivityCreated: $savedInstanceState")
        setup()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")

        recyclerViewParcelable = (weatherForecastRecyclerView.layoutManager as LinearLayoutManager)
            .onSaveInstanceState()

        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON DESTROY VIEW")

        subscriptions.clear()

        weatherForecastRecyclerView.apply {
            adapter = null
        }

        autoCompleteRecyclerView.apply {
            adapter = null
        }

        hideActionBarLocalityName()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ON DESTROY")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVE INSTANCE STATE")
        outState.apply {
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(STATE_PARCEL_KEY, stateParcel)

            /**
             * If recyclerViewParcelable is available (as in not null) it gets saved
             * into the bundle.
             *
             * If user navigates away from the application the recyclerViewParcelable will
             * be null, and instead we save the scroll position via .onSavedInstanceState()
             *
             */
            recyclerViewParcelable?.run {
                putParcelable(SCROLL_POSITION_KEY, this)
            } ?: weatherForecastRecyclerView?.layoutManager?.run {
                putParcelable(
                    SCROLL_POSITION_KEY,
                    (this as LinearLayoutManager)
                        .onSaveInstanceState()
                )
            }
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
        weatherForecastRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = weatherForecastAdapter
        }
    }

    private fun setupEvents() {

        if (subscriptions.size() == 0) {
            weatherForecastSearchView
                .setOnCloseListener {
                    onSearchViewDismissedSubject.onNext(
                        WeatherForecastView
                            .Event
                            .OnSearchViewDismissed
                    )
                    true
                }

            Observable.mergeArray(
                onViewInitialisedSubject
                    .toObservable(),
                onLocationPermissionGrantedSubject
                    .toObservable(),
                onLocationPermissionDeniedSubject
                    .toObservable(),
                onLocationPermissionDeniedNeverAskAgainSubject
                    .toObservable(),
                onSearchViewDismissedSubject
                    .toObservable(),
                onWeatherListDisplayedSubject
                    .toObservable(),
                onScrollPositionRestoredSubject
                    .toObservable(),
                onFailureHandledSubject
                    .toObservable(),
                onProgressBarEffectStartedSubject
                    .toObservable(),
                onProgressBarEffectStoppedSubject
                    .toObservable(),
                onStateParcelUpdatedSubject
                    .toObservable(),
                onAutoCompleteItemClickedSubject
                    .toObservable()
                    .map { item: AutoCompleteItem ->
                        WeatherForecastView
                            .Event
                            .OnAutoCompleteItemClicked(item)
                    },
                weatherForecastLocationSearchButton
                    .clicks()
                    .map {
                        WeatherForecastView
                            .Event
                            .OnSearchButtonToggled
                    },
                weatherForecastRefresh
                    .refreshes()
                    .map {
                        WeatherForecastView
                            .Event
                            .OnSwipedToRefresh
                    },
                weatherForecastSearchView
                    .queryTextChangeEvents()
                    .skipInitialValue()
                    .map { searchEvent ->
                        when {
                            searchEvent.isSubmitted -> WeatherForecastView
                                .Event
                                .OnSearchButtonSubmitted(searchEvent.queryText.toString())
                            else -> WeatherForecastView
                                .Event
                                .OnSearchTextChanged(
                                    searchEvent.queryText.toString()
                                )
                        }
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
                    WeatherForecastView
                        .Event
                        .OnViewInitialised(
                            stateParcel = bundle?.getParcelable(
                                STATE_PARCEL_KEY
                            )
                        )
                )
        }
    }

    private fun render(viewState: WeatherForecastView.State) =
        when (viewState.renderEvent) {
            WeatherForecastView.RenderEvent.None -> Unit
            WeatherForecastView.RenderEvent.RequestLocationPermission -> onRequestLocationPermission()
            WeatherForecastView.RenderEvent.StartProgressBarEffect -> startProgressBarEffect()
            WeatherForecastView.RenderEvent.StopProgressBarEffect -> stopProgressBarEffect()
            WeatherForecastView.RenderEvent.EnableSearchView -> enableSearchView()
            WeatherForecastView.RenderEvent.RestoreScrollPosition -> restoreScrollPosition()
            is WeatherForecastView.RenderEvent.DisableSearchView -> disableSearchView(viewState.renderEvent)
            is WeatherForecastView.RenderEvent.DisplayAutoComplete -> displayAutoCompleteList(viewState.renderEvent)
            is WeatherForecastView.RenderEvent.DisplayWeatherForecast -> displayWeatherForecast(viewState.renderEvent)
            is WeatherForecastView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
            WeatherForecastView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
        }

    private fun startProgressBarEffect() {
        Timber.d("startProgressBarEffect")

        weatherForecastLoadingProgressBar.apply {
            toVisible()
        }

        onProgressBarEffectStartedSubject.onNext(
            WeatherForecastView
                .Event
                .OnProgressBarEffectStarted
        )
    }

    private fun stopProgressBarEffect() {
        Timber.d("stopProgressBarEffect")

        weatherForecastLoadingProgressBar.apply {
            toGone()
        }

        weatherForecastRefresh.apply {
            isRefreshing = false
        }

        onProgressBarEffectStoppedSubject.onNext(
            WeatherForecastView
                .Event
                .OnProgressBarEffectStopped
        )
    }

    private fun displayAutoCompleteList(renderEvent: WeatherForecastView.RenderEvent.DisplayAutoComplete) {
        autoCompleteAdapter.updateList(renderEvent.newFilteredList)
        autoCompleteRecyclerView.adapter?.run {
            renderEvent.diffResult?.dispatchUpdatesTo(this)
        }
    }

    private fun restoreScrollPosition() {
        Timber.d("restoreScrollPosition")
        bundle?.run {
            (weatherForecastRecyclerView.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(
                    getParcelable(SCROLL_POSITION_KEY)
                )
        }

        onScrollPositionRestoredSubject.onNext(
            WeatherForecastView
                .Event
                .OnScrollPositionRestored
        )
    }

    private fun updateStateParcel(state: WeatherForecastView.State) {
        stateParcel = WeatherForecastView.StateParcel(
            searchText = state.searchText,
            isSearchToggled = state.isSearchToggled,
            forceNet = state.forceNet,
            startRefreshing = state.startRefreshing,
            stopRefreshing = state.stopRefreshing
        )
        Timber.d("updateStateParcel: $stateParcel")

        onStateParcelUpdatedSubject.onNext(
            WeatherForecastView
                .Event
                .OnStateParcelUpdated
        )
    }

    private fun disableSearchView(renderEvent: WeatherForecastView.RenderEvent.DisableSearchView) {
        Timber.d("disableSearchView")

        weatherForecastLocationSearchButton.apply {
            toVisible()
        }

        weatherForecastSearchView.apply {
            toInvisible()
            setQuery(renderEvent.text, false)
        }

        autoCompleteRecyclerView.apply {
            adapter = null
            toInvisible()
        }

        onSearchViewDismissedSubject.onNext(
            WeatherForecastView
                .Event
                .OnSearchViewDismissed
        )
    }

    private fun enableSearchView() {
        autoCompleteAdapter.clearList()

        weatherForecastSearchView.apply {
            toVisible()
            isFocusable = true
            isIconified = false
            requestFocusFromTouch()
        }

        autoCompleteRecyclerView.apply {
            adapter = autoCompleteAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

            drawable?.let { res: Drawable ->
                itemDecoration
                    .setDrawable(res)
            }

            addItemDecoration(itemDecoration)
            hasFixedSize()
            toVisible()
        }

        weatherForecastLocationSearchButton.apply {
            toInvisible()
        }
    }

    private fun displayWeatherForecast(renderEvent: WeatherForecastView.RenderEvent.DisplayWeatherForecast) {
        Timber.d("Rendering weather forecast data")
        weatherForecastAdapter.updateList(renderEvent.list)
        setActionBarLocalityName(renderEvent.locality)

        onWeatherListDisplayedSubject.onNext(
            WeatherForecastView
                .Event
                .OnWeatherListDisplayed
        )
    }

    private fun onRequestLocationPermission(): Unit =
        onLocationPermissionGrantedWithPermissionCheck()

    @NeedsPermission(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionGranted() =
        onLocationPermissionGrantedSubject
            .onNext(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionGranted
            )

    @OnPermissionDenied(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionDenied() =
        onLocationPermissionDeniedSubject
            .onNext(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionDenied
            )

    @OnNeverAskAgain(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionNeverAskAgain() =
        onLocationPermissionDeniedNeverAskAgainSubject
            .onNext(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionDeniedNeverAskAgain
            )

    override fun renderError(errorCode: Int) {
        snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        onFailureHandledSubject
            .onNext(
                WeatherForecastView
                    .Event
                    .OnFailureHandled
            )
    }
}