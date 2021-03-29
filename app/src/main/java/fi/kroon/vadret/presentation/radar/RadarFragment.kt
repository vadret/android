package fi.kroon.vadret.presentation.radar

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.request.ImageRequest
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.changes
import com.jakewharton.rxbinding3.widget.userChanges
import fi.kroon.vadret.BuildConfig
import fi.kroon.vadret.R
import fi.kroon.vadret.databinding.RadarFragmentBinding
import fi.kroon.vadret.presentation.radar.di.DaggerRadarComponent
import fi.kroon.vadret.presentation.radar.di.RadarComponent
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_CENTER_LATITUDE
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LATITUDE_MAX
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LATITUDE_MIN
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LONGITUDE_MAX
import fi.kroon.vadret.util.DEFAULT_BOUNDINGBOX_LONGITUDE_MIN
import fi.kroon.vadret.util.DEFAULT_RADAR_FILE_EXTENSION
import fi.kroon.vadret.util.DEFAULT_RADAR_ZOOM_LEVEL
import fi.kroon.vadret.util.MAXIMUM_ZOOM_LEVEL
import fi.kroon.vadret.util.MINIMUM_ZOOM_LEVEL
import fi.kroon.vadret.util.RADAR_DEBOUNCE_MILLIS
import fi.kroon.vadret.util.WIKIMEDIA_TILE_SOURCE_URL
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.snack
import fi.kroon.vadret.util.extension.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.GroundOverlay
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

typealias RadarFile = fi.kroon.vadret.data.radar.model.File

class RadarFragment : Fragment() {

    private companion object {
        const val A_NAME = "wikimedia"
        const val A_ZOOM_MIN_LEVEL = 1
        const val A_ZOOM_MAX_LEVEL = 18
        const val A_TILE_SIZE_PIXELS = 256
        const val STATE_PARCEL_KEY = "STATE_PARCEL_KEY"
    }

    private var _binding: RadarFragmentBinding? = null
    private val binding: RadarFragmentBinding get() = _binding!!
    private lateinit var disposable: Disposable
    private var bundle: Bundle? = null
    private var stateParcel: RadarView.StateParcel? = null
    private var isConfigChangeOrProcessDeath = false

    private val component: RadarComponent by lazyAndroid {
        DaggerRadarComponent
            .factory()
            .create(context = requireContext(), coreComponent = coreComponent)
    }

    private val scheduler by lazyAndroid {
        component.provideScheduler()
    }

    private val imageLoader: ImageLoader by lazyAndroid {
        component.provideImageLoader()
    }

    private val viewModel: RadarViewModel by lazyAndroid {
        component.provideRadarViewModel()
    }

