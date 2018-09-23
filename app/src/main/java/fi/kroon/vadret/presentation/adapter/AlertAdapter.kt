package fi.kroon.vadret.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.data.alert.model.Warning
import kotlinx.android.synthetic.main.alert_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class AlertAdapter @Inject constructor() : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    internal var collection: List<Warning> by Delegates.observable(emptyList()) {
        _, _, _ -> notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(warning: Warning) {
            itemView.alertTitle.setText(handleMsgType(warning.msgType))
            itemView.areaDesc.text = warning.info.headline
            itemView.alertDescription.text = warning.info.description

            warning.info.eventCode.map {
                if (it.valueName == "system_event_level_sv-SE") {
                    itemView.alertSubject.text = it.value
                }
            }
        }

        private fun handleMsgType(string: String): Int {
            return when (string) {
                "Alert" -> R.string.alert
                else -> R.string.alert
            }
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