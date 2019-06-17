package fi.kroon.vadret.presentation.warning.display

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.warning.display.model.IWarningModel
import fi.kroon.vadret.presentation.warning.display.model.WarningModel
import fi.kroon.vadret.util.DAY_IN_MILLIS
import fi.kroon.vadret.util.HOUR_IN_MILLIS
import fi.kroon.vadret.util.MINUTE_IN_MILLIS
import fi.kroon.vadret.util.extension.empty
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toVisible
import kotlinx.android.synthetic.main.warning_fragment.view.*
import kotlinx.android.synthetic.main.warning_item.view.*
import timber.log.Timber
import javax.inject.Inject

class WarningAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val WARNING_VIEW_TYPE = 0
    }

    private val list: MutableList<IWarningModel> = mutableListOf()

    inner class WarningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(entity: IWarningModel) {
            entity as WarningModel

            Timber.d("RENDER: $entity")
            with(itemView) {
                warningDisplayTitle.text = entity.headline
                warningDisplayFeedSourceColor.setBackgroundResource(entity.backgroundResourceId)

                if (entity.preamble != String.empty()) {
                    warningDisplayPreamble.text = entity.preamble
                    warningDisplayPreamble.toVisible()
                } else {
                    warningDisplayPreamble.toGone()
                }

                warningDisplayDescription.text = entity.bodyText
                warningDisplayFeedSource.text = entity.senderName
                warningDisplayPublished.text = setMomentString(
                    entity.elapsedTime.resId,
                    entity.elapsedTime.diffInMillis
                )
            }
        }

        private fun setMomentString(@StringRes resourceInt: Int, timeDiffMillis: Long): String =
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WarningViewHolder =
        when (viewType) {
            WARNING_VIEW_TYPE -> WarningViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.warning_item,
                        parent,
                        false
                    )
            )
            else -> WarningViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.warning_item,
                        parent,
                        false
                    )
            )
        }

    override fun getItemViewType(position: Int): Int =
        when (list[position]) {
            is WarningModel -> WARNING_VIEW_TYPE
            else -> WARNING_VIEW_TYPE
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            WARNING_VIEW_TYPE -> (holder as WarningViewHolder)
                .bind(list[position] as WarningModel)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(entityList: List<IWarningModel>) {
        Timber.d("LIST RECEIVED: ${list.size}")
        list.clear()
        list.addAll(entityList)
        notifyDataSetChanged()
    }
}