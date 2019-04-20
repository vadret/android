package fi.kroon.vadret.presentation.weatherforecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastFeatureScope
import fi.kroon.vadret.presentation.weatherforecast.model.BaseWeatherForecastModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastDateItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastHeadlineModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastSplashItemModel
import fi.kroon.vadret.utils.extensions.toGone
import fi.kroon.vadret.utils.extensions.toInvisible
import fi.kroon.vadret.utils.extensions.toVisible
import kotlinx.android.synthetic.main.weather_forecast_date_item.view.*
import kotlinx.android.synthetic.main.weather_forecast_headline_item.view.*
import kotlinx.android.synthetic.main.weather_forecast_item.view.*
import kotlinx.android.synthetic.main.weather_forecast_splash_item.view.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@WeatherForecastFeatureScope
class WeatherForecastAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list: MutableList<BaseWeatherForecastModel> = mutableListOf()
    private val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

    override fun getItemCount(): Int = list.size

    companion object {
        const val TYPE_WEATHER_WEEKDAY = 0
        const val TYPE_WEATHER_FORECAST = 1
        const val TYPE_WEATHER_SPLASH = 2
        const val TYPE_WEATHER_HEADER = 3
    }

    inner class HeadlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: WeatherForecastHeadlineModel) {
            item.headline?.let { headlineInt ->
                itemView.weatherHeadline.setText(
                    handleWsymb2Description(headlineInt)
                )
            }
        }
    }

    inner class WeekdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: WeatherForecastDateItemModel) {
            itemView.date.text = item.date.format(formatter)
            itemView.weekDay.text = item.date
                .dayOfWeek
                .getDisplayName(
                    TextStyle.FULL_STANDALONE,
                    Locale.getDefault()
                )
                .toUpperCase()
        }
    }

    inner class ForecastSplashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                Timber.d("ForecastSplashViewHolder: Item $adapterPosition clicked of ${list.size}")
            }
        }

        fun bind(item: WeatherForecastSplashItemModel) {

            val percentSymbol: String = itemView.context.getString(R.string.percent)
            val degreeSymbol: String = itemView.context.getString(R.string.degree)
            val meterPerSecond: String = itemView.context.getString(R.string.m_s)

            val tempString = "${item.temperature}$degreeSymbol"
            itemView.currentTemperature.text = tempString

            val leftTemperature = "${item.temperature}$degreeSymbol"
            itemView.temperatureLeft.text = leftTemperature

            item.feelsLikeTemperature?.let {
                val rightTemperature = "${item.feelsLikeTemperature}$degreeSymbol"
                itemView.temperatureRight.text = rightTemperature
            } ?: run {
                itemView.temperatureRight.toGone()
            }

            itemView.windSpeed.text = item.windSpeed.toString()

            item.windDirection?.let { windDirectionDegree: Double ->
                val directionString: String = itemView.context.getString(handleWindDirection(windDirectionDegree))
                itemView.windDirection.text = directionString
            }
            val windSpeedString = "${item.windSpeed}$meterPerSecond"
            itemView.windSpeed.text = windSpeedString

            val humidityPercentString = "${item.humidityPercent}$percentSymbol"
            itemView.humidityPercent.text = humidityPercentString

            item.precipitationCode?.let { precipitationCodeInt: Int ->
                itemView.precipitationCode.setText(handlePrSort(precipitationCodeInt))
            } ?: itemView.precipitationCode.toGone()

            if (item.sunriseDateTime != null && item.sunsetDateTime != null) {
                val format = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                itemView.sunriseDateTime.text = item.sunriseDateTime.toLocalTime().format(format)
                itemView.sunsetDateTime.text = item.sunsetDateTime.toLocalTime().format(format)
            } else {
                itemView.sunriseDateTime.setText(R.string.sun_wont_rise_today)
                itemView.sunsetDateTime.setText(R.string.sun_wont_set_today)
            }

            item.precipitationCode?.let { intCode ->
                if (intCode > 0) {
                    itemView.precipitationCode.setText(handlePrSort(intCode))
                }
            } ?: itemView.precipitationCode.toGone()
        }
    }

    private fun handleWsymb2Icon(index: Int): Int =
        when (index) {
            1 -> R.drawable.wsymb2_clear_sky
            2 -> R.drawable.wsymb2_nearly_clear_sky
            3 -> R.drawable.wsymb2_variable_cloudiness
            4 -> R.drawable.wsymb2_halfclear_sky
            5 -> R.drawable.wsymb2_cloudy_sky
            6 -> R.drawable.wsymb2_overcast
            7 -> R.drawable.wsymb2_fog
            8 -> R.drawable.wsymb2_light_rain_showers
            9 -> R.drawable.wsymb2_moderate_rain_showers
            10 -> R.drawable.wsymb2_heavy_rain_showers
            11 -> R.drawable.wsymb2_thunderstorm
            12 -> R.drawable.wsymb2_light_sleet_showers
            13 -> R.drawable.wsymb2_moderate_sleet_showers
            14 -> R.drawable.wsymb2_heavy_sleet_showers
            15 -> R.drawable.wsymb2_light_snow_showers
            16 -> R.drawable.wsymb2_moderate_snow_showers
            17 -> R.drawable.wsymb2_heavy_snow_showers
            18 -> R.drawable.wsymb2_light_rain
            19 -> R.drawable.wsymb2_moderate_rain
            20 -> R.drawable.wsymb2_heavy_rain
            21 -> R.drawable.wsymb2_thunder
            22 -> R.drawable.wsymb2_light_sleet
            23 -> R.drawable.wsymb2_moderate_sleet
            24 -> R.drawable.wsymb2_heavy_sleet
            25 -> R.drawable.wsymb2_light_snowfall
            26 -> R.drawable.wsymb2_moderate_snowfall
            27 -> R.drawable.wsymb2_heavy_snowfall
            else -> {
                R.drawable.wsymb2_clear_sky
            }
        }

    private fun handleWsymb2Description(index: Int): Int =
        when (index) {
            1 -> R.string.wsymb2_clear_sky
            2 -> R.string.wsymb2_nearly_clear_sky
            3 -> R.string.wsymb2_variable_cloudiness
            4 -> R.string.wsymb2_halfclear_sky
            5 -> R.string.wsymb2_cloudy_sky
            6 -> R.string.wsymb2_overcast
            7 -> R.string.wsymb2_fog
            8 -> R.string.wsymb2_light_rain_showers
            9 -> R.string.wsymb2_moderate_rain_showers
            10 -> R.string.wsymb2_heavy_rain_showers
            11 -> R.string.wsymb2_thunderstorm
            12 -> R.string.wsymb2_light_sleet_showers
            13 -> R.string.wsymb2_moderate_sleet_showers
            14 -> R.string.wsymb2_heavy_sleet_showers
            15 -> R.string.wsymb2_light_snow_showers
            16 -> R.string.wsymb2_moderate_snow_showers
            17 -> R.string.wsymb2_heavy_snow_showers
            18 -> R.string.wsymb2_light_rain
            19 -> R.string.wsymb2_moderate_rain
            20 -> R.string.wsymb2_heavy_rain
            21 -> R.string.wsymb2_thunder
            22 -> R.string.wsymb2_light_sleet
            23 -> R.string.wsymb2_moderate_sleet
            24 -> R.string.wsymb2_heavy_sleet
            25 -> R.string.wsymb2_light_snowfall
            26 -> R.string.wsymb2_moderate_snowfall
            27 -> R.string.wsymb2_heavy_snowfall
            else -> {
                R.string.wsymb2_clear_sky
            }
        }

    private fun handlePrSort(prSort: Int): Int =
        when (prSort) {
            0 -> R.string.prsort_no_precipitation
            1 -> R.string.prsort_snow
            2 -> R.string.prsort_snow_and_rain
            3 -> R.string.prsort_rain
            4 -> R.string.prsort_drizzle
            5 -> R.string.prsort_freezing_rain
            6 -> R.string.prsort_freezing_drizzle
            else -> R.string.prsort_no_precipitation
        }

    private fun handleWindDirection(degree: Double): Int =
        when {

            // North: 0.0 - 11.24
            (degree <= 11.25 && 0.0 <= degree) -> R.string.north

            // North, North East: 11.25 - 33.74
            (degree <= 33.75 && 11.25 < degree) -> R.string.north_north_east

            // North East:  33.75 - 56.24
            (degree <= 56.25 && 33.75 < degree) -> R.string.north_east

            // East, North East:
            (degree <= 78.75 && 56.25 < degree) -> R.string.east_north_east

            // East:
            (degree <= 101.25 && 78.75 < degree) -> R.string.east

            // East, South East:
            (degree <= 123.75 && 101.25 < degree) -> R.string.east_south_east

            // South East
            (degree <= 146.25 && 123.75 < degree) -> R.string.south_east

            // South, South East
            (degree <= 168.75 && 146.25 < degree) -> R.string.south_south_east

            // South
            (degree <= 191.25 && 168.75 < degree) -> R.string.south

            // South, South West
            (degree <= 213.75 && 191.25 < degree) -> R.string.south_south_west

            // South West
            (degree <= 236.25 && 213.75 < degree) -> R.string.south_west

            // West, South West
            (degree <= 258.75 && 236.25 < degree) -> R.string.west_south_west

            // West
            (degree <= 281.25 && 258.75 < degree) -> R.string.west

            // West, North West
            (degree <= 303.75 && 281.25 < degree) -> R.string.west_north_west

            // North West
            (degree <= 326.25 && 303.75 < degree) -> R.string.north_west

            // North, North West
            (degree <= 348.75 && 326.25 < degree) -> R.string.north_north_west

            // North: 348.75 - 360.0
            (degree <= 360 && 348.75 < degree) -> R.string.north

            else -> {
                Timber.e("DisplayError: Wind direction is outside of range: $degree")
                R.string.empty_wind_direction
            }
        }

    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                Timber.d("ForecastViewHolder: Item $adapterPosition clicked of ${list.size}")
            }
        }

        fun bind(item: WeatherForecastItemModel) {
            itemView.time.text = item.time
            itemView.temperature.text = item.temperature.toString()
            itemView.wsymb2Description.setText(handleWsymb2Description(item.weatherDescription))
            itemView.wsymb2Icon.setImageResource(handleWsymb2Icon(item.weatherIcon))

            handleIndicatorFlair(item.temperature)

            item.feelsLikeTemperature?.let {
                itemView.feelsLikeTemperature.text = item.feelsLikeTemperature
                itemView.feelsLike.toVisible()
                itemView.feelsLikeTemperature.toVisible()
                itemView.feelsLikeTempUnit.toVisible()
            } ?: run {
                itemView.feelsLikeTemperature.toInvisible()
                itemView.feelsLike.toInvisible()
                itemView.feelsLikeTempUnit.toInvisible()
            }
        }

        private fun handleIndicatorFlair(temperature: Double) {
            when {

                // Range: < -10.0  -- Example: -22.0
                (-10.0 >= temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_0)

                // Range: -5.0 - -10.0  -- Example: -7.5
                (-5.0 >= temperature && temperature > -10.0) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_1)

                // Range: -2.5 - -5.0   -- Example: -3.67
                (-2.5 >= temperature && temperature > -5.0) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_2)

                // Range: -0.5 -> -2.5   -- Example: -1.75 || -0.5
                (-0.5 >= temperature && temperature > -2.5) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_3)

                // Range: 0.0 -> -0.5    -- Example: -0.25 || 0.0
                (temperature <= 0.0 && -0.5 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_4)

                // Range: 0.0 -> 1.0     -- Example: 0.5 || 1.0
                (temperature <= 1.0 && 0.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_5)

                // Range: 1.0 -> 2.0     -- Example: 1.5 || 2.0
                (temperature <= 2.0 && 1.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_6)

                // Range: 2.0 -> 3.0     -- Example: 2.5 || 3.0
                (temperature <= 3.0 && 2.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_7)

                // Range: 3.0 -> 4.0     -- Example: 3.9 || 4.0
                (temperature <= 4.0 && 3.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_8)

                // Range: 4.0 -> 5.0     -- Example: 4.0 || 5.0
                (temperature <= 5.0 && 4.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_9)

                // Range: 5.0 -> 10.0    -- Example: 7.5 || 10.0
                (temperature <= 10.0 && 5.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_10)

                // Range: 10.0 -> 15.0   -- Example: 12.5 || 15.0
                (temperature <= 15.0 && 10.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_11)

                // Range: 15.0 -> 20.0   -- Example: 17.5 || 20.0
                (temperature <= 20.0 && 15.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_12)

                // Range: 20.0 -> 22.5   -- Example: 21.1 || 22.5
                (temperature <= 22.5 && 20.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_13)

                // Range: 22.5 -> 25.0   -- Example: 24.0 || 25.0
                (temperature <= 25.0 && 22.5 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_14)

                // Range: 25.0 - 30.0    -- Example: 27.0 ||  30.0
                (temperature <= 30.0 && 25.0 < temperature) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_15)

                // Range: > 30.0         -- Example: 31.0 | 50.0
                (temperature > 30.0) -> itemView.temperature_indicator_flair.setBackgroundResource(R.color.color_gradient_16)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is WeatherForecastItemModel -> WeatherForecastAdapter.TYPE_WEATHER_FORECAST
            is WeatherForecastSplashItemModel -> WeatherForecastAdapter.TYPE_WEATHER_SPLASH
            is WeatherForecastHeadlineModel -> WeatherForecastAdapter.TYPE_WEATHER_HEADER
            else -> WeatherForecastAdapter.TYPE_WEATHER_WEEKDAY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WEATHER_WEEKDAY -> WeekdayViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.weather_forecast_date_item, parent, false)
            )
            TYPE_WEATHER_SPLASH -> ForecastSplashViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.weather_forecast_splash_item, parent, false)
            )
            TYPE_WEATHER_HEADER -> HeadlineViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.weather_forecast_headline_item, parent, false)
            )
            else -> ForecastViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.weather_forecast_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_WEATHER_SPLASH -> (holder as ForecastSplashViewHolder)
                .bind(list[position] as WeatherForecastSplashItemModel)
            TYPE_WEATHER_FORECAST -> (holder as ForecastViewHolder)
                .bind(list[position] as WeatherForecastItemModel)
            TYPE_WEATHER_WEEKDAY -> (holder as WeekdayViewHolder)
                .bind(list[position] as WeatherForecastDateItemModel)
            TYPE_WEATHER_HEADER -> (holder as HeadlineViewHolder)
                .bind(list[position] as WeatherForecastHeadlineModel)
        }
    }

    fun updateList(entityList: List<BaseWeatherForecastModel>) {
        Timber.d("WeatherForecastAdapter> UpdateList: $entityList")
        list.clear()
        list.addAll(entityList)
        notifyDataSetChanged()
    }
}