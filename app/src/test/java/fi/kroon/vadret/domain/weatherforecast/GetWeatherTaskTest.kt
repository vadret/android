package fi.kroon.vadret.domain.weatherforecast

/*
class GetWeatherTaskTest : BaseUnitTest() {

    @Mock
    private lateinit var mockWeatherForecastRepository: WeatherForecastRepository

    private lateinit var testGetWeatherTask: GetWeatherTask

    @Mock
    private lateinit var mockWeather: Weather

    @Mock
    private lateinit var mockWeatherOut: WeatherOut

    @Mock
    private lateinit var mockLocationEntity: Location

    @Before
    fun setup() {
        testGetWeatherTask = GetWeatherTask(mockWeatherForecastRepository)
    }

    @Test
    fun `task returns expected objects`() {
        doReturn(getResultEither())
            .`when`(mockWeatherForecastRepository)
            .get(mockWeatherOut)

        val mappedList = WeatherForecastMapper.toWeatherForecastModel(mockWeather.timeSeries!!, mockLocationEntity)

        testGetWeatherTask(mockWeatherOut)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<Weather>> && it.b == mappedList }
    }

    @Test
    fun `task throws internal error`() {
        doReturn(getFailure())
            .`when`(mockWeatherForecastRepository)
            .get(mockWeatherOut)

        testGetWeatherTask(mockWeatherOut)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is WeatherForecastFailure.NoWeatherAvailable }
    }

    @Test
    fun `task throws exception`() {
        doReturn(throwException())
            .`when`(mockWeatherForecastRepository)
            .get(mockWeatherOut)

        testGetWeatherTask(mockWeatherOut)
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is Failure.TaskFailure }
    }

    private fun throwException(): Single<Either<Failure, Weather>> =
        Single.error<Either<Failure, Weather>>(Exception("failure"))

    private fun getResultEither(): Single<Either<Failure, Weather>> = Single.just(
        Either.Right(mockWeather)
    )

    private fun getFailure(): Single<Either<Failure, Weather>> = Single.just(
        WeatherForecastFailure.NoWeatherAvailable().asLeft()
    )
}*/