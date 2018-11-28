package fi.kroon.vadret.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import fi.kroon.vadret.R
import fi.kroon.vadret.data.alert.model.Info
import fi.kroon.vadret.data.alert.model.Warning
import fi.kroon.vadret.utils.extensions.splitToList
import fi.kroon.vadret.utils.extensions.toTimeAgo
import kotlinx.android.synthetic.main.alert_item.view.*
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import kotlin.properties.Delegates

class AlertAdapter @Inject constructor() : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    internal var collection: List<Warning> by Delegates.observable(emptyList()) {
        _, _, _ -> notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(warning: Warning) {

            val timeAgo = "${itemView.context.getString(R.string.published)} ${warning.sent.toTimeAgo(itemView.context)}"

            itemView.issued.text = timeAgo
            itemView.alertDescription.text = warning.info.description

            val locale = Locale.getDefault().displayLanguage
            Timber.i("Locale currently: $locale")

            warning.info.eventCode.map {
                when (it.valueName) {
                    "system_event_priority" -> setWarningColor(it.value)
                }
            }

            setEventLevel(warning.info)

            val headlines = warning.info.headline.splitToList()
            headlines.let {
                itemView.chipGroup.removeAllViews()
                initialise(it)
            }
        }

        private fun setWarningColor(level: String) {
            when (level) {
                "1" -> itemView.rectangle_at_the_top.setBackgroundResource(R.color.warning_class_1)
                "2" -> itemView.rectangle_at_the_top.setBackgroundResource(R.color.warning_class_2)
                "3" -> itemView.rectangle_at_the_top.setBackgroundResource(R.color.warning_class_3)
                "5" -> itemView.rectangle_at_the_top.setBackgroundResource(R.color.risk_for_very_difficult_weather)
                "6" -> itemView.rectangle_at_the_top.setBackgroundResource(R.color.risk_for_fire)
                "7" -> itemView.rectangle_at_the_top.setBackgroundResource(R.color.message_high_temperature)
                else -> itemView.rectangle_at_the_top.setBackgroundResource(R.color.the_one)
            }
        }

        private fun setEventLevel(info: Info) {
            when (Locale.getDefault().displayLanguage) {
                "English" -> setEvent("system_event_level", info)
                else -> setEvent("system_event_level_sv-SE", info)
            }
        }

        private fun setEvent(key: String, info: Info) {
            info.eventCode.map {
                Timber.d("Event key is: $key")
                when (it.valueName) {
                    key -> itemView.alertTitle.text = it.value
                }
            }
        }

        private fun initialise(data: List<String>) = data.map {
            val chip = Chip(itemView.chipGroup.context)
            chip.text = it
            chip.isCheckable = false
            chip.isClickable = false
            chip.setChipBackgroundColorResource(R.color.the_one)
            itemView.chipGroup.addView(chip)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.alert_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    override fun getItemCount() = collection.size
}