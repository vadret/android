package fi.kroon.vadret.presentation.weatherforecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastFeatureScope
import fi.kroon.vadret.presentation.weatherforecast.model.IWeatherForecastModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastDateItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastHeadlineModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastSplashItemModel
import fi.kroon.vadret.util.common.WeatherForecastUtil.getTemperatureColorResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getPrecipitationResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.handleWindDirection
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWsymb2ResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWsymb2IconResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWindSpeedClassResourceId
import fi.kroon.vadret.util.extension.empty
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toInvisible
import fi.kroon.vadret.util.extension.toVisible
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

    private val list: MutableList<IWeatherForecastModel> = mutableListOf()
    private val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

    override fun getItemCount(): Int = list.size

    companion object {
        const val TYPE_WEATHER_WEEKDAY = 0
        const val TYPE_WEATHER_FORECAST = 1
        const val TYPE_WEATHER_SPLASH = 2
        const val TYPE_WEATHER_HEADER = 3
        const val ROTATION_DEGREE_OFFSET = 90
    }

    inner class HeadlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: WeatherForecastHeadlineModel) {
            item.headline?.let { headlineInt ->

                val weatherDescription: String = itemView
                    .context
                    .getString(
                        getWsymb2ResourceId(
                            headlineInt
                        )
                    )

                val withString: String = itemView.context.getString(R.string.ws_with)
                val mostlyString: String = itemView.context.getString(R.string.ws_mostly)
                val windDirection: String? = item.windDirection?.let {
                    itemView.context.getString(handleWindDirection(it))
                } ?: String.empty()

                val windString: String = itemView.context.getString(R.string.ws_wind)
                val windSeverity: String = if (item.windSpeed != null) itemView.context.getString(getWindSpeedClassResourceId(item.windSpeed)) else String.empty()
                val weatherForecastHeadline = "$weatherDescription $withString $mostlyString $windSeverity $windDirection $windString."

                itemView.weatherDescription.text = weatherForecastHeadline

                val drawable = itemView.context.getDrawable(R.drawable.wsymb2_wind_direction_arrow)

                item.windDirection?.let {
                    itemView.windDirectionIcon.setImageDrawable(drawable)
                    itemView.windDirectionIcon.rotation = item.windDirection.toFloat() - ROTATION_DEGREE_OFFSET
                } ?: run {
                    itemView.windDirectionIcon.toInvisible()
                }
                itemView.toVisible()
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

            val windSpeedString = "${item.windSpeed}$meterPerSecond"
            itemView.windSpeed.text = windSpeedString

            val humidityPercentString = "${item.humidityPercent}$percentSymbol"
            itemView.humidityPercent.text = humidityPercentString

            item.precipitationCode?.let { precipitationCodeInt: Int ->
                itemView.precipitationCode.setText(getPrecipitationResourceId(precipitationCodeInt))
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
                    itemView.precipitationCode.setText(getPrecipitationResourceId(intCode))
                }
            } ?: itemView.precipitationCode.toGone()
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
            itemView.wsymb2Description.setText(getWsymb2ResourceId(item.weatherDescription))
            itemView.wsymb2Icon.setImageResource(getWsymb2IconResourceId(item.weatherIcon))
            itemView.temperature_indicator_flair.setBackgroundResource(getTemperatureColorResourceId(item.temperature))

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
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is WeatherForecastItemModel -> TYPE_WEATHER_FORECAST
            is WeatherForecastSplashItemModel -> TYPE_WEATHER_SPLASH
            is WeatherForecastHeadlineModel -> TYPE_WEATHER_HEADER
            else -> TYPE_WEATHER_WEEKDAY
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

    fun updateList(entityList: List<IWeatherForecastModel>) {
        Timber.d("WeatherForecastAdapter> UpdateList: $entityList")
        list.clear()
        list.addAll(entityList)
        notifyDataSetChanged()
    }
}