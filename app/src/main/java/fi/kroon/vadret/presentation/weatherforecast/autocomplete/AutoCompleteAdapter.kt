package fi.kroon.vadret.presentation.weatherforecast.autocomplete

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.databinding.WeatherForecastAutoCompleteItemBinding
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView

class AutoCompleteAdapter constructor(
    private val callback: AutoCompleteAdapterCallback
) : RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder>() {

    private val list: MutableList<AutoCompleteItem> = mutableListOf()

    inner class ViewHolder(private val itemBinding: WeatherForecastAutoCompleteItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.root
                .setOnClickListener {
                    callback
                        .onAutoCompleteItemClicked(
                            WeatherForecastView
                                .Event
                                .OnAutoCompleteItemClicked(list[adapterPosition])
                        )
                }
        }

        fun bind(autoCompleteItem: AutoCompleteItem) {
            val description = "${autoCompleteItem.municipality}, ${autoCompleteItem.county}"
            itemBinding.municipalityCounty.text = description
            itemBinding.city.text = autoCompleteItem.locality
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            WeatherForecastAutoCompleteItemBinding
                .inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size

    fun updateList(itemList: List<AutoCompleteItem>) {
        list.clear()
        list.addAll(itemList)
    }

    fun clearList() {
        list.clear()
    }
}