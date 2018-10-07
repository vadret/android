package fi.kroon.vadret.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fi.kroon.vadret.R
import kotlinx.android.synthetic.main.suggestion_item.view.*
import javax.inject.Inject
import kotlin.properties.Delegates

class SuggestionAdapter @Inject constructor() : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    internal var collection: MutableList<String> by Delegates.observable(mutableListOf()) {
        _, _, _ ->
    }

    internal var clickListener: (String) -> Unit = { _ -> }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(suggestion: String, clickListener: (String) -> Unit) {
            itemView.queryCity.text = suggestion
            itemView.setOnClickListener {
                clickListener(suggestion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.suggestion_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(collection[position], clickListener)

    override fun getItemCount() = collection.size
}