    private val onViewInitialisedSubject: PublishSubject<RadarView.Event.OnViewInitialised> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnViewInitialised()
    }

    private val onFailureHandledSubject: PublishSubject<RadarView.Event.OnFailureHandled> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnFailureHandled()
    }

    private val onRadarImageDisplayedSubject: PublishSubject<RadarView.Event.OnRadarImageDisplayed> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnRadarImageDisplayed()
    }

    private val onSeekBarStoppedSubject: PublishSubject<RadarView.Event.OnSeekBarStopped> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnSeekBarStopped()
    }

    private val onStateParcelUpdatedSubject: PublishSubject<RadarView.Event.OnStateParcelUpdated> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnStateParcelUpdated()
    }

    private val onPlayButtonStartedSubject: PublishSubject<RadarView.Event.OnPlayButtonStarted> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnPlayButtonStarted()
    }

    private val onPlayButtonStoppedSubject: PublishSubject<RadarView.Event.OnPlayButtonStopped> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnPlayButtonStopped()
    }

    private val onSeekBarResetSubject: PublishSubject<RadarView.Event.OnSeekBarReset> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnSeekBarReset()
    }

    private val onPositionUpdatedSubject: PublishSubject<RadarView.Event.OnPositionUpdated> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnPositionUpdated()
    }

    private val onSeekBarRestoredSubject: PublishSubject<RadarView.Event.OnSeekBarRestored> by lazy(
        LazyThreadSafetyMode.NONE
    ) {
        component.provideOnSeekBarRestored()
    }

    private val subscriptions: CompositeDisposable by lazyAndroid {
        component.provideCompositeDisposable()
    }

    private val defaultTileSource = XYTileSource(
        A_NAME,
        A_ZOOM_MIN_LEVEL,
        A_ZOOM_MAX_LEVEL,
        A_TILE_SIZE_PIXELS,
        DEFAULT_RADAR_FILE_EXTENSION,
        arrayOf(WIKIMEDIA_TILE_SOURCE_URL)
    )

    private fun renderError(errorCode: Int) {
        snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        onFailureHandledSubject.onNext(
            RadarView
                .Event
                .OnFailureHandled
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMapViewConfiguration()
        savedInstanceState?.let { restoredBundle ->
            if (bundle == null) {
                Timber.d("savedInstanceState restored: $restoredBundle")
                bundle = restoredBundle
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RadarFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRadarMapView()
        setupEvents()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVE INSTANCE STATE")
        outState.apply {
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(STATE_PARCEL_KEY, stateParcel)
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")
        isConfigChangeOrProcessDeath = true
    }

    private fun setupMapViewConfiguration() {
        Timber.d("setupMapViewConfiguration")
        val configuration: IConfigurationProvider = Configuration.getInstance()
        configuration.run {
            osmdroidBasePath = File(context?.cacheDir!!.absolutePath, "osmdroid")
            osmdroidTileCache = File(configuration.osmdroidBasePath.absolutePath, "tile")
            userAgentValue = BuildConfig.APPLICATION_ID
        }
    }

    override fun onResume() {
        super.onResume()
        binding.radarMapView.onResume()
        if (isConfigChangeOrProcessDeath) {
            setupEvents()
            isConfigChangeOrProcessDeath = false
        }
    }

    override fun onPause() {
        binding.radarMapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.radarMapView.onDetach()
        disposeSeekBar()
        subscriptions.clear()
        _binding = null
    }

    private fun disposeSeekBar() {
        if (::disposable.isInitialized) {
            disposable.dispose()
        }
    }

    private fun setupRadarMapView() {
        Timber.d("setupRadarMapView")
        binding.radarMapView.apply {
            setTileSource(defaultTileSource)
            isTilesScaledToDpi = true
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            maxZoomLevel = MAXIMUM_ZOOM_LEVEL
            minZoomLevel = MINIMUM_ZOOM_LEVEL
            controller.setCenter(
                GeoPoint(
                    DEFAULT_BOUNDINGBOX_CENTER_LATITUDE,
                    DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE
                )
            )
            controller.setZoom(DEFAULT_RADAR_ZOOM_LEVEL)
            setScrollableAreaLimitDouble(
                BoundingBox(
                    DEFAULT_BOUNDINGBOX_LATITUDE_MAX,
                    DEFAULT_BOUNDINGBOX_LONGITUDE_MAX,
                    DEFAULT_BOUNDINGBOX_LATITUDE_MIN,
                    DEFAULT_BOUNDINGBOX_LONGITUDE_MIN
                )
            )
        }
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {

            Observable.mergeArray(
                onViewInitialisedSubject
                    .toObservable(),
                onStateParcelUpdatedSubject
                    .toObservable(),
                onSeekBarStoppedSubject
                    .toObservable(),
                onRadarImageDisplayedSubject
                    .toObservable(),
                onSeekBarRestoredSubject
                    .toObservable(),
                onFailureHandledSubject
                    .toObservable(),
                onSeekBarResetSubject
                    .toObservable(),
                onPositionUpdatedSubject
                    .toObservable(),
                onPlayButtonStartedSubject
                    .toObservable(),
                onPlayButtonStoppedSubject
                    .toObservable(),
                binding.radarSeekBar
                    .userChanges()
                    .throttleFirst(RADAR_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                    .map { position: Int ->
                        RadarView
                            .Event
                            .OnPositionUpdated(
                                position
                            )
                    },
                binding.radarSeekBar
                    .changes()
                    .skipInitialValue()
                    .map { position: Int ->
                        RadarView
                            .Event
                            .OnRadarImageDisplayed(position)
                    },
                binding.radarPlayFab
                    .clicks()
                    .map {
                        RadarView
                            .Event
                            .OnPlayButtonClicked
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
                    RadarView
                        .Event
                        .OnViewInitialised(
                            stateParcel = bundle?.getParcelable(
                                STATE_PARCEL_KEY
                            )
                        )
                )
        }
    }

    private fun updateStateParcel(state: RadarView.State) {
        Timber.d("Updating stateParcel")
        stateParcel = RadarView.StateParcel(
            isInitialised = state.isInitialised,
            currentSeekBarIndex = state.currentSeekBarIndex,
            isSeekBarRunning = state.isSeekBarRunning,
            seekBarMax = state.seekBarMax
        )

        onStateParcelUpdatedSubject
            .onNext(
                RadarView
                    .Event
                    .OnStateParcelUpdated
            )
    }

    private fun render(viewState: RadarView.State) {
        when (viewState.renderEvent) {
            RadarView.RenderEvent.None -> Unit
            RadarView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
            RadarView.RenderEvent.SetPlayButtonToPlaying -> setPlayButtonToPlaying()
            RadarView.RenderEvent.SetPlayButtonToStopped -> setPlayButtonToStopped()
            RadarView.RenderEvent.StartSeekBar -> startSeekBar(viewState)
            RadarView.RenderEvent.StopSeekBar -> stopSeekBar()
            is RadarView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
            is RadarView.RenderEvent.DisplayRadarImage -> displayRadarImage(viewState.renderEvent.file)
            RadarView.RenderEvent.ResetSeekBar -> resetSeekBarPosition(viewState)
            RadarView.RenderEvent.RestoreSeekBarPosition -> restoreSeekBarPosition(viewState)
        }
    }

    private fun restoreSeekBarPosition(viewState: RadarView.State) {
        setRadarSeekBarPosition(viewState)
        onSeekBarRestoredSubject.onNext(
            RadarView
                .Event
                .OnSeekBarRestored
        )
    }

    private fun resetSeekBarPosition(viewState: RadarView.State) {
        setRadarSeekBarPosition(viewState)
        Timber.d("resetSeekBarPosition")
        onSeekBarResetSubject.onNext(
            RadarView
                .Event
                .OnSeekBarReset
        )
    }

    private fun setRadarSeekBarPosition(viewState: RadarView.State) {
        binding.radarSeekBar.run {
            progress = viewState.currentSeekBarIndex
            max = viewState.seekBarMax
        }
    }

    private fun setPlayButtonToPlaying() {
        binding.radarPlayFab.setImageResource(R.drawable.ic_pause_white_24dp)
        Timber.d("setPlayButtonToPlaying")
        onPlayButtonStartedSubject.onNext(
            RadarView
                .Event
                .OnPlayButtonStarted
        )
    }

    private fun setPlayButtonToStopped() {
        binding.radarPlayFab.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        Timber.d("setPlayButtonToStopped")
        onPlayButtonStoppedSubject.onNext(
            RadarView
                .Event
                .OnPlayButtonStopped
        )
    }

    private fun startSeekBar(state: RadarView.State) {
        disposable = Observable.interval(
            RADAR_DEBOUNCE_MILLIS,
            TimeUnit.MILLISECONDS
        ).map { _ ->
            binding.radarSeekBar.run {
                progress += state.seekStep
                max = state.seekBarMax
            }
        }.subscribe()
    }

    private fun stopSeekBar() {
        disposeSeekBar()
        onSeekBarStoppedSubject.onNext(
            RadarView
                .Event
                .OnSeekBarStopped
        )
    }

    private fun displayRadarImage(file: RadarFile) {
        ImageRequest
            .Builder(requireContext())
            .data(
                file
                    .formats
                    .first()
                    .link
            )
            .target(
                onStart = { _ ->
                    Unit
                },
                onSuccess = { result: Drawable ->
                    setOverlayImage(result.toBitmap())
                },
                onError = { _ ->
                    Timber.e("Image loading failed.")
                }
            )
            .build()
            .apply {
                imageLoader.enqueue(this)
            }
    }

    private fun setOverlayImage(bitmap: Bitmap) {
        val radarOverlay: GroundOverlay = GroundOverlay()
            .apply {

                image = bitmap
                setPosition(
                    GeoPoint(
                        DEFAULT_BOUNDINGBOX_LATITUDE_MAX,
                        DEFAULT_BOUNDINGBOX_LONGITUDE_MIN
                    ),
                    GeoPoint(
                        DEFAULT_BOUNDINGBOX_LATITUDE_MIN,
                        DEFAULT_BOUNDINGBOX_LONGITUDE_MAX
                    )
                )
            }

        binding.radarMapView.apply {
            overlays.clear()
            overlayManager.add(radarOverlay)
            invalidate()
        }
    }
}