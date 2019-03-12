package fi.kroon.vadret.presentation.alert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.alert.model.BaseWarningItemModel
import fi.kroon.vadret.presentation.alert.model.WarningItemItemModel
import fi.kroon.vadret.utils.DAY_IN_MILLIS
import fi.kroon.vadret.utils.HOUR_IN_MILLIS
import fi.kroon.vadret.utils.MINUTE_IN_MILLIS
import kotlinx.android.synthetic.main.alert_item.view.*
import javax.inject.Inject

class AlertAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ALERT = 0
    }

    private val list: MutableList<BaseWarningItemModel> = mutableListOf()

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(warningItemItemModel: WarningItemItemModel): Unit =
            with(warningItemItemModel) {
                itemView.issued.text = setTimeToMoments(
                    issuedAtStringResource,
                    diffInMillis
                )
                itemView.alertTitle.text = eventLevelTitle
                itemView.alertDescription.text = description
                itemView.alertStartColorBar.setBackgroundResource(backgroundColorResource)
                headlineItems.let { list: List<String> ->
                    itemView.chipGroup.removeAllViews()
                    setChip(list)
                }
            }

        private fun setTimeToMoments(@StringRes resourceInt: Int, timeDiffMillis: Long): String =
            when (resourceInt) {
                R.string.in_the_future -> itemView.context.getString(R.string.in_the_future)
                R.string.moments_ago -> itemView.context.getString(R.string.moments_ago)
                R.string.a_minute_ago -> itemView.context.getString(R.string.a_minute_ago)
                R.string.minutes_ago -> {
                    "${(timeDiffMillis / MINUTE_IN_MILLIS)} ${itemView.context.getString(R.string.minutes_ago)}"
                }
                R.string.an_hour_ago -> itemView.context.getString(R.string.an_hour_ago)
                R.string.hours_ago -> {
                    "${timeDiffMillis / HOUR_IN_MILLIS} ${itemView.context.getString(R.string.hours_ago)}"
                }
                R.string.yesterday -> itemView.context.getString(R.string.yesterday)
                else -> {
                    "${timeDiffMillis / DAY_IN_MILLIS} ${itemView.context.getString(R.string.days_ago)}"
                }
            }

        private fun setChip(data: List<String>) = data.map { string: String ->
            val chip = Chip(itemView.chipGroup.context)
            chip.text = string
            chip.isCheckable = false
            chip.isClickable = false
            chip.setChipBackgroundColorResource(R.color.light_purple_shade)
            itemView.chipGroup.addView(chip)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder =
        when (viewType) {
            VIEW_TYPE_ALERT -> AlertViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.alert_item,
                        parent,
                        false
                    )
            )
            // FIXME for later support for multiple view types
            else -> AlertViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.alert_item,
                        parent,
                        false
                    )
            )
        }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            // FIXME for later support for multiple view types
            is WarningItemItemModel -> AlertAdapter.VIEW_TYPE_ALERT
            else -> AlertAdapter.VIEW_TYPE_ALERT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_ALERT -> (holder as AlertViewHolder)
                .bind(list[position] as WarningItemItemModel)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(itemList: List<BaseWarningItemModel>) {
        list.clear()
        list.addAll(itemList)
        notifyDataSetChanged()
    }
}