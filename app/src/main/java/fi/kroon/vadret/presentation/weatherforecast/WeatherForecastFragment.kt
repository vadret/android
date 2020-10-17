package fi.kroon.vadret.presentation.weatherforecast

import android.Manifest
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.nominatim.model.Locality
import fi.kroon.vadret.presentation.main.MainActivity
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecast.di.DaggerWeatherForecastComponent
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastComponent
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toInvisible
import fi.kroon.vadret.util.extension.toVisible
import kotlinx.android.synthetic.main.weather_forecast_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import ru.ldralighieri.corbind.appcompat.queryTextChangeEvents
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import ru.ldralighieri.corbind.view.clicks
import timber.log.Timber

@ExperimentalCoroutinesApi
@FlowPreview
@RuntimePermissions
class WeatherForecastFragment : Fragment(R.layout.weather_forecast_fragment) {

    private companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
    }

    private var isConfigChangeOrProcessDeath = false
    private var stateParcel: WeatherForecastView.StateParcel? = null
    private var bundle: Bundle? = null
    private var recyclerViewParcelable: Parcelable? = null

    private val component: WeatherForecastComponent by lazyAndroid {
        DaggerWeatherForecastComponent
            .factory()
            .create(context = requireContext(), coreComponent = coreComponent)
    }

    private val viewModel: WeatherForecastViewModel by lazyAndroid {
        component.provideWeatherForecastViewModel()
    }

    private val eventChannel: ConflatedBroadcastChannel<WeatherForecastView.Event> by lazyAndroid {
        component.provideEventChannel()
    }

    private val weatherForecastAdapter: WeatherForecastAdapter by lazyAndroid {
        component.provideWeatherForecastAdapter()
    }

    private val autoCompleteAdapter: AutoCompleteAdapter by lazyAndroid {
        component.provideAutoCompleteAdapter()
    }

    private val itemDecoration: DividerItemDecoration by lazyAndroid {
        DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
    }

    private val drawable: Drawable? by lazyAndroid {
        ContextCompat.getDrawable(requireContext(), R.drawable.search_item_divider)
    }

    override fun onAttach(context: Context) {
        Timber.d("ON ATTACH -- WEATHER FORECAST")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ON CREATE -- WEATHER FORECAST")
        savedInstanceState?.let { _bundle: Bundle ->
            if (bundle == null) {
                Timber.d("savedInstanceState restored: $_bundle")
                bundle = _bundle
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("ON VIEW CREATED -- WEATHER FORECAST")
        setup()

        lifecycleScope
            .launch {
                viewModel()
                    .flowOn(Dispatchers.IO)
                    .collect(::render)
            }
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
        Timber.d("ON STOP -- WEATHER FORECAST")

        recyclerViewParcelable = (weatherForecastRecyclerView.layoutManager as LinearLayoutManager)
            .onSaveInstanceState()
        hideActionBarLocalityName()

        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON DESTROY VIEW -- WEATHER FORECAST")

        weatherForecastRecyclerView.apply {
            adapter = null
        }

        autoCompleteRecyclerView.apply {
            adapter = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("ON DESTROY -- WEATHER FORECAST")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVE INSTANCE STATE -- WEATHER FORECAST")
        outState.apply {
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
        Timber.d("ON RESUME -- WEATHER FORECAST")
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
            hasFixedSize()
        }
    }

    private fun setupEvents() {

        weatherForecastSearchView
            .setOnCloseListener {

                eventChannel.offer(
                    WeatherForecastView
                        .Event
                        .OnSearchViewDismissed
                )

                true
            }

        weatherForecastLocationSearchButton
            .clicks()
            .map {
                eventChannel
                    .offer(
                        WeatherForecastView.Event.OnSearchButtonToggled
                    )
            }.launchIn(lifecycleScope)

        weatherForecastRefresh
            .refreshes()
            .map {
                eventChannel.offer(WeatherForecastView.Event.OnSwipedToRefresh)
            }.launchIn(lifecycleScope)

        weatherForecastSearchView
            .queryTextChangeEvents()
            .debounce(200)
            .drop(1)
            .map { searchEvent ->
                when {
                    searchEvent.isSubmitted -> {
                        eventChannel.offer(
                            WeatherForecastView
                                .Event
                                .OnSearchButtonSubmitted(searchEvent.queryText.toString())
                        )
                    }
                    else -> {
                        eventChannel.offer(
                            WeatherForecastView
                                .Event
                                .OnSearchTextChanged(
                                    searchEvent.queryText.toString()
                                )
                        )
                    }
                }
            }.launchIn(lifecycleScope)

        eventChannel.offer(
            WeatherForecastView
                .Event
                .OnViewInitialised(
                    stateParcel = bundle?.getParcelable(
                        STATE_PARCEL_KEY
                    )
                )
        )
    }

    private fun render(viewState: WeatherForecastView.State) =
        when (viewState.renderEvent) {
            WeatherForecastView.RenderEvent.Idle -> Unit
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

        eventChannel.offer(
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

        eventChannel.offer(
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

        eventChannel.offer(
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

        eventChannel.offer(
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

        eventChannel.offer(
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

        autoCompleteRecyclerView
            .apply {
                adapter = autoCompleteAdapter
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                drawable?.let { res: Drawable ->
                    itemDecoration
                        .setDrawable(res)
                }
                addItemDecoration(itemDecoration)
                hasFixedSize()
                visibility = View.VISIBLE
            }

        weatherForecastLocationSearchButton.visibility = View.INVISIBLE
    }

    private fun displayWeatherForecast(renderEvent: WeatherForecastView.RenderEvent.DisplayWeatherForecast) {
        Timber.d("Rendering weather forecast data")
        weatherForecastAdapter.updateList(renderEvent.list)
        setActionBarLocalityName(renderEvent.locality)

        eventChannel.offer(
            WeatherForecastView
                .Event
                .OnWeatherListDisplayed
        )
    }

    private fun onRequestLocationPermission(): Unit =
        onLocationPermissionGrantedWithPermissionCheck()

    @NeedsPermission(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionGranted() {
        eventChannel
            .offer(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionGranted
            )
    }

    @OnPermissionDenied(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionDenied() {
        eventChannel
            .offer(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionDenied
            )
    }

    @OnNeverAskAgain(value = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun onLocationPermissionNeverAskAgain() {
        eventChannel
            .offer(
                WeatherForecastView
                    .Event
                    .OnLocationPermissionDeniedNeverAskAgain
            )
    }

    private fun renderError(errorCode: Int) {
        // TODO snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        eventChannel
            .offer(
                WeatherForecastView
                    .Event
                    .OnFailureHandled
            )
    }

    private fun hideActionBarLocalityName() =
        (requireActivity() as MainActivity)
            .hideLocalityActionBar()

    private fun setActionBarLocalityName(locality: Locality) =
        (requireActivity() as MainActivity)
            .setLocalityActionBar(locality)
}