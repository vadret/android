package fi.kroon.vadret.presentation

// TODO - Refactor
/*
class RadarFragment  {

    companion object {
        // fixme add
    }

    override fun layoutId() = R.layout.radar_fragment

    @Inject
    lateinit var schedulers: Schedulers

    lateinit var playRadarDisposable: Disposable

    private var isToggled: Boolean = false
    private lateinit var radarViewModel: RadarViewModel
    private lateinit var mapView: MapView
    private lateinit var radarSeekbar: SeekBar
    private lateinit var radarPlay: FloatingActionButton

    // FIXME wikimedia -> companion object const val
    private val DEFAULT_TILE_SOURCE = XYTileSource("wikimedia", 1, 18, 256, DEFAULT_RADAR_FILE_EXTENSION, arrayOf(WIKIMEDIA_TILE_SOURCE_URL))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cmp.inject(this)
        radarViewModel = viewModel(viewModelFactory) {}
        initialiseMapConfiguration()
    }

    private fun initialiseMapConfiguration() {
        val configuration = Configuration.getInstance()
        val basePath = java.io.File(context?.cacheDir!!.absolutePath, "osmdroid")
        configuration.osmdroidBasePath = basePath
        val tileCache = java.io.File(configuration.osmdroidBasePath.absolutePath, "tile")
        configuration.osmdroidTileCache = tileCache
        configuration.userAgentValue = BuildConfig.APPLICATION_ID
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseMapView()
        initialise()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        if (::playRadarDisposable.isInitialized) {
            pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDetach()
    }

    private fun initialiseMapView() {
        mapView = view?.findViewById(R.id.mapView)!!
        mapView.setTileSource(DEFAULT_TILE_SOURCE)
        mapView.isTilesScaledToDpi = true
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false) // TODO deprecated
        mapView.maxZoomLevel = MAXIMUM_ZOOM_LEVEL
        mapView.minZoomLevel = MINIMUM_ZOOM_LEVEL
        mapView.controller.setCenter(GeoPoint(DEFAULT_BOUNDINGBOX_CENTER_LATITUDE, DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE))
        mapView.controller.setZoom(DEFAULT_RADAR_ZOOM_LEVEL)
        mapView.setScrollableAreaLimitDouble(BoundingBox(DEFAULT_BOUNDINGBOX_LATITUDE_MAX, DEFAULT_BOUNDINGBOX_LONGITUDE_MAX, DEFAULT_BOUNDINGBOX_LATITUDE_MIN, DEFAULT_BOUNDINGBOX_LONGITUDE_MIN))
    }

    fun initialise() {
        loadRadar(RadarRequest())
    }

    private fun loadRadar(radarRequest: RadarRequest) {
        radarViewModel
            .get(radarRequest)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .onErrorReturn { Either.Left(RadarFailure.NoRadarAvailable()) }
            .subscribe(::radarHandler)
            .addTo(subscriptions)
    }

    private fun radarHandler(data: Either<Failure, Radar>) {
        data.either(::handleFailure, ::handleRadar)
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.IOException -> renderFailure(R.string.io_exception)
            is RadarFailure.NoRadarAvailable -> renderFailure(R.string.no_radar_available)
            is Failure.NetworkException -> renderFailure(R.string.network_failure)
            is Failure.NetworkOfflineFailure -> renderFailure(R.string.no_network_available)
        }
    }

    private fun handleRadar(radar: Radar) {
        radarSeekbar = view?.findViewById(R.id.radarSeekBar)!!
        radarSeekbar.max = radar.files.size - FILE_COUNT_OFFSET
        radarSeekbar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    Timber.d("onProgressChanged: ${radar.files[progress]}")
                    if (::playRadarDisposable.isInitialized)
                        renderRadar(radar.files[progress])
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    Timber.d("Tracking started.")
                    startNoAutoPlay()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    Timber.d("Tracking stopped.")
                    pauseNoAutoPlay()
                }
            }
        )
        radarPlay = view?.findViewById(R.id.radarPlay)!!
        radarPlay.setOnClickListener {
            Timber.d("Clicked nextRadar!")
            if (isToggled) {
                pause()
            } else {
                start()
            }
        }
    }

    private fun pauseNoAutoPlay() {
        if (!isToggled) {
            if (::playRadarDisposable.isInitialized)
                playRadarDisposable.dispose()
            radarPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        }
    }

    private fun startNoAutoPlay() {
        if (!isToggled) {
            radarPlay.setImageResource(R.drawable.ic_pause_white_24dp)
            playRadarDisposable = playRadar()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe()
        }
    }

    private fun pause() {
        if (::playRadarDisposable.isInitialized)
            playRadarDisposable.dispose()
        isToggled = false
        radarPlay.setImageResource(R.drawable.ic_play_arrow_white_24dp)
    }

    private fun start() {
        isToggled = true
        radarPlay.setImageResource(R.drawable.ic_pause_white_24dp)
        playRadarDisposable = playRadar()
            .subscribe()
    }

    private fun playRadar() = Observable
        .interval(DEFAULT_RADAR_INTERVAL, TimeUnit.MILLISECONDS)
        .map { nextRadar() }

    private fun nextRadar() =
        if (radarSeekbar.progress < radarSeekbar.max && radarSeekbar.progress >= 0) {
            radarSeekbar.progress += 1
            Timber.d("Progress: ${radarSeekbar.progress}")
        } else {
            radarSeekbar.progress = 0
            Timber.d("Progress: ${radarSeekbar.progress}")
        }

    private fun renderRadar(file: File) {

        Timber.d("Radar data: $file")
        Picasso.get().load(file.formats[0].link).into(
            object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Timber.d("Image loading failed.")
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    if (playRadarDisposable.isDisposed or subscriptions.isDisposed) {
                        Timber.d("Caller disposed assets. Return")
                        return
                    }

                    mapView.overlays.clear()
                    radarDate.text = OffsetDateTime.parse(file.formats[0].updated).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    Timber.d("Image loaded successfully.")
                    val radarOverlay = GroundOverlay2()
                    radarOverlay.image = bitmap
                    radarOverlay.setPosition(
                        GeoPoint(DEFAULT_BOUNDINGBOX_LATITUDE_MAX, DEFAULT_BOUNDINGBOX_LONGITUDE_MIN),
                        GeoPoint(DEFAULT_BOUNDINGBOX_LATITUDE_MIN, DEFAULT_BOUNDINGBOX_LONGITUDE_MAX)
                    )
                    mapView.overlayManager.add(radarOverlay)
                    mapView.invalidate()
                }
            }
        )
    }

    private fun renderFailure(@StringRes message: Int) = Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
}*/