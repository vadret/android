package fi.kroon.vadret.domain.radar

/*
@RunWith(MockitoJUnitRunner::class)
class GetRadarImageUrlServiceTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private val listOfFiles: List<File> by lazy {
        listOf(mockFile)
    }

    private val radarRequest: RadarRequest =
        RadarRequest(
            year = "2019",
            month = "7",
            date = "18",
            format = "png",
            timeZone = "GMT"
        )

    private val timeStamp: Long = 1563136512344L

    private val index = 0

    private val testDateTimeUtil: TestDateTimeUtil = TestDateTimeUtil()

    private val data: GetRadarImageUrlService.Data =
        GetRadarImageUrlService.Data(index = 0, timeStamp = timeStamp)

    private lateinit var testGetRadarImageUrlService: GetRadarImageUrlService

    @Mock
    private lateinit var mockGetRadarImageUrlTask: GetRadarImageUrlTask

    @Mock
    private lateinit var mockGetRadarDiskCacheTask: GetRadarDiskCacheTask

    @Mock
    private lateinit var mockGetRadarMemoryCacheTask: GetRadarMemoryCacheTask

    @Mock
    private lateinit var mockSetRadarDiskCacheTask: SetRadarDiskCacheTask

    @Mock
    private lateinit var mockSetRadarMemoryCacheTask: SetRadarMemoryCacheTask

    @Mock
    private lateinit var mockRadar: Radar

    @Mock
    private lateinit var mockFile: File

    @Before
    fun setup() {
        testGetRadarImageUrlService = GetRadarImageUrlService(
            getRadarImageUrlTask = mockGetRadarImageUrlTask,
            getRadarDiskCacheTask = mockGetRadarDiskCacheTask,
            getRadarMemoryCacheTask = mockGetRadarMemoryCacheTask,
            setRadarDiskCacheTask = mockSetRadarDiskCacheTask,
            setRadarMemoryCacheTask = mockSetRadarMemoryCacheTask,
            iDateTimeUtil = testDateTimeUtil
        )
    }

    @Test
    fun `get response successfully, no errors`() {

        doReturn(getRadarImageUrlTaskEither())
            .`when`(mockGetRadarImageUrlTask)
            .invoke(radarRequest)

        doReturn(getRadarDiskCacheTaskEither())
            .`when`(mockGetRadarDiskCacheTask)
            .invoke()

        doReturn(getRadarMemoryCacheTaskEither())
            .`when`(mockGetRadarMemoryCacheTask)
            .invoke()

        doReturn(listOfFiles)
            .`when`(mockRadar).files

        testGetRadarImageUrlService(index = index, timeStamp = timeStamp + FIFTEEN_MINUTES_IN_MILLIS)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right<GetRadarImageUrlService.Data>
            }
    }

    private fun getRadarMemoryCacheTaskEither(): Single<Either<Failure, Radar>> =
        mockRadar
            .asRight()
            .asSingle()

    private fun getRadarDiskCacheTaskEither(): Single<Either<Failure, Radar>> =
        mockRadar
            .asRight()
            .asSingle()

    private fun getRadarImageUrlTaskEither(): Single<Either<Failure, Radar>> =
        mockRadar
            .asRight()
            .asSingle()
}*/