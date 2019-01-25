package fi.kroon.vadret.presentation.weatherforecast.autocomplete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastScope
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.weather_forecast_auto_complete_item.view.*
import javax.inject.Inject

@WeatherForecastScope
class AutoCompleteAdapter @Inject constructor(
    private val onAutoCompleteItemItemClickedSubject: PublishSubject<AutoCompleteItem>
) : RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder>() {

    private val list: MutableList<AutoCompleteItem> = mutableListOf()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onAutoCompleteItemItemClickedSubject
                    .onNext(list[adapterPosition])
            }
        }

        fun bind(autoCompleteItem: AutoCompleteItem) {
            val description = "${autoCompleteItem.municipality}, ${autoCompleteItem.county}"
            itemView.municipalityCounty.text = description
            itemView.city.text = autoCompleteItem.locality
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.weather_forecast_auto_complete_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    fun updateList(itemList: List<AutoCompleteItem>) {
        list.clear()
        list.addAll(itemList)
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
    }
}