package fi.kroon.vadret.presentation.radar

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import fi.kroon.vadret.BuildConfig
import fi.kroon.vadret.R
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
import fi.kroon.vadret.util.WIKIMEDIA_TILE_SOURCE_URL
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.snack
import kotlinx.android.synthetic.main.radar_fragment.radarMapView
import kotlinx.android.synthetic.main.radar_fragment.radarPlayFab
import kotlinx.android.synthetic.main.radar_fragment.radarSeekBar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.config.IConfigurationProvider
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.GroundOverlay
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.changes
import ru.ldralighieri.corbind.widget.userChanges
import timber.log.Timber
import java.io.File

typealias RadarFile = fi.kroon.vadret.data.radar.model.File

@ExperimentalCoroutinesApi
class RadarFragment : Fragment(R.layout.radar_fragment) {

    private companion object {
        const val A_NAME = "wikimedia"
        const val A_ZOOM_MIN_LEVEL = 1
        const val A_ZOOM_MAX_LEVEL = 18
        const val A_TILE_SIZE_PIXELS = 256
        const val STATE_PARCEL_KEY = "STATE_PARCEL_KEY"
    }

    private var isConfigChangeOrProcessDeath: Boolean = false
    private var bundle: Bundle? = null
    private var stateParcel: RadarView.StateParcel? = null

    private val component: RadarComponent by lazyAndroid {
        DaggerRadarComponent
            .factory()
            .create(context = requireContext(), coreComponent = coreComponent)
    }

    private val imageLoader: ImageLoader by lazyAndroid {
        component.provideImageLoader()
    }

    private val viewModel: RadarViewModel by lazyAndroid {
        component.provideRadarViewModel()
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
        viewModel.send(RadarView.Event.OnFailureHandled)
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

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")
        isConfigChangeOrProcessDeath = true
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

    private fun setupMapViewConfiguration() {
        Timber.d("setupMapViewConfiguration")
        val configuration: IConfigurationProvider = Configuration.getInstance()
        configuration.run {
            osmdroidBasePath = File(context?.cacheDir!!.absolutePath, "osmdroid")
            osmdroidTileCache = File(configuration.osmdroidBasePath.absolutePath, "tile")
            userAgentValue = BuildConfig.APPLICATION_ID
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope
            .launch {
                viewModel.viewState
                    .collect(::render)
            }
        setupRadarMapView()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()
        radarMapView.onResume()
        if (isConfigChangeOrProcessDeath) {
            setupEvents()
            isConfigChangeOrProcessDeath = false
        }
    }

    override fun onPause() {
        radarMapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radarMapView.onDetach()
    }

    private fun setupRadarMapView() {
        Timber.d("setupRadarMapView")
        radarMapView.apply {
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
        // TODO this would allow ddos like behavior since
        // it does not throttle
        radarSeekBar
            .userChanges()
            .map { position: Int ->
                viewModel.send(
                    RadarView
                        .Event
                        .OnPositionUpdated(
                            position
                        )
                )
            }.launchIn(lifecycleScope)
        radarSeekBar
            .changes()
            .drop(1)
            .map { position: Int ->
                viewModel.send(
                    RadarView
                        .Event
                        .OnRadarImageDisplayed(position)
                )
            }.launchIn(lifecycleScope)

        radarPlayFab
            .clicks()
            .map {
                viewModel.send(
                    RadarView
                        .Event
                        .OnPlayButtonClicked
                )
            }.launchIn(lifecycleScope)

        viewModel.send(
            RadarView
                .Event
                .OnViewInitialised(
                    stateParcel = bundle?.getParcelable(
                        STATE_PARCEL_KEY
                    )
                )
        )
    }

    private fun updateStateParcel(state: RadarView.State) {
        Timber.d("Updating stateParcel")
        stateParcel = RadarView.StateParcel(
            isInitialised = state.isInitialised,
            currentSeekBarIndex = state.currentSeekBarIndex,
            isSeekBarRunning = state.isSeekBarRunning,
            seekBarMax = state.seekBarMax
        )

        viewModel
            .send(
                RadarView
                    .Event
                    .OnStateParcelUpdated
            )
    }

    private fun render(viewState: RadarView.State) {
        when (viewState.renderEvent) {
            RadarView.RenderEvent.Idle -> Unit
            RadarView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
            RadarView.RenderEvent.SetPlayButtonToPlaying -> setPlayButtonToPlaying()
            RadarView.RenderEvent.SetPlayButtonToStopped -> setPlayButtonToStopped()
            is RadarView.RenderEvent.StartSeekBar -> Unit //startSeekBar(viewState)
            RadarView.RenderEvent.StopSeekBar -> Unit // stopSeekBar()
            is RadarView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
            is RadarView.RenderEvent.DisplayRadarImage -> displayRadarImage(
                viewState.renderEvent.file,
                viewState.currentSeekBarIndex,
                viewState.seekBarMax
            )
            RadarView.RenderEvent.ResetSeekBar -> Unit //resetSeekBarPosition(viewState)
            RadarView.RenderEvent.RestoreSeekBarPosition -> Unit //restoreSeekBarPosition(viewState)
        }
    }

    private fun startSeekBar(viewState: RadarView.State) {
        //viewState.currentSeekBarIndex

        radarSeekBar?.run {
            progress = viewState.currentSeekBarIndex
            max = viewState.seekBarMax
        }
    }

    private fun restoreSeekBarPosition(viewState: RadarView.State) {
        //setRadarSeekBarPosition(viewState)
        viewModel.send(
            RadarView
                .Event
                .OnSeekBarRestored
        )
    }

    private fun resetSeekBarPosition(viewState: RadarView.State) {
        //setRadarSeekBarPosition(viewState)
        Timber.d("resetSeekBarPosition")
        viewModel.send(
            RadarView
                .Event
                .OnSeekBarReset
        )
    }

    private fun setRadarSeekBarPosition(viewState: RadarView.State) {
        radarSeekBar?.run {
            progress = viewState.currentSeekBarIndex
            max = viewState.seekBarMax
        }
    }

    private fun setPlayButtonToPlaying() {
        radarPlayFab.setImageResource(R.drawable.ic_pause_white_24dp)
        Timber.d("setPlayButtonToPlaying")
        viewModel.send(
            RadarView
                .Event
                .OnPlayButtonStarted
        )
    }

    private fun setPlayButtonToStopped() {
        radarPlayFab.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        Timber.d("setPlayButtonToStopped")
        viewModel.send(
            RadarView
                .Event
                .OnPlayButtonStopped
        )
    }

    private fun displayRadarImage(file: RadarFile, currentSeekBarIndex: Int, seekBarMax: Int) {

        radarSeekBar?.run {
            progress = currentSeekBarIndex
            max = seekBarMax
        }

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

        viewModel.send(RadarView.Event.OnRadarImageDisplayed(currentSeekBarIndex))
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

        radarMapView?.apply {
            overlays.clear()
            overlayManager.add(radarOverlay)
            invalidate()
        }
    }
}