package fi.kroon.vadret.presentation.warning.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import fi.kroon.vadret.R
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.databinding.WarningFilterDistrictChipGroupBinding
import fi.kroon.vadret.databinding.WarningFilterFeedSourceChipGroupBinding
import fi.kroon.vadret.databinding.WarningFilterTitleItemBinding
import fi.kroon.vadret.presentation.shared.IViewHolder
import fi.kroon.vadret.presentation.warning.filter.WarningFilterUtil.getChipDistrictBackgroundColor
import fi.kroon.vadret.presentation.warning.filter.WarningFilterUtil.getChipDistrictStrokeColor
import fi.kroon.vadret.presentation.warning.filter.WarningFilterUtil.getChipFeedSourceBackgroundColor
import fi.kroon.vadret.presentation.warning.filter.WarningFilterUtil.getChipFeedSourceStrokeColor
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import fi.kroon.vadret.presentation.warning.filter.model.TitleModel
import fi.kroon.vadret.util.extension.getAttribute
import timber.log.Timber

class WarningFilterAdapter constructor(
    private val onFeedSourceItemSelected: (FeedSourceOptionEntity) -> Unit,
    private val onDistrictItemSelected: (DistrictOptionEntity) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TITLE_VIEW_TYPE = 0
        const val FEED_SOURCE_VIEW_TYPE = 1
        const val DISTRICT_VIEW_TYPE = 2
    }

    private val list: MutableList<IFilterable> = mutableListOf()

    inner class TitleViewHolder(private val itemBinding: WarningFilterTitleItemBinding) : RecyclerView.ViewHolder(itemBinding.root), IViewHolder {
        override fun bind(entity: IFilterable) {
            entity as TitleModel

            Timber.d("TITLE_VIEW_TYPE VIEW HOLDER")
            with(itemBinding) {
                warningFilterTitleText.text = root.context.getString(entity.resId)
            }
        }
    }

    inner class FeedSourceViewHolder(private val itemBinding: WarningFilterFeedSourceChipGroupBinding) : RecyclerView.ViewHolder(itemBinding.root), IViewHolder {

        override fun bind(entity: IFilterable) {
            entity as FeedSourceOptionEntity

            with(itemBinding) {
                warningFilterFeedSourceChipGroup.removeAllViews()
                val chip = Chip(warningFilterFeedSourceChipGroup.context)
                chip.text = entity.name
                chip.isEnabled = true
                chip.isCheckable = true
                chip.isClickable = true
                chip.isChecked = entity.isEnabled

                val backgroundColorResource: Int = itemView.context.getAttribute(getChipFeedSourceBackgroundColor(entity.name))
                val strokeColorResource: Int = itemView.context.getAttribute(getChipFeedSourceStrokeColor(entity.name))

                chip.setChipBackgroundColorResource(backgroundColorResource)
                chip.setChipStrokeColorResource(strokeColorResource)
                chip.setChipStrokeWidthResource(R.dimen.warning_filter_chip_stroke_width)

                chip.setOnClickListener {
                    Timber.d("ITEM CLICKED ${list[adapterPosition]}")
                    onFeedSourceItemSelected(
                        list[adapterPosition] as FeedSourceOptionEntity
                    )
                }

                warningFilterFeedSourceChipGroup.addView(chip)
            }
        }
    }

    inner class DistrictViewHolder(private val itemBinding: WarningFilterDistrictChipGroupBinding) : RecyclerView.ViewHolder(itemBinding.root), IViewHolder {

        override fun bind(entity: IFilterable) {
            entity as DistrictOptionEntity

            with(itemBinding) {
                warningFilterDistrictChipGroup.removeAllViews()
                val chip = Chip(warningFilterDistrictChipGroup.context)

                val backgroundColorResource: Int = itemView.context.getAttribute(getChipDistrictBackgroundColor(category = entity.category))
                val strokeColorResource: Int = itemView.context.getAttribute(getChipDistrictStrokeColor(category = entity.category))

                chip.setChipBackgroundColorResource(backgroundColorResource)
                chip.setChipStrokeColorResource(strokeColorResource)
                chip.setChipStrokeWidthResource(R.dimen.warning_filter_chip_stroke_width)

                chip.text = entity.name
                chip.isEnabled = true
                chip.isCheckable = true
                chip.isClickable = true
                chip.isChecked = entity.isEnabled
                chip.setOnClickListener {
                    Timber.d("ITEM CLICKED ${list[adapterPosition]}")
                    onDistrictItemSelected(list[adapterPosition] as DistrictOptionEntity)
                }

                warningFilterDistrictChipGroup.addView(chip)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TITLE_VIEW_TYPE -> TitleViewHolder(
                WarningFilterTitleItemBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
            )
            FEED_SOURCE_VIEW_TYPE -> FeedSourceViewHolder(
                WarningFilterFeedSourceChipGroupBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
            )
            else -> DistrictViewHolder(
                WarningFilterDistrictChipGroupBinding
                    .inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TITLE_VIEW_TYPE -> {
                (holder as TitleViewHolder)
                    .bind(list[position] as TitleModel)
            }
            FEED_SOURCE_VIEW_TYPE -> {
                (holder as FeedSourceViewHolder)
                    .bind(list[position] as FeedSourceOptionEntity)
            }
            DISTRICT_VIEW_TYPE -> {
                (holder as DistrictViewHolder)
                    .bind(list[position] as DistrictOptionEntity)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (list[position]) {
            is DistrictOptionEntity -> DISTRICT_VIEW_TYPE
            is FeedSourceOptionEntity -> FEED_SOURCE_VIEW_TYPE
            is TitleModel -> TITLE_VIEW_TYPE
            else -> TITLE_VIEW_TYPE
        }

    override fun getItemCount(): Int = list.size

    fun updateList(entityList: List<IFilterable>, notifyDataSetChanged: Boolean = true) {
        Timber.d("WARNING FILTER ADAPTER")
        list.clear()
        list.addAll(entityList)
        Timber.d("LIST LENGTH: ${list.size}")

        if (notifyDataSetChanged) {
            notifyDataSetChanged()
        }
    }
}