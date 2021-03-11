package fi.kroon.vadret.presentation.weatherforecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.databinding.WeatherForecastDateItemBinding
import fi.kroon.vadret.databinding.WeatherForecastHeadlineItemBinding
import fi.kroon.vadret.databinding.WeatherForecastItemBinding
import fi.kroon.vadret.databinding.WeatherForecastSplashItemBinding
import fi.kroon.vadret.presentation.weatherforecast.model.IWeatherForecastModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastDateItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastHeadlineModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastItemModel
import fi.kroon.vadret.presentation.weatherforecast.model.WeatherForecastSplashItemModel
import fi.kroon.vadret.util.common.WeatherForecastUtil.getPrecipitationResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getTemperatureColorResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWindSpeedClassResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWsymb2IconResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.getWsymb2ResourceId
import fi.kroon.vadret.util.common.WeatherForecastUtil.handleWindDirection
import fi.kroon.vadret.util.extension.empty
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toInvisible
import fi.kroon.vadret.util.extension.toVisible
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

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

    inner class WeatherForecastHeadlineViewHolder(private val itemBinding: WeatherForecastHeadlineItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
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
                val windDirection: String = item.windDirection?.let {
                    itemView.context.getString(handleWindDirection(it))
                } ?: String.empty()

                val windString: String = itemView.context.getString(R.string.ws_wind)
                val windSeverity: String = if (item.windSpeed != null) itemView.context.getString(getWindSpeedClassResourceId(item.windSpeed)) else String.empty()
                val weatherForecastHeadline = "$weatherDescription $withString $mostlyString $windSeverity $windDirection $windString."

                itemBinding.weatherDescription.text = weatherForecastHeadline

                val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.wsymb2_wind_direction_arrow)

                item.windDirection?.let {
                    itemBinding.windDirectionIcon.setImageDrawable(drawable)
                    itemBinding.windDirectionIcon.rotation = item.windDirection.toFloat() + ROTATION_DEGREE_OFFSET
                } ?: run {
                    itemBinding.windDirectionIcon.toInvisible()
                }
                itemBinding.root.toVisible()
            }
        }
    }

    // weather_forecast_date_item
    inner class WeekdayViewHolder(private val itemBinding: WeatherForecastDateItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: WeatherForecastDateItemModel) {
            itemBinding.date.text = item.date.format(formatter)
            itemBinding.weekDay.text = item.date
                .dayOfWeek
                .getDisplayName(
                    TextStyle.FULL_STANDALONE,
                    Locale.getDefault()
                ).toUpperCase()
        }
    }

    inner class ForecastSplashViewHolder(private val itemBinding: WeatherForecastSplashItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root.setOnClickListener {
                Timber.d("ForecastSplashViewHolder: Item $adapterPosition clicked of ${list.size}")
            }
        }

        fun bind(item: WeatherForecastSplashItemModel) {

            val percentSymbol: String = itemView.context.getString(R.string.percent)
            val degreeSymbol: String = itemView.context.getString(R.string.degree)
            val meterPerSecond: String = itemView.context.getString(R.string.m_s)

            val tempString = "${item.temperature}$degreeSymbol"
            itemBinding.currentTemperature.text = tempString

            val leftTemperature = "${item.temperature}$degreeSymbol"
            itemBinding.temperatureLeft.text = leftTemperature

            item.feelsLikeTemperature?.let {
                val rightTemperature = "${item.feelsLikeTemperature}$degreeSymbol"
                itemBinding.temperatureRight.text = rightTemperature
            } ?: run {
                itemBinding.temperatureRight.toGone()
            }

            val windSpeedString = "${item.windSpeed}$meterPerSecond"
            itemBinding.windSpeed.text = windSpeedString

            val humidityPercentString = "${item.humidityPercent}$percentSymbol"
            itemBinding.humidityPercent.text = humidityPercentString

            item.precipitationCode?.let { precipitationCodeInt: Int ->
                itemBinding.precipitationCode.setText(getPrecipitationResourceId(precipitationCodeInt))
            } ?: itemBinding.precipitationCode.toGone()

            if (item.sunriseDateTime != null && item.sunsetDateTime != null) {
                val format = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                itemBinding.sunriseDateTime.text = item.sunriseDateTime.toLocalTime().format(format)
                itemBinding.sunsetDateTime.text = item.sunsetDateTime.toLocalTime().format(format)
            } else {
                itemBinding.sunriseDateTime.setText(R.string.sun_wont_rise_today)
                itemBinding.sunsetDateTime.setText(R.string.sun_wont_set_today)
            }

            item.precipitationCode?.let { intCode ->
                if (intCode > 0) {
                    itemBinding.precipitationCode.setText(getPrecipitationResourceId(intCode))
                }
            } ?: itemBinding.precipitationCode.toGone()
        }
    }

    inner class WeatherForecastViewHolder(private val itemBinding: WeatherForecastItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root.setOnClickListener {
                Timber.d("ForecastViewHolder: Item $adapterPosition clicked of ${list.size}")
            }
        }

        fun bind(item: WeatherForecastItemModel) {
            itemBinding.time.text = item.time
            itemBinding.temperature.text = item.temperature.toString()
            itemBinding.wsymb2Description.setText(getWsymb2ResourceId(item.weatherDescription))
            itemBinding.wsymb2Icon.setImageResource(getWsymb2IconResourceId(item.weatherIcon))
            itemBinding.temperatureIndicatorFlair.setBackgroundResource(getTemperatureColorResourceId(item.temperature))

            item.feelsLikeTemperature?.let {
                itemBinding.feelsLikeTemperature.text = item.feelsLikeTemperature
                itemBinding.feelsLike.toVisible()
                itemBinding.feelsLikeTemperature.toVisible()
                itemBinding.feelsLikeTempUnit.toVisible()
            } ?: run {
                itemBinding.feelsLikeTemperature.toInvisible()
                itemBinding.feelsLike.toInvisible()
                itemBinding.feelsLikeTempUnit.toInvisible()
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
                WeatherForecastDateItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_WEATHER_SPLASH -> ForecastSplashViewHolder(
                WeatherForecastSplashItemBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
            )
            TYPE_WEATHER_HEADER -> WeatherForecastHeadlineViewHolder(
                WeatherForecastHeadlineItemBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
            )
            else -> WeatherForecastViewHolder(
                WeatherForecastItemBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_WEATHER_SPLASH ->
                (holder as ForecastSplashViewHolder)
                    .bind(list[position] as WeatherForecastSplashItemModel)
            TYPE_WEATHER_FORECAST ->
                (holder as WeatherForecastViewHolder)
                    .bind(list[position] as WeatherForecastItemModel)
            TYPE_WEATHER_WEEKDAY ->
                (holder as WeekdayViewHolder)
                    .bind(list[position] as WeatherForecastDateItemModel)
            TYPE_WEATHER_HEADER ->
                (holder as WeatherForecastHeadlineViewHolder)
                    .bind(list[position] as WeatherForecastHeadlineModel)
        }
    }

    fun updateList(entityList: List<IWeatherForecastModel>) {
        Timber.d("WeatherForecastAdapter> UpdateList: $entityList")
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(
                WeatherForecastDiffUtil(
                    oldList = list,
                    newList = entityList
                )
            )

        list.clear()
        list.addAll(entityList)
        diffResult.dispatchUpdatesTo(this)
    }
}