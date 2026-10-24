package fi.kroon.vadret.weatherforecast

import androidx.recyclerview.widget.DiffUtil
import fi.kroon.vadret.weatherforecast.model.IWeatherForecastModel

class WeatherForecastDiffUtil constructor(
    private val oldList: List<IWeatherForecastModel>,
    private val newList: List<IWeatherForecastModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}