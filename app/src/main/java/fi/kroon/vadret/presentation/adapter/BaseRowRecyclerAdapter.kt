package fi.kroon.vadret.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.adapter.listener.BaseRowOnClickInterface
import fi.kroon.vadret.presentation.common.model.BaseRowModel
import fi.kroon.vadret.utils.extensions.toGone
import kotlinx.android.synthetic.main.base_row.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class BaseRowRecyclerAdapter @Inject constructor() : RecyclerView.Adapter<BaseRowRecyclerAdapter.ViewHolder>() {

    internal var collection: List<BaseRowModel> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    private var clickListener: BaseRowOnClickInterface? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(baseRowModel: BaseRowModel, clickListener: BaseRowOnClickInterface?) {
            baseRowModel.iconResId?.let {
                itemView.iconView.setImageResource(it)
            }

            setTextOrMakeGone(itemView.title, baseRowModel.titleResId)
            setTextOrMakeGone(itemView.hint, baseRowModel.hintResId)

            itemView.setOnClickListener { clickListener?.onBaseRowClick(baseRowModel) }
        }

        private fun setTextOrMakeGone(textView: TextView, resId: Int?) {
            if (resId != null) {
                textView.setText(resId)
            } else {
                textView.toGone()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.base_row, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(collection[position], clickListener)

    override fun getItemCount(): Int = collection.size

    fun setListener(listener: BaseRowOnClickInterface) {
        this.clickListener = listener
    }

    fun unregisterListener() {
        this.clickListener = null
    }
